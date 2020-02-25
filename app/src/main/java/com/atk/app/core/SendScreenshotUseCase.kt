package com.atk.app.core

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.PixelCopy
import android.widget.Toast
import androidx.core.content.FileProvider.getUriForFile
import androidx.fragment.app.FragmentActivity
import com.atk.app.core.repository.database.entity.CreatedObject
import com.atk.app.core.utils.isAppAvailable
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class SendScreenshotUseCase constructor(
    private val activity: FragmentActivity
) {

    companion object {
        const val appName: String = "org.telegram.messenger"
    }

    private var imagePath: File? = null
    private var newFile: File? = null
    private var contentUri: Uri? = null

    private fun sendToTelegram(createdObject: CreatedObject) {
        val isAppInstalled: Boolean = activity.isAppAvailable(appName)
        if (isAppInstalled) {
            Intent(Intent.ACTION_SEND).apply {
                setPackage(appName)
                type = "text/plain"
                contentUri?.let { putExtra(Intent.EXTRA_STREAM, it) }

                val dutListAsString = StringBuilder().apply {
                    createdObject.dutList.forEach {
                        append(it.name).append(" ").append(it.value).append("\n")
                    }
                }
                val msg =
                    createdObject.company.name + "\n" +
                        createdObject.unitName + "\n" +
                        createdObject.equipment.type + " " + createdObject.equipment.imei + "\n" +
                        createdObject.simCard.simType + " " + createdObject.simCard.phone + "\n" +
                        dutListAsString.toString()

                putExtra(Intent.EXTRA_TEXT, msg)

                activity.startActivity(Intent.createChooser(this, "Share with"))
            }
        } else {
            Toast.makeText(activity, "Telegram not Installed", Toast.LENGTH_SHORT).show()
        }
    }

    fun sendScreenshotToTelegram(createdObject: CreatedObject) {
        createFile()
        Log.d("Screenshot", "Taken")
        getScreenshot()
        Log.d("Screenshot", "Sent")
        sendToTelegram(createdObject)
    }

    private fun getScreenshot() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getScreenShotFromView()
        } else {
            getScreenShot()
        }
    }

    @Suppress("DEPRECATION")
    private fun getScreenShot(): Bitmap {
        var bitmap: Bitmap? = null
        activity.window?.let {
            it.decorView.rootView.apply {
                isDrawingCacheEnabled = true
                bitmap = Bitmap.createBitmap(drawingCache)
                isDrawingCacheEnabled = false
            }
        }

        return bitmap!!
    }

    private fun getScreenShotFromView() {
        activity.apply {

            window?.let { window ->
                val view = window.decorView.rootView
                val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
                val locationOfViewInWindow = IntArray(2)
                view.getLocationInWindow(locationOfViewInWindow)
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        PixelCopy.request(
                            window,
                            Rect(
                                locationOfViewInWindow[0],
                                locationOfViewInWindow[1],
                                locationOfViewInWindow[0] + view.width,
                                locationOfViewInWindow[1] + view.height
                            ), bitmap, { copyResult ->
                                if (copyResult == PixelCopy.SUCCESS) {
                                    saveImageToFile(bitmap)
                                } else {
                                }
                                // possible to handle other result codes ...
                            },
                            view.handler
                        )
                    }
                } catch (e: IllegalArgumentException) {
                    // PixelCopy may throw IllegalArgumentException, make sure to handle it
                    e.printStackTrace()
                }
            }
        }
    }

    private fun createFile() {
        imagePath = File(activity.filesDir, "images")
        if (imagePath!!.parentFile?.exists() == false)
            imagePath!!.parentFile?.mkdirs()
        newFile = File(imagePath, "default_image.jpg")
        if (newFile!!.parentFile?.exists() == false)
            newFile!!.parentFile?.mkdirs()
//        newFile = File(
//            Environment.getExternalStorageDirectory().toString() + File.separator + "testimage.jpg"
//        )
//        newFile!!.createNewFile()
        contentUri = getUriForFile(activity, "com.atk.app.fileprovider", newFile!!)
    }

    private fun saveImageToFile(bitmap: Bitmap) {
        // Delete old file if exist.
        val file = newFile!!
        if (file.exists()) {
            file.delete()
        }
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
        } catch (e: Exception) {
            Log.e("DEBUG", e.message.orEmpty())
        } finally {
            fos?.let {
                try {
                    fos.close()
                } catch (ioe: IOException) {
                    Log.e("DEBUG", ioe.message.orEmpty())
                }
            }
        }
    }
}