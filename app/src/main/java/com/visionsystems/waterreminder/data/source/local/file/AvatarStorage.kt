package com.visionsystems.waterreminder.data.source.local.file

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

@Singleton
class AvatarStorage @Inject constructor(
    @param:ApplicationContext private val context: Context
) {

    private val directory: File get() = File(context.filesDir, DIRECTORY).apply { mkdirs() }

    suspend fun save(uid: String, sourceUri: String): String = withContext(Dispatchers.IO) {
        val bitmap = decode(sourceUri.toUri())
        val file = File(directory, "${uid.safeName()}_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, it) }
        bitmap.recycle()
        Uri.fromFile(file).toString()
    }

    suspend fun delete(photoUri: String?) = withContext(Dispatchers.IO) {
        val path = photoUri?.toUri()?.path ?: return@withContext
        val file = File(path)
        if (file.parentFile == directory) file.delete()
    }

    private fun decode(uri: Uri): Bitmap =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri)) { decoder, info, _ ->
                val largest = max(info.size.width, info.size.height)
                if (largest > MAX_SIZE_PX) {
                    val scale = MAX_SIZE_PX.toFloat() / largest
                    decoder.setTargetSize((info.size.width * scale).toInt(), (info.size.height * scale).toInt())
                }
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
            }
        } else {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            var sample = 1
            while (max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= MAX_SIZE_PX) sample *= 2
            val options = BitmapFactory.Options().apply { inSampleSize = sample }
            context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, options) }
                ?: throw IllegalArgumentException("Unable to read image")
        }

    private fun String.safeName(): String = replace(Regex("[^A-Za-z0-9_-]"), "_")

    private companion object {
        const val DIRECTORY = "avatars"
        const val MAX_SIZE_PX = 512
        const val JPEG_QUALITY = 88
    }
}
