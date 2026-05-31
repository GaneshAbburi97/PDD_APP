import nibabel as nib
import numpy as np

def preprocess_image(input_path: str) -> np.ndarray:
    image = nib.load(input_path)
    volume = image.get_fdata(dtype=np.float32)
    if volume.ndim == 4:
        volume = volume[..., 0]
    volume = np.nan_to_num(volume, nan=0.0, posinf=0.0, neginf=0.0).astype(np.float32)
    min_val = np.min(volume)
    max_val = np.max(volume)
    if max_val > min_val:
        volume = (volume - min_val) / (max_val - min_val)
    else:
        volume = np.zeros_like(volume, dtype=np.float32)
    return volume
