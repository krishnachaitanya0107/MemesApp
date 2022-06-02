package com.example.memesapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.memesapp.R
import com.android.volley.toolbox.JsonObjectRequest
import org.json.JSONObject
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestListener
import android.graphics.drawable.Drawable
import com.bumptech.glide.load.engine.GlideException
import org.json.JSONException
import com.android.volley.VolleyError
import android.widget.Toast
import com.example.memesapp.MySingleton
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.graphics.drawable.toBitmap
import com.android.volley.Request
import com.android.volley.Response
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.target.Target

class MainActivity : AppCompatActivity() {
    var imageView: ImageView? = null
    var progressBar: ProgressBar? = null
    var memeUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById(R.id.progressbar)
        imageView = findViewById(R.id.memeImageView)
        loadMeme()
    }

    private fun loadMeme() {
        progressBar!!.visibility = View.VISIBLE
        val url = "https://meme-api.herokuapp.com/gimme"

        // Request a string response from the provided URL.
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET,
            url, null,
            { response: JSONObject ->
                try {
                    memeUrl = response.getString("url")
                    Glide.with(this@MainActivity).load(memeUrl)
                        .listener(object : RequestListener<Drawable?> {
                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any,
                                target: Target<Drawable?>,
                                dataSource: DataSource,
                                isFirstResource: Boolean
                            ): Boolean {
                                progressBar!!.visibility = View.GONE
                                return false
                            }

                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any, target: Target<Drawable?>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                progressBar!!.visibility = View.GONE
                                return false
                            }
                        }).into(imageView!!)

                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }) { error: VolleyError ->
            Toast.makeText(this@MainActivity, "Error loading image", Toast.LENGTH_SHORT).show()
            error.printStackTrace()
        }

        // Add the request to the RequestQueue.
        MySingleton.getInstance(this)?.addToRequestQueue(jsonObjectRequest)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.download) {
            if (memeUrl!!.contains("jpg")) {
                download("" + System.currentTimeMillis() + ".jpg", Uri.parse(memeUrl))
            } else if (memeUrl!!.contains("gif")) {
                download("" + System.currentTimeMillis() + ".gif", Uri.parse(memeUrl))
            } else if (memeUrl!!.contains("png")) {
                download("" + System.currentTimeMillis() + ".png", Uri.parse(memeUrl))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun download(fileName: String?, uri: Uri?) {
        if (memeUrl == null) {
            Toast.makeText(this, "Please wait for image to load ...", Toast.LENGTH_SHORT).show()
            return
        }
        val request: DownloadManager.Request = DownloadManager.Request(uri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        request.setTitle("Download")
        request.setDescription(fileName)
        request.allowScanningByMediaScanner()
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            fileName
        )
        val manager = applicationContext.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        manager.enqueue(request)
        Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show()
    }

    fun shareMeme(view: View?) {
        if (memeUrl == null) {
            Toast.makeText(this, "Please wait for image to load ...", Toast.LENGTH_SHORT)
                .show()
        } else {
            val intent = Intent(Intent.ACTION_SEND)
            if (memeUrl!!.contains("gif")) {
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, "Hey , check out this meme $memeUrl")
                val chooserTarget = Intent.createChooser(intent, "Share this meme using ")
                startActivity(chooserTarget)
            } else if (memeUrl!!.contains("jpg") || memeUrl!!.contains("png")) {
                intent.type = "image/*"
                val bitmap = imageView!!.drawable.toBitmap()
                val path = MediaStore.Images.Media.insertImage(contentResolver, bitmap, "img", null)
                val uri = Uri.parse(path)
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.putExtra(Intent.EXTRA_TEXT, "Hey , check out this meme $memeUrl")
                val chooserTarget = Intent.createChooser(intent, "Share this meme using ")
                startActivity(chooserTarget)
            }
        }
    }

    fun nextMeme(view: View?) {
        loadMeme()
    }
}