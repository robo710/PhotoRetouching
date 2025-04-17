package com.sonchan.photoretouching.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.util.Log
import kotlin.math.pow
import androidx.core.graphics.createBitmap
import com.sonchan.photoretouching.gpu.HighlightFilter
import jp.co.cyberagent.android.gpuimage.GPUImage

object ImageEditor {
    fun applyBrightness(bitmap: Bitmap, value: Int): Bitmap {
        val result =
            createBitmap(bitmap.width, bitmap.height, bitmap.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()

        val brightness = value * 2.55f

        Log.d("로그", "Brightness value: $brightness")

        val colorMatrix = ColorMatrix(
            floatArrayOf(
                1f, 0f, 0f, 0f, brightness,
                0f, 1f, 0f, 0f, brightness,
                0f, 0f, 1f, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
            )
        )

        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return result
    }

    fun applyExposure(bitmap: Bitmap, value: Int): Bitmap {
        val result =
            createBitmap(bitmap.width, bitmap.height, bitmap.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()

        val exposure = value / 100f * 1.5f
        val exposureScale = 2.0.pow(exposure.toDouble()).toFloat()

        Log.d("로그", "Exposure value: $exposureScale")

        val colorMatrix = ColorMatrix(
            floatArrayOf(
                exposureScale, 0f, 0f, 0f, 0f,
                0f, exposureScale, 0f, 0f, 0f,
                0f, 0f, exposureScale, 0f, 0f,
                0f, 0f, 0f, 1f, 0f
            )
        )

        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return result
    }

    fun applyConstruct(bitmap: Bitmap, value: Int): Bitmap {
        val result =
            createBitmap(bitmap.width, bitmap.height, bitmap.config ?: Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint()

        val construct = 1f + (value / 100f)
        val translate = 128f * (1 - construct)

        Log.d("로그", "construct value: $construct, translate value : $translate")

        val colorMatrix = ColorMatrix(
            floatArrayOf(
                construct, 0f, 0f, 0f, translate,
                0f, construct, 0f, 0f, translate,
                0f, 0f, construct, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            )
        )

        paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return result
    }

    fun applyHighlight(context: Context, bitmap: Bitmap, value: Int): Bitmap {
        val intensity = value / 150f
        val gpuImage = GPUImage(context)
        gpuImage.setImage(bitmap)
        gpuImage.setFilter(HighlightFilter(intensity))
        return gpuImage.bitmapWithFilterApplied
    }


    fun applyShadow(bitmap: Bitmap, value: Int): Bitmap {
        val shadowFactor = value / 300f
        val width = bitmap.width
        val height = bitmap.height
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)

        val pixels = IntArray(width * height)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        for (i in pixels.indices) {
            val pixel = pixels[i]

            val r = (pixel shr 16 and 0xFF).toFloat()
            val g = (pixel shr 8 and 0xFF).toFloat()
            val b = (pixel and 0xFF).toFloat()

            val brightness = (r + g + b) / 3
            val factor = if (brightness < 55) shadowFactor else 0f  // 어두운 영역만 강조

            // 어두운 영역 밝기 증가: 그림자 영역을 밝게
            val newR = (r + (255 - r) * factor).coerceIn(0f, 255f)
            val newG = (g + (255 - g) * factor).coerceIn(0f, 255f)
            val newB = (b + (255 - b) * factor).coerceIn(0f, 255f)

            val newColor = (0xFF shl 24) or
                    (newR.toInt() shl 16) or
                    (newG.toInt() shl 8) or
                    newB.toInt()

            pixels[i] = newColor
        }

        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }


}