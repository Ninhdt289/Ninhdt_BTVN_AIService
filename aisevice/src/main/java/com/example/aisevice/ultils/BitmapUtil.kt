package com.example.aisevice.ultils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.aisevice.data.remote.FileHelper.RESOLUTION_IMAGE_OUTPUT
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.max
import kotlin.math.min

suspend fun String.loadBitmapWithGlide(context: Context): Bitmap =
    suspendCoroutine { continuation ->
        Glide.with(context)
            .asBitmap()
            .load(this)
            .skipMemoryCache(false)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?,
                ) {
                    return continuation.resume(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    /* no-op */
                }
            })
    }

suspend fun String.loadBitmapAndScaleWithGlide(context: Context,imageWidth: Int, imageHeight: Int): Bitmap =
    suspendCoroutine { continuation ->
        val size =
            (RESOLUTION_IMAGE_OUTPUT.toDouble() / max(imageWidth, imageHeight)) * min(imageWidth, imageHeight)
        Glide.with(context)
            .asBitmap()
            .load(this)
            .skipMemoryCache(false)
            .override(size.toInt())
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?,
                ) {
                    return continuation.resume(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    /* no-op */
                }
            })
    }
