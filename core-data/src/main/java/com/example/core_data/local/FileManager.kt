package com.example.core_data.local

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class FileManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : FileManager {

    override fun saveImage(uri: Uri): String? {
        return try {
            val input = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.filesDir, "vehicle_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { output -> input.copyTo(output) }
            input.close()
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun deleteImage(path: String): Boolean {
        return try {
            val file = File(path)
            if (file.exists()) file.delete() else false
        } catch (e: Exception) {
            false
        }
    }
}

interface FileManager {
    fun saveImage(uri: Uri): String?
    fun deleteImage(path: String): Boolean
}