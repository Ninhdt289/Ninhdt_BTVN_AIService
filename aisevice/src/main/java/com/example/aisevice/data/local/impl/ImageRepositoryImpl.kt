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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.net.URL

class ImageRepositoryImpl(private val contentResolver: ContentResolver) : ImageRepository {

    private val cachedImages = mutableListOf<DeviceImage>()
    private val mutex = Mutex()
    private var isPreloading = false

    override suspend fun getDeviceImages(offset: Int, limit: Int): List<DeviceImage> =
        mutex.withLock {
            val requiredSize = offset + limit
            if (cachedImages.size >= requiredSize) {
                return@withLock cachedImages.subList(offset, requiredSize)
            }

            val currentSize = cachedImages.size
            val newImagesToFetch = requiredSize - currentSize

            if (newImagesToFetch > 0) {
                val fetchedImages = queryDeviceImages(currentSize, newImagesToFetch)
                cachedImages.addAll(fetchedImages)
            }

            return@withLock if (cachedImages.size >= requiredSize) {
                cachedImages.subList(offset, requiredSize)
            } else {
                cachedImages.drop(offset)
            }
        }

    suspend fun preloadInitialPages() {
        if (isPreloading || cachedImages.isNotEmpty()) return

        isPreloading = true
        withContext(Dispatchers.IO) {
            try {
                getDeviceImages(0, 100)
            } catch (e: Exception) {
                Log.e("ImageRepository", "Error preloading images", e)
            } finally {
                isPreloading = false
            }
        }
    }

    private suspend fun queryDeviceImages(offset: Int, limit: Int): List<DeviceImage> =
        withContext(Dispatchers.IO) {
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
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val displayNameColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                    val dateAddedColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                    val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                    val mimeTypeColumn =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)

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
                                    mimeType = mimeType,
                                    bitmap = null
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

    private fun calculateSampleSize(width: Int, height: Int, reqWidth: Int, reqHeight: Int): Int {
        var sampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while ((halfHeight / sampleSize) >= reqHeight && (halfWidth / sampleSize) >= reqWidth) {
                sampleSize *= 2
            }
        }
        return sampleSize
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

    override suspend fun downloadImage(imageUrl: String, fileName: String): Uri? =
        withContext(Dispatchers.IO) {
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

                val uri: Uri? = contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

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
