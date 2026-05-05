"""DeepLabV3+ architecture for IAN segmentation.

Architecture summary
--------------------
DeepLabV3+ (Chen et al., 2018) combines an encoder with atrous convolutions
(ASPP) and a lightweight decoder for sharp boundary recovery.

Why DeepLabV3+ for IAN?
- ASPP captures multi-scale context, important for nerve segments at varying
  widths across panoramic/CBCT modalities.
- Lighter decoder allows faster iteration and lower GPU memory vs full U-Net.
- Strong baseline for curved tubular structures.

GPU memory: ~4 GB for 512x512 input, batch size 4.
Expected metrics (planning target): Dice ≥ 0.86, IoU ≥ 0.76.
"""

from __future__ import annotations

import torch
import torch.nn as nn
import torch.nn.functional as F


class _ASPP(nn.Module):
    """Atrous Spatial Pyramid Pooling."""

    def __init__(self, in_channels: int, out_channels: int) -> None:
        super().__init__()
        rates = [1, 6, 12, 18]
        self.convs = nn.ModuleList(
            [
                nn.Sequential(
                    nn.Conv2d(in_channels, out_channels, 3, padding=r, dilation=r, bias=False),
                    nn.BatchNorm2d(out_channels),
                    nn.ReLU(inplace=True),
                )
                for r in rates
            ]
        )
        self.global_avg = nn.Sequential(
            nn.AdaptiveAvgPool2d(1),
            nn.Conv2d(in_channels, out_channels, 1, bias=False),
            nn.BatchNorm2d(out_channels),
            nn.ReLU(inplace=True),
        )
        self.project = nn.Sequential(
            nn.Conv2d(out_channels * 5, out_channels, 1, bias=False),
            nn.BatchNorm2d(out_channels),
            nn.ReLU(inplace=True),
            nn.Dropout(0.5),
        )

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        size = x.shape[2:]
        parts = [conv(x) for conv in self.convs]
        global_feat = F.interpolate(self.global_avg(x), size=size, mode="bilinear", align_corners=True)
        parts.append(global_feat)
        return self.project(torch.cat(parts, dim=1))


class _SimpleEncoder(nn.Module):
    """Lightweight 4-layer encoder (no pre-trained backbone dependency)."""

    def __init__(self, in_channels: int, base: int = 64) -> None:
        super().__init__()
        self.layer1 = self._block(in_channels, base)
        self.layer2 = self._block(base, base * 2, stride=2)
        self.layer3 = self._block(base * 2, base * 4, stride=2)
        self.layer4 = self._block(base * 4, base * 8, stride=2, dilation=2)
        self.low_level_channels = base * 2
        self.high_level_channels = base * 8

    @staticmethod
    def _block(in_ch: int, out_ch: int, stride: int = 1, dilation: int = 1) -> nn.Sequential:
        return nn.Sequential(
            nn.Conv2d(in_ch, out_ch, 3, stride=stride, padding=dilation, dilation=dilation, bias=False),
            nn.BatchNorm2d(out_ch),
            nn.ReLU(inplace=True),
            nn.Conv2d(out_ch, out_ch, 3, padding=1, bias=False),
            nn.BatchNorm2d(out_ch),
            nn.ReLU(inplace=True),
        )

    def forward(self, x: torch.Tensor):
        x1 = self.layer1(x)
        x2 = self.layer2(x1)
        x3 = self.layer3(x2)
        x4 = self.layer4(x3)
        return x2, x4  # low-level, high-level


class DeepLabV3Plus(nn.Module):
    """DeepLabV3+ with a lightweight encoder (no external pre-trained weights required)."""

    def __init__(self, in_channels: int = 1, num_classes: int = 1, base_features: int = 64) -> None:
        super().__init__()
        f = base_features
        self.encoder = _SimpleEncoder(in_channels, f)
        self.aspp = _ASPP(f * 8, 256)

        self.low_level_proj = nn.Sequential(
            nn.Conv2d(self.encoder.low_level_channels, 48, 1, bias=False),
            nn.BatchNorm2d(48),
            nn.ReLU(inplace=True),
        )
        self.decoder = nn.Sequential(
            nn.Conv2d(256 + 48, 256, 3, padding=1, bias=False),
            nn.BatchNorm2d(256),
            nn.ReLU(inplace=True),
            nn.Conv2d(256, 256, 3, padding=1, bias=False),
            nn.BatchNorm2d(256),
            nn.ReLU(inplace=True),
            nn.Conv2d(256, num_classes, 1),
        )

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        input_size = x.shape[2:]
        low_level, high_level = self.encoder(x)

        aspp_out = self.aspp(high_level)
        aspp_out = F.interpolate(aspp_out, size=low_level.shape[2:], mode="bilinear", align_corners=True)

        low_feat = self.low_level_proj(low_level)
        fused = torch.cat([aspp_out, low_feat], dim=1)

        out = self.decoder(fused)
        return F.interpolate(out, size=input_size, mode="bilinear", align_corners=True)
