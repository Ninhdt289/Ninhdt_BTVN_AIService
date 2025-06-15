package com.example.aisevice.data.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.Keep
import androidx.exifinterface.media.ExifInterface
import com.apero.aigenerate.network.model.segment.ResponseSegment

import com.example.aisevice.data.remote.ServiceError.CODE_FILE_NULL
import com.example.aisevice.data.remote.ServiceError.SAVE_FILE_ERROR
import com.example.aisevice.data.remote.model.ResponseState
import com.example.aisevice.data.remote.model.enqueueCallResult
import com.example.aisevice.data.remote.model.enqueueCallSegmentResult
import com.example.aisevice.ultils.loadBitmapAndScaleWithGlide
import com.example.aisevice.ultils.loadBitmapWithGlide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Call
import java.io.File
import java.io.FileOutputStream
import java.util.Locale
import java.util.UUID
import kotlin.math.max

@Keep
object FileHelper {
    const val RESOLUTION_IMAGE_OUTPUT = 1600

    fun getMimeType(file: File, fallback: String = "image/*"): String {
        return MimeTypeMap.getFileExtensionFromUrl(file.toString())?.let {
            MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(it.lowercase(Locale.getDefault()))
        } ?: fallback
    }

    fun getFolderInCache(context: Context, folderName: String): String {
        val folder = File(context.cacheDir.path, folderName)
        if (folder.exists().not()) {
            folder.mkdirs()
        }
        return folder.path
    }

    private suspend fun saveFileFromResponseBody(
        body: ResponseBody,
        fileResult: File,
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            body.byteStream().use { inputStream ->
                FileOutputStream(fileResult.path).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var bytesRead: Int
                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                    }
                }
            }
            Result.success(true)
        } catch (e: Exception) {
            deleteFileInCache(fileResult.path)
            Result.failure(e)
        }
    }

    suspend fun deleteFileInCache(pathFile: String) {
        val file = File(pathFile)
        if (file.exists()) {
            withContext(Dispatchers.IO) {
                file.delete()
            }
        }
    }


    suspend fun deleteFolderInCache(pathFolder: String) {
        val folder = File(pathFolder)
        if (folder.exists()) {
            withContext(Dispatchers.IO) {
                folder.deleteRecursively()
            }
        }
    }

    suspend fun deleteAllFileInCache(cacheDir: File) {
        if (cacheDir.listFiles().isNullOrEmpty().not()) {
            withContext(Dispatchers.IO) {
                cacheDir.deleteRecursively()
            }
        }
    }

    suspend fun handleResultState(
        context: Context,
        response: Call<ResponseBody>,
        nameFolder: String,
    ): ResponseState<File, Throwable> {
        val folderPath = getFolderInCache(context,nameFolder)
        return when (val stateEnqueueResponse = response.enqueueCallResult()) {
            is ResponseState.Success -> {
                stateEnqueueResponse.data?.body()?.let { body ->
                    val name = "${UUID.randomUUID()}.png"
                    val fileResult = File(folderPath, name)
                    val saveResult = saveFileFromResponseBody(body, fileResult)
                    saveResult.fold(
                        onSuccess = {
                            ResponseState.Success(fileResult)
                        },
                        onFailure = {
                            ResponseState.Error(Throwable(SAVE_FILE_ERROR), CODE_FILE_NULL)
                        }
                    )
                } ?: run {
                    ResponseState.Error(Throwable(SAVE_FILE_ERROR), CODE_FILE_NULL)
                }
            }

            is ResponseState.Error -> ResponseState.Error(
                stateEnqueueResponse.error,
                stateEnqueueResponse.code
            )

        }
    }

    suspend fun handleResultSegmentState(
        response: Call<ResponseSegment>,
    ): ResponseState<ResponseSegment, Throwable> {
        return when (val stateEnqueueResponse = response.enqueueCallSegmentResult()) {
            is ResponseState.Success -> {
                val data = stateEnqueueResponse.data
                if (data!!.body() != null) {
                    ResponseState.Success(data.body()!!)
                } else {
                    ResponseState.Error(Throwable(SAVE_FILE_ERROR), CODE_FILE_NULL)
                }
            }

            is ResponseState.Error-> ResponseState.Error(
                stateEnqueueResponse.error,
                stateEnqueueResponse.code
            )
        }
    }

    suspend fun String.preProcessingPath(folderName: String,context: Context): String {
        val bitmapOption = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        BitmapFactory.decodeFile(this@preProcessingPath, bitmapOption)
        val maxResolution = max(bitmapOption.outWidth, bitmapOption.outHeight)
        val isBigSize = maxResolution > RESOLUTION_IMAGE_OUTPUT
        val rotation = this.getRotationValue()
        val isImageJPG = File(this).extension.lowercase().let {
            it == "jpg" || it == "jpeg"
        }
        return when {
            isBigSize -> {
                val bitmapResult =
                    this.loadBitmapAndScaleWithGlide(context,bitmapOption.outWidth, bitmapOption.outHeight)
                saveBitmapToFile(bitmapResult, folderName, System.currentTimeMillis().toString(),context)
            }

            rotation != 0 || isImageJPG.not() -> {
                val bitmapInput = this.loadBitmapWithGlide(context)
                saveBitmapToFile(bitmapInput, folderName, System.currentTimeMillis().toString(),context)
            }

            else -> this
        }
    }

    suspend fun String.getRotationValue(): Int = withContext(Dispatchers.Default) {
        val exifInterface = ExifInterface(this@getRotationValue)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
        )
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    }

    private suspend fun saveBitmapToFile(
        bitmap: Bitmap,
        folderName: String,
        fileName: String,
        context: Context
    ): String =
        withContext(Dispatchers.IO) {
            val newFile = File(getFolderInCache(context,folderName), "image_$fileName.jpg").apply {
                outputStream().use { out ->
                    bitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        100,
                        out
                    )
                }
            }
            return@withContext newFile.absolutePath
        }

    suspend fun downloadAndSaveFile(
        url: String,
        nameFolder: String,
        context: Context
    ): ResponseState<File, Throwable> = withContext(Dispatchers.IO) {
        try {
            val folderPath = getFolderInCache(context,nameFolder)
            val fileName = "${UUID.randomUUID()}.png"
            val outputFile = File(folderPath, fileName)

            val client = okhttp3.OkHttpClient()
            val request = okhttp3.Request.Builder()
                .url(url)
                .build()
            Log.d("downloadAndSaveFile", "url: $url")

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    return@withContext ResponseState.Error(
                        Throwable("Failed to download file: ${response.code}"),
                        response.code
                    )
                }

                response.body?.let { body ->
                    val saveResult = saveFileFromResponseBody(body, outputFile)
                    return@withContext saveResult.fold(
                        onSuccess = {
                            ResponseState.Success(outputFile)
                        },
                        onFailure = {
                            ResponseState.Error(Throwable(SAVE_FILE_ERROR), CODE_FILE_NULL)
                        }
                    )
                } ?: run {
                    return@withContext ResponseState.Error(
                        Throwable(SAVE_FILE_ERROR),
                        CODE_FILE_NULL
                    )
                }
            }
        } catch (e: Exception) {
            ResponseState.Error(e, CODE_FILE_NULL)
        }
    }
}
