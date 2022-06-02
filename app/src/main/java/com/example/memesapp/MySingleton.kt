package com.example.memesapp

import android.content.Context
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.example.memesapp.MySingleton
import kotlin.jvm.Synchronized
import android.graphics.Bitmap
import com.android.volley.Request
import com.android.volley.toolbox.ImageLoader
import com.bumptech.glide.util.LruCache

class MySingleton private constructor(private var ctx: Context) {
    private var requestQueue: RequestQueue?
    private val imageLoader: ImageLoader
    private fun getRequestQueue(): RequestQueue {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.applicationContext)
        }
        return requestQueue as RequestQueue
    }

    fun <T> addToRequestQueue(req: Request<T>?) {
        getRequestQueue().add(req)
    }

    companion object {
        private var instance: MySingleton? = null
        @Synchronized
        fun getInstance(context: Context): MySingleton? {
            if (instance == null) {
                instance = MySingleton(context)
            }
            return instance
        }
    }

    init {
        requestQueue = getRequestQueue()
        imageLoader = ImageLoader(requestQueue,
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(20)
                override fun getBitmap(url: String): Bitmap? {
                    return cache[url]
                }

                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })
    }
}