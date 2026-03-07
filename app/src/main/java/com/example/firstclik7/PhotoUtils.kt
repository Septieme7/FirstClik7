package com.example.firstclik7

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PhotoUtils {

    fun sauvegarderPhoto(context: Context, bitmap: Bitmap): String? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val nomFichier = "IMG_$timestamp.png"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, nomFichier)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Clik7")
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                resolver.openOutputStream(it)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                return uri.toString()
            } ?: return null
        } else {
            val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val clik7Dir = File(picturesDir, "Clik7")
            if (!clik7Dir.exists()) clik7Dir.mkdirs()
            val fichier = File(clik7Dir, nomFichier)
            FileOutputStream(fichier).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            return fichier.absolutePath
        }
    }

    fun chargerBitmap(context: Context, chemin: String): Bitmap? {
        return try {
            if (chemin.startsWith("content://")) {
                val inputStream = context.contentResolver.openInputStream(Uri.parse(chemin))
                BitmapFactory.decodeStream(inputStream)
            } else {
                BitmapFactory.decodeFile(chemin)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}