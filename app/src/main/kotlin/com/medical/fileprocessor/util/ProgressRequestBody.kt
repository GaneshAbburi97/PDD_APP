package com.medical.fileprocessor.util

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

/**
 * A custom [RequestBody] that reports upload progress.
 */
class ProgressRequestBody(
    private val file: File,
    private val contentType: String,
    private val onProgress: (progress: Int) -> Unit
) : RequestBody() {

    override fun contentType(): MediaType? {
        return contentType.toMediaTypeOrNull()
    }

    override fun contentLength(): Long {
        return file.length()
    }

    override fun writeTo(sink: BufferedSink) {
        val fileLength = file.length()
        val buffer = ByteArray(2048)
        val inStream = FileInputStream(file)
        var uploaded: Long = 0

        try {
            var read: Int
            while (inStream.read(buffer).also { read = it } != -1) {
                sink.write(buffer, 0, read)
                uploaded += read
                val progress = if (fileLength > 0) {
                    ((uploaded.toFloat() / fileLength.toFloat()) * 100).toInt().coerceIn(0, 100)
                } else {
                    0
                }
                onProgress(progress)
            }
        } finally {
            inStream.close()
        }
    }
}
