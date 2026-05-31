package com.cuangx.finance.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object PhotoUtils {

    fun savePhotoToInternal(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            val fileName = "receipt_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, "receipts")
            if (!file.exists()) file.mkdirs()

            val outputFile = File(file, fileName)
            FileOutputStream(outputFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
            }

            outputFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getPhotoFile(context: Context, path: String): File? {
        val file = File(path)
        return if (file.exists()) file else null
    }

    fun deletePhoto(path: String): Boolean {
        return try {
            val file = File(path)
            if (file.exists()) file.delete() else false
        } catch (e: Exception) {
            false
        }
    }
}
