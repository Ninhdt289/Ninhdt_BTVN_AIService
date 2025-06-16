package com.example.aisevice.data.local.impl

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.aisevice.data.local.model.DeviceImage
import com.example.aisevice.data.local.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

class ImageRepositoryImpl(private val contentResolver: ContentResolver) : ImageRepository {
    override suspend fun getDeviceImages(offset: Int, limit: Int): List<DeviceImage> = withContext(Dispatchers.IO) {
        val images = mutableListOf<DeviceImage>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        val selection = null
        val selectionArgs = null

        try {
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateAddedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)

                // Move to offset position
                if (cursor.moveToPosition(offset)) {
                    var count = 0
                    do {
                        val id = cursor.getLong(idColumn)
                        val displayName = cursor.getString(displayNameColumn)
                        val dateAdded = cursor.getLong(dateAddedColumn)
                        val size = cursor.getLong(sizeColumn)
                        val mimeType = cursor.getString(mimeTypeColumn)

                        val contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            id
                        )

                        images.add(
                            DeviceImage(
                                id = id,
                                uri = contentUri,
                                displayName = displayName,
                                dateAdded = dateAdded,
                                size = size,
                                mimeType = mimeType
                            )
                        )
                        count++
                    } while (cursor.moveToNext() && count < limit)
                }
            }
        } catch (e: Exception) {
            Log.e("ImageRepository", "Error loading images", e)
        }

        return@withContext images
    }

    override suspend fun getTotalImageCount(): Int = withContext(Dispatchers.IO) {
        var count = 0
        try {
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID),
                null,
                null,
                null
            )?.use { cursor ->
                count = cursor.count
            }
        } catch (e: Exception) {
            Log.e("ImageRepository", "Error getting image count", e)
        }
        return@withContext count
    }

    override suspend fun downloadImage(imageUrl: String, fileName: String): Uri? = withContext(Dispatchers.IO) {
        try {
            val url = URL(imageUrl)
            val connection = url.openConnection()
            connection.connect()
            val inputStream: InputStream = connection.getInputStream()

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/AIDownloads")
            }

            val uri: Uri? = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            uri?.let { imageUri ->
                val outputStream: OutputStream? = contentResolver.openOutputStream(imageUri)
                outputStream?.use { os ->
                    inputStream.copyTo(os)
                }
                return@withContext imageUri
            }
        } catch (e: Exception) {
            Log.e("ImageRepositoryImpl", "Error downloading image: $e", e)
        }
        return@withContext null
    }
}
