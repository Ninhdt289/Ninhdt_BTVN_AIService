package com.example.aisevice.data.local.impl

import android.content.ContentResolver
import android.content.ContentUris
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Log
import com.example.aisevice.data.local.model.DeviceImage
import com.example.aisevice.data.local.repository.ImageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class ImageRepositoryImpl(private val contentResolver: ContentResolver) {
  fun getDeviceImages(): List<DeviceImage> {

        val images = mutableListOf<DeviceImage>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.MIME_TYPE
        )

        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        try {
            contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                Log.d("ImageRepository", "Found ${cursor.count} images")
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val displayNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val dateAddedColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val dateAdded = cursor.getLong(dateAddedColumn)
                    val size = cursor.getLong(sizeColumn)
                    val mimeType = cursor.getString(mimeTypeColumn)

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    val bitmap = try {
                        val inputStream = contentResolver.openInputStream(contentUri)
                        BitmapFactory.decodeStream(inputStream)
                    } catch (e: Exception) {
                        Log.e("ImageRepository", "Error decoding bitmap for $contentUri", e)
                        null
                    }

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
                }
            }
        } catch (e: Exception) {
            Log.e("ImageRepository", "Error loading images", e)
        }

        return images
    }


}
