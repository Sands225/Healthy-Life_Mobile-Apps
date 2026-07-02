package com.example.healthylife.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import java.io.File

/**
 * Menyimpan & memuat foto profil di penyimpanan internal aplikasi.
 */
object AvatarStore {

    private const val FILE_NAME = "profile_avatar.jpg"

    private fun file(context: Context) = File(context.filesDir, FILE_NAME)

    fun exists(context: Context): Boolean = file(context).exists()

    /** Salin gambar terpilih ke penyimpanan internal. */
    fun save(context: Context, uri: Uri): Boolean = try {
        context.contentResolver.openInputStream(uri)?.use { input ->
            file(context).outputStream().use { output -> input.copyTo(output) }
        }
        true
    } catch (e: Exception) {
        false
    }

    /** Decode gambar dari URI (di-downsample) untuk diatur di CropImageView. */
    fun decode(context: Context, uri: Uri, target: Int = 1080): Bitmap? = try {
        val cr = context.contentResolver
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        cr.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (bounds.outWidth / sample > target || bounds.outHeight / sample > target) sample *= 2
        cr.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample })
        }
    } catch (e: Exception) {
        null
    }

    /** Simpan bitmap hasil crop ke penyimpanan internal. */
    fun saveBitmap(context: Context, bitmap: Bitmap): Boolean = try {
        file(context).outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it) }
        true
    } catch (e: Exception) {
        false
    }

    /** Muat foto (di-downsample) ke ImageView. Return true jika ada foto. */
    fun loadInto(imageView: ImageView): Boolean {
        val f = file(imageView.context)
        if (!f.exists()) return false
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(f.absolutePath, bounds)
        var sample = 1
        val target = 256
        while (bounds.outWidth / sample > target || bounds.outHeight / sample > target) sample *= 2
        val bitmap = BitmapFactory.decodeFile(f.absolutePath, BitmapFactory.Options().apply { inSampleSize = sample })
            ?: return false
        imageView.setImageBitmap(bitmap)
        return true
    }
}
