package com.rabapp.mysnapshot

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Build
import android.os.Handler
import java.io.File
import java.io.FileOutputStream
import android.os.IBinder
import android.os.Looper
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi


/*
class FloatingService : Service() {

    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private lateinit var imageReader: ImageReader

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForegroundService() // Start foreground service

        mediaProjection = MediaProjectionSingleton.mediaProjection
        if (mediaProjection == null) {
            stopSelf()
            return START_NOT_STICKY
        }

        startScreenCapture()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null



    private fun startScreenCapture() {
        try {
            val metrics = DisplayMetrics()
            val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(metrics)

            val width = metrics.widthPixels
            val height = metrics.heightPixels
            val density = metrics.densityDpi

            // Initialize ImageReader for capturing screenshots
            imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
            val surface = imageReader.surface

            // Create virtual display
            virtualDisplay = mediaProjection?.createVirtualDisplay(
                "ScreenCaptureVirtualDisplay",
                width,
                height,
                density,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                surface,
                null,
                null
            )

            // Listen for captured images
            imageReader.setOnImageAvailableListener({ reader ->
                val image = reader.acquireLatestImage()
                if (image != null) {
                    processCapturedImage(image)
                    image.close()
                }
            }, Handler(Looper.getMainLooper()))

            Toast.makeText(this, "Screen capture started!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Failed to start screen capture: ${e.message}", Toast.LENGTH_SHORT).show()
            stopSelf()
        }
    }

    private fun processCapturedImage(image: Image) {
        try {
            // Get the pixel buffer from the Image
            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * image.width

            // Create Bitmap from the buffer
            val bitmap = Bitmap.createBitmap(
                image.width + rowPadding / pixelStride,
                image.height,
                Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)

            // Save Bitmap to storage
            saveScreenshot(bitmap)

        } catch (e: Exception) {
            Toast.makeText(this, "Error processing image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun saveScreenshot(bitmap: Bitmap) {
        try {
            val screenshotsDir = File(getExternalFilesDir(null), "Screenshots")
            if (!screenshotsDir.exists()) screenshotsDir.mkdirs()

            val screenshotFile = File(screenshotsDir, "screenshot_${System.currentTimeMillis()}.png")
            FileOutputStream(screenshotFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            Toast.makeText(this, "Screenshot saved: ${screenshotFile.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to save screenshot: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }



*/
/*
    private fun saveScreenshot(bitmap: Bitmap) {
        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "screenshot_${System.currentTimeMillis()}.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/Screenshots")
            }

            val resolver = applicationContext.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            if (uri != null) {
                resolver.openOutputStream(uri).use { outputStream ->
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    }
                }
                Toast.makeText(this, "Screenshot saved to Gallery!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to save screenshot.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error saving screenshot: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
*//*





    private fun startForegroundService() {
        val notificationChannelId = "screen_capture_service"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "Screen Capture Service",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Screen Capture")
            .setContentText("Capturing your screen.")
            .setSmallIcon(R.drawable.logo)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION)
        } else {
            startForeground(1, notification)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        virtualDisplay?.release()
        mediaProjection?.stop()
        imageReader.close()
        Toast.makeText(this, "Screen capture stopped!", Toast.LENGTH_SHORT).show()
    }
}
*/


class FloatingService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingView: View
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private lateinit var imageReader: ImageReader

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Initialize windowManager
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_icon_layout, null)

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            android.graphics.PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 100
            y = 100
        }

        windowManager.addView(floatingView, layoutParams)

        // Set up the click listener for the capture button
        floatingView.findViewById<ImageView>(R.id.capture_button).setOnClickListener {
            captureScreenshotAndSave()
        }

        // Set the touch listener for dragging the floating icon
        floatingView.setOnTouchListener(FloatingTouchListener(windowManager, layoutParams))

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun captureScreenshotAndSave() {
        startScreenCapture()
    }

    private fun startScreenCapture() {
        try {
            mediaProjection = MediaProjectionSingleton.mediaProjection
            if (mediaProjection == null) {
                stopSelf()
                return
            }

            val metrics = DisplayMetrics()
            val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(metrics)

            val width = metrics.widthPixels
            val height = metrics.heightPixels
            val density = metrics.densityDpi

            imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
            val surface = imageReader.surface

            virtualDisplay = mediaProjection?.createVirtualDisplay(
                "ScreenCaptureVirtualDisplay",
                width,
                height,
                density,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                surface,
                null,
                null
            )

            imageReader.setOnImageAvailableListener({ reader ->
                val image = reader.acquireLatestImage()
                if (image != null) {
                    processCapturedImage(image)
                    image.close()
                }
            }, Handler(Looper.getMainLooper()))

            Toast.makeText(this, "Screen capture started!", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Failed to start screen capture: ${e.message}", Toast.LENGTH_SHORT).show()
            stopSelf()
        }
    }

    private fun processCapturedImage(image: Image) {
        try {
            val planes = image.planes
            val buffer = planes[0].buffer
            val pixelStride = planes[0].pixelStride
            val rowStride = planes[0].rowStride
            val rowPadding = rowStride - pixelStride * image.width

            val bitmap = Bitmap.createBitmap(
                image.width + rowPadding / pixelStride,
                image.height,
                Bitmap.Config.ARGB_8888
            )
            bitmap.copyPixelsFromBuffer(buffer)

            saveScreenshot(bitmap)

        } catch (e: Exception) {
            Toast.makeText(this, "Error processing image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveScreenshot(bitmap: Bitmap) {
        try {
            val screenshotsDir = File(getExternalFilesDir(null), "Screenshots")
            if (!screenshotsDir.exists()) screenshotsDir.mkdirs()

            val screenshotFile = File(screenshotsDir, "screenshot_${System.currentTimeMillis()}.png")
            FileOutputStream(screenshotFile).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            Toast.makeText(this, "Screenshot saved: ${screenshotFile.absolutePath}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to save screenshot: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        virtualDisplay?.release()
        mediaProjection?.stop()
        imageReader.close()
        windowManager.removeView(floatingView)
        Toast.makeText(this, "Screen capture stopped!", Toast.LENGTH_SHORT).show()
    }
}













