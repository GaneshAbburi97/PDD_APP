package com.medical.fileprocessor.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility for compressing and resizing images before upload.
 * 
 * Specifically optimized for Firebase Free Tier:
 * - Max dimension: 512px
 * - Quality: 80%
 * - Format: JPEG
 */
@Singleton
class ImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Compresses an image from a URI and saves it to a temporary file.
     * 
     * @param uri Source image URI
     * @param maxWidth Maximum width (default 512)
     * @param maxHeight Maximum height (default 512)
     * @param quality Compression quality 0-100 (default 80)
     * @return Compressed File or null if processing failed
     */
    suspend fun compressImage(
        uri: Uri,
        maxWidth: Int = 512,
        maxHeight: Int = 512,
        quality: Int = 80
    ): File? = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return@withContext null
            val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return@withContext null
            
            // Calculate scaling factors
            val scale = minOf(
                maxWidth.toFloat() / originalBitmap.width,
                maxHeight.toFloat() / originalBitmap.height
            ).coerceAtMost(1.0f)

            // Resize bitmap
            val matrix = Matrix().apply { postScale(scale, scale) }
            val resizedBitmap = Bitmap.createBitmap(
                originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true
            )

            // Compress to JPEG
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val byteArray = outputStream.toByteArray()

            // Save to temp file
            val tempFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
            FileOutputStream(tempFile).use { fos ->
                fos.write(byteArray)
            }

            // Cleanup
            originalBitmap.recycle()
            if (resizedBitmap != originalBitmap) {
                resizedBitmap.recycle()
            }

            tempFile
        } catch (e: Exception) {
            null
        }
    }
}
