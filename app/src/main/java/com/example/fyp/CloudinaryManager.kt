package com.example.fyp

import android.content.Context
import android.net.Uri
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import java.io.InputStream
import java.util.concurrent.Executors

object CloudinaryManager {
    private const val CLOUD_NAME = "drhdp0t9j"  // Replace with your Cloudinary Cloud Name
    private const val API_KEY = "851978611266154"  // Replace with your Cloudinary API Key
    private const val API_SECRET = "ZinCg2tp98pVpfVKbn0lcE5A8DA"  // Replace with your Cloudinary API Secret

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to CLOUD_NAME,
            "api_key" to API_KEY,
            "api_secret" to API_SECRET
        )
    )

    private val executorService = Executors.newSingleThreadExecutor()

    fun uploadImage(context: Context, fileUri: Uri, callback: (String?) -> Unit) {
        val inputStream: InputStream? = context.contentResolver.openInputStream(fileUri)

        if (inputStream == null) {
            callback(null)
            return
        }

        executorService.execute {
            try {
                val uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.emptyMap())
                val imageUrl = uploadResult["secure_url"] as String?
                callback(imageUrl)
            } catch (e: Exception) {
                e.printStackTrace()
                callback(null)
            }
        }
    }
}
