"""Medical-specific metrics (Hausdorff distance etc.)."""

from __future__ import annotations

import numpy as np


def hausdorff_distance(pred_mask: np.ndarray, gt_mask: np.ndarray) -> float:
    """Compute the 95th-percentile Hausdorff distance between two binary masks.

    Requires scipy.
    Returns infinity if either mask is empty.
    """
    try:
        from scipy.spatial.distance import directed_hausdorff
    except ImportError:
        return float("inf")

    pred_pts = np.argwhere(pred_mask > 0)
    gt_pts = np.argwhere(gt_mask > 0)

    if len(pred_pts) == 0 or len(gt_pts) == 0:
        return float("inf")

    d1 = directed_hausdorff(pred_pts, gt_pts)[0]
    d2 = directed_hausdorff(gt_pts, pred_pts)[0]
    return max(d1, d2)
