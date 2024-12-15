package com.rabapp.mysnapshot

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PixelFormat
import android.graphics.drawable.Icon
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.getSystemService
import com.rabapp.mysnapshot.ui.theme.MySnapShotTheme
import java.io.File
import java.io.FileOutputStream
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.OutputStream

class MainActivity : ComponentActivity() {

    private val REQUEST_CODE_STORAGE_PERMISSION = 2001
    private val REQUEST_CODE_SCREEN_CAPTURE = 1001
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private var mediaProjection: MediaProjection? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        checkOverlayPermission()
      //  checkStoragePermission()
        setContent {
            MySnapShotTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Column {
                        RoundedToolbar()
                        CenteredIcon()
                    }
                }
            }
        }
    }



    private fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:com.rabapp.mysnapshot"))
            startActivityForResult(intent, 1234)
        } else {
            requestScreenCapturePermission()
        }
    }

    private fun requestScreenCapturePermission() {
        val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
        startActivityForResult(captureIntent, REQUEST_CODE_SCREEN_CAPTURE)
    }

/*
    private fun checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11+ (Scoped Storage), request "All files access" permission
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, REQUEST_CODE_STORAGE_PERMISSION)
            }
        } else {
            // For Android 10 and below, Scoped Storage is not applicable
            // and WRITE_EXTERNAL_STORAGE is still required for older versions
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                REQUEST_CODE_STORAGE_PERMISSION
            )
        }
    }
*/


    /*  private fun startScreenCapture() {
          imageReader = ImageReader.newInstance(
              resources.displayMetrics.widthPixels,
              resources.displayMetrics.heightPixels,
              PixelFormat.RGBA_8888,
              2
          )

          val surface = imageReader.surface
          mediaProjection?.createVirtualDisplay(
              "ScreenCapture",
              resources.displayMetrics.widthPixels,
              resources.displayMetrics.heightPixels,
              resources.displayMetrics.densityDpi,
              0,
              surface,
              null,
              null
          )

          captureScreenshot()
      }


      private fun captureScreenshot() {
          val image = imageReader.acquireLatestImage()

          image?.use {
              val planes = it.planes
              val buffer = planes[0].buffer
              val byteArray = ByteArray(buffer.remaining())
              buffer.get(byteArray)

              val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
              Toast.makeText(this, "Screenshot taken!", Toast.LENGTH_SHORT).show()
              saveScreenshot(bitmap)
          }
      }*/

    private fun startFloatingService() {
        val intent = Intent(this, FloatingService::class.java)
        ContextCompat.startForegroundService(this, intent)
    }




    @Composable
    fun CenteredIcon() {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Centered Icon",
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Take screenshots with the app tool to show here",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    @Composable
    fun RoundedToolbar() {
        Surface(
            color = Color(0xFF2A4241),
            shape = RoundedCornerShape(
                bottomStart = 24.dp,
                bottomEnd = 24.dp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.icontoolbar),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable {
                            // Add back navigation or functionality here if needed
                        }
                )

                Image(
                    painter = painterResource(id = R.drawable.iconsetting),
                    contentDescription = "More",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1234) {
            if (Settings.canDrawOverlays(this)) {
                requestScreenCapturePermission()
            } else {
                Toast.makeText(this, "Overlay permission is required", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == REQUEST_CODE_SCREEN_CAPTURE && resultCode == RESULT_OK) {
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data!!)
            MediaProjectionSingleton.mediaProjection = mediaProjection
            startFloatingService()
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

}





