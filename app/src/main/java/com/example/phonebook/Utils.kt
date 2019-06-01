package com.example.phonebook

import android.graphics.Bitmap

class Utils {
    companion object {
        /**
         * reduces the size of the image
         * @param image
         * @param maxSize
         * @return
         */
        fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {
            var width = image.width
            var height = image.height

            val bitmapRatio = width.toFloat() / height.toFloat()
            if (bitmapRatio > 1) {
                width = maxSize
                height = (width / bitmapRatio).toInt()
            } else {
                height = maxSize
                width = (height * bitmapRatio).toInt()
            }
            return Bitmap.createScaledBitmap(image, width, height, true)
        }
    }
}