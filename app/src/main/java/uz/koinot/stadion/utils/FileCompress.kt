package uz.koinot.stadion.utils

import android.content.Context
import android.graphics.*
import android.media.ExifInterface
import android.util.Log
import id.zelory.compressor.calculateInSampleSize
import me.shaohui.advancedluban.Luban
import me.shaohui.advancedluban.OnCompressListener
import java.io.*

fun File.compress(context: Context, callback: (File) -> Unit) {
    var output: File
    Luban.compress(context, this)
        .setMaxSize(800) //maximum output size in kb
        .setMaxWidth(700) //maximum output image with
        .setMaxHeight(700) //maximum output image height
        .setCompressFormat(Bitmap.CompressFormat.PNG)
        .putGear(Luban.CUSTOM_GEAR).launch(object : OnCompressListener {
            override fun onSuccess(file: File?) {
                callback.invoke(file!!)
            }

            override fun onError(e: Throwable?) {
                
                Log.e("Luban", e!!.message!!)
            }

            override fun onStart() {
            }
        })
}
fun File.compress2(context: Context): File {
    val maxHeight = 1920.0f
    val maxWidth = 1080.0f
    var scaledBitmap: Bitmap? = null
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    var bmp = BitmapFactory.decodeFile(this.path, options)
    var actualHeight = options.outHeight
    var actualWidth = options.outWidth
    var imgRatio = actualWidth.toFloat() / actualHeight.toFloat()
    val maxRatio = maxWidth / maxHeight
    if (actualHeight > maxHeight || actualWidth > maxWidth) {
        when {
            imgRatio < maxRatio -> {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            }
            imgRatio > maxRatio -> {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            }
            else -> {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }
    }
    options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
    options.inJustDecodeBounds = false
    options.inTempStorage = ByteArray(16 * 1024)
    try {
        bmp = BitmapFactory.decodeFile(this.path, options)
    } catch (exception: OutOfMemoryError) {
        exception.printStackTrace()
    }
    try {
        scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
    } catch (exception: OutOfMemoryError) {
        exception.printStackTrace()
    }
    val ratioX = actualWidth / options.outWidth.toFloat()
    val ratioY = actualHeight / options.outHeight.toFloat()
    val middleX = actualWidth / 2.0f
    val middleY = actualHeight / 2.0f
    val scaleMatrix = Matrix()
    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
    val canvas = Canvas(scaledBitmap!!)
    canvas.setMatrix(scaleMatrix)
    canvas.drawBitmap(
        bmp,
        middleX - bmp.width / 2,
        middleY - bmp.height / 2,
        Paint(Paint.FILTER_BITMAP_FLAG)
    )
    val exif: ExifInterface?
    try {
        exif = ExifInterface(this.path)
        val orientation: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)
        val matrix = Matrix()
        when (orientation) {
            6 -> {
                matrix.postRotate(90F)
            }
            3 -> {
                matrix.postRotate(180F)
            }
            8 -> {
                matrix.postRotate(270F)
            }
        }
        scaledBitmap = Bitmap.createBitmap(
            scaledBitmap,
            0,
            0,
            scaledBitmap.width,
            scaledBitmap.height,
            matrix,
            true
        )
    } catch (e: IOException) {
        e.printStackTrace()
    }
    val out = ByteArrayOutputStream()
    scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 75, out)
    val output = File(context.cacheDir, "filename.jpg")
    output.createNewFile()
    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(output)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    try {
        fos!!.write(out.toByteArray())
        fos.flush()
        fos.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return output
}