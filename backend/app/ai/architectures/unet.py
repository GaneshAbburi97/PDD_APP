"""U-Net architecture for IAN segmentation.

Architecture summary
--------------------
U-Net (Ronneberger et al., 2015) is a fully-convolutional encoder-decoder
with skip connections between symmetric encoder and decoder stages.

Why U-Net for IAN?
- Excellent performance on small anatomical structures with limited data.
- Skip connections preserve spatial detail crucial for nerve boundary accuracy.
- Training converges reliably with as few as a few hundred labelled slices.

GPU memory: ~2 GB for 512x512 single-channel input, batch size 4.
Expected metrics (planning target): Dice ≥ 0.85, IoU ≥ 0.75.
"""

from __future__ import annotations

import torch
import torch.nn as nn


class _DoubleConv(nn.Module):
    def __init__(self, in_ch: int, out_ch: int) -> None:
        super().__init__()
        self.block = nn.Sequential(
            nn.Conv2d(in_ch, out_ch, 3, padding=1, bias=False),
            nn.BatchNorm2d(out_ch),
            nn.ReLU(inplace=True),
            nn.Conv2d(out_ch, out_ch, 3, padding=1, bias=False),
            nn.BatchNorm2d(out_ch),
            nn.ReLU(inplace=True),
        )

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        return self.block(x)


class _Down(nn.Module):
    def __init__(self, in_ch: int, out_ch: int) -> None:
        super().__init__()
        self.pool_conv = nn.Sequential(nn.MaxPool2d(2), _DoubleConv(in_ch, out_ch))

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        return self.pool_conv(x)


class _Up(nn.Module):
    def __init__(self, in_ch: int, out_ch: int, bilinear: bool = True) -> None:
        super().__init__()
        if bilinear:
            self.up = nn.Upsample(scale_factor=2, mode="bilinear", align_corners=True)
            self.conv = _DoubleConv(in_ch, out_ch)
        else:
            self.up = nn.ConvTranspose2d(in_ch // 2, in_ch // 2, 2, stride=2)
            self.conv = _DoubleConv(in_ch, out_ch)

    def forward(self, x1: torch.Tensor, x2: torch.Tensor) -> torch.Tensor:
        x1 = self.up(x1)
        # Pad x1 to match x2 dimensions if needed
        diff_y = x2.size(2) - x1.size(2)
        diff_x = x2.size(3) - x1.size(3)
        x1 = nn.functional.pad(x1, [diff_x // 2, diff_x - diff_x // 2, diff_y // 2, diff_y - diff_y // 2])
        return self.conv(torch.cat([x2, x1], dim=1))


class UNet(nn.Module):
    """Standard U-Net with 4 down/up levels."""

    def __init__(self, in_channels: int = 1, num_classes: int = 1, base_features: int = 64, bilinear: bool = True) -> None:
        super().__init__()
        f = base_features
        self.inc = _DoubleConv(in_channels, f)
        self.down1 = _Down(f, f * 2)
        self.down2 = _Down(f * 2, f * 4)
        self.down3 = _Down(f * 4, f * 8)
        factor = 2 if bilinear else 1
        self.down4 = _Down(f * 8, f * 16 // factor)
        self.up1 = _Up(f * 16, f * 8 // factor, bilinear)
        self.up2 = _Up(f * 8, f * 4 // factor, bilinear)
        self.up3 = _Up(f * 4, f * 2 // factor, bilinear)
        self.up4 = _Up(f * 2, f, bilinear)
        self.out_conv = nn.Conv2d(f, num_classes, 1)

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        x1 = self.inc(x)
        x2 = self.down1(x1)
        x3 = self.down2(x2)
        x4 = self.down3(x3)
        x5 = self.down4(x4)
        x = self.up1(x5, x4)
        x = self.up2(x, x3)
        x = self.up3(x, x2)
        x = self.up4(x, x1)
        return self.out_conv(x)
