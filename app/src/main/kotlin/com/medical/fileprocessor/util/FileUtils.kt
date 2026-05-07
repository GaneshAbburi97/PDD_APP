package com.medical.fileprocessor.util

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns

fun Uri.getFileSize(context: Context): Long {
    var size: Long = 0
    context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        if (sizeIndex != -1 && cursor.moveToFirst()) {
            size = cursor.getLong(sizeIndex)
        }
    }
    return size
}

fun Uri.getFileName(context: Context): String? {
    var name: String? = null
    context.contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameIndex != -1 && cursor.moveToFirst()) {
            name = cursor.getString(nameIndex)
        }
    }
    return name
}
