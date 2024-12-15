package com.rabapp.mysnapshot

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.media.Image
import android.graphics.PixelFormat
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Surface
import android.widget.Toast
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object ScreenCaptureUtil {
    fun createSurface(width: Int, height: Int): Surface {
        val imageReader = ImageReader.newInstance(width, height, ImageFormat.RGB_565, 2)
        return imageReader.surface
    }
}

