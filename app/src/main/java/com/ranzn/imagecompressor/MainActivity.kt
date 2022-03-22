package com.ranzn.imagecompressor

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ranjan"
    }


    private lateinit var realImage: Bitmap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Selecting Image through Library
        uploadBtn.setOnClickListener {
            ImagePicker.with(this).start()
        }


        //Compress Image Button
        compressImage.setOnClickListener {
            //get the compressed image
            val newImage = scaleBitmap(realImage, 1024 * 1024 * 2)
            //display compress image details
            newImage?.apply {
                val size: Double = byteCount / (1024 * 1024).toDouble()
                Log.d(Companion.TAG, "newSize: $size")
                Log.d(TAG, "newWidth: $width")
                Log.d(TAG, "newHeight: $height")
            }
            Log.d(TAG, "---------------------------------------------------------")
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                //getting image uri form intent data
                val uri = data?.data!!
                imageView.setImageURI(uri)

                //converting same image uri to bitmap
                realImage = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)

                //displaying image details
                realImage.apply {
                    val size: Double = byteCount / (1024 * 1024).toDouble()
                    Log.d(Companion.TAG, "originalSize: $size")
                    Log.d(TAG, "originalWidth: ${realImage.width}")
                    Log.d(TAG, "originalHeight: ${realImage.height}")
                }
            }
            ImagePicker.RESULT_ERROR -> {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
            }
            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    }


    /**
     * Main method to compress image...
     * but don't know how it worked.
     */
    private fun scaleBitmap(input: Bitmap, maxBytes: Long): Bitmap? {
        val currentWidth = input.width
        val currentHeight = input.height
        val currentPixels = currentWidth * currentHeight
        // Get the amount of max pixels:
        // 1 pixel = 4 bytes (R, G, B, A)
        val maxPixels = maxBytes / 4 // Floored
        if (currentPixels <= maxPixels) {
            // Already correct size:
            return input
        }
        // Scaling factor when maintaining aspect ratio is the square root since x and y have a relation:
        val scaleFactor = Math.sqrt(maxPixels / currentPixels.toDouble())
        val newWidthPx = Math.floor(currentWidth * scaleFactor).toInt()
        val newHeightPx = Math.floor(currentHeight * scaleFactor).toInt()

        return Bitmap.createScaledBitmap(input, newWidthPx, newHeightPx, true)
    }


}