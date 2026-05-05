"""Attention U-Net architecture for IAN segmentation.

Architecture summary
--------------------
Attention U-Net (Oktay et al., 2018) augments the standard U-Net with
attention gates that learn to suppress irrelevant feature activations.

Why Attention U-Net for IAN?
- Attention gates focus on the nerve region, reducing false positives
  in surrounding bone/tissue structures.
- Minimal extra compute (~5–10% overhead vs plain U-Net) for ~3–5%
  Dice improvement on narrow tubular structures.

GPU memory: ~3 GB for 512x512 single-channel input, batch size 4.
Expected metrics (planning target): Dice ≥ 0.87, IoU ≥ 0.77.
"""

from __future__ import annotations

import torch
import torch.nn as nn

from app.ai.architectures.unet import _DoubleConv, _Down


class _AttentionGate(nn.Module):
    def __init__(self, f_g: int, f_l: int, f_int: int) -> None:
        super().__init__()
        self.W_g = nn.Sequential(nn.Conv2d(f_g, f_int, 1, bias=True), nn.BatchNorm2d(f_int))
        self.W_x = nn.Sequential(nn.Conv2d(f_l, f_int, 1, bias=True), nn.BatchNorm2d(f_int))
        self.psi = nn.Sequential(nn.Conv2d(f_int, 1, 1, bias=True), nn.BatchNorm2d(1), nn.Sigmoid())
        self.relu = nn.ReLU(inplace=True)

    def forward(self, g: torch.Tensor, x: torch.Tensor) -> torch.Tensor:
        g1 = self.W_g(g)
        x1 = self.W_x(x)
        psi = self.relu(g1 + x1)
        psi = self.psi(psi)
        return x * psi


class AttentionUNet(nn.Module):
    """U-Net with attention gates at each skip connection."""

    def __init__(self, in_channels: int = 1, num_classes: int = 1, base_features: int = 64) -> None:
        super().__init__()
        f = base_features
        self.inc = _DoubleConv(in_channels, f)
        self.down1 = _Down(f, f * 2)
        self.down2 = _Down(f * 2, f * 4)
        self.down3 = _Down(f * 4, f * 8)
        self.down4 = _Down(f * 8, f * 16)

        self.up4 = nn.ConvTranspose2d(f * 16, f * 8, 2, stride=2)
        self.att4 = _AttentionGate(f_g=f * 8, f_l=f * 8, f_int=f * 4)
        self.conv4 = _DoubleConv(f * 16, f * 8)

        self.up3 = nn.ConvTranspose2d(f * 8, f * 4, 2, stride=2)
        self.att3 = _AttentionGate(f_g=f * 4, f_l=f * 4, f_int=f * 2)
        self.conv3 = _DoubleConv(f * 8, f * 4)

        self.up2 = nn.ConvTranspose2d(f * 4, f * 2, 2, stride=2)
        self.att2 = _AttentionGate(f_g=f * 2, f_l=f * 2, f_int=f)
        self.conv2 = _DoubleConv(f * 4, f * 2)

        self.up1 = nn.ConvTranspose2d(f * 2, f, 2, stride=2)
        self.att1 = _AttentionGate(f_g=f, f_l=f, f_int=f // 2)
        self.conv1 = _DoubleConv(f * 2, f)

        self.out_conv = nn.Conv2d(f, num_classes, 1)

    def forward(self, x: torch.Tensor) -> torch.Tensor:
        e1 = self.inc(x)
        e2 = self.down1(e1)
        e3 = self.down2(e2)
        e4 = self.down3(e3)
        e5 = self.down4(e4)

        d4 = self.up4(e5)
        e4 = self.att4(d4, e4)
        d4 = self.conv4(torch.cat([e4, d4], dim=1))

        d3 = self.up3(d4)
        e3 = self.att3(d3, e3)
        d3 = self.conv3(torch.cat([e3, d3], dim=1))

        d2 = self.up2(d3)
        e2 = self.att2(d2, e2)
        d2 = self.conv2(torch.cat([e2, d2], dim=1))

        d1 = self.up1(d2)
        e1 = self.att1(d1, e1)
        d1 = self.conv1(torch.cat([e1, d1], dim=1))

        return self.out_conv(d1)
