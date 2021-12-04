package com.example.myapplication

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class BlankFragment : Fragment() {

    var btnRetroLoader: Button? = null
    var btnHttpLoader: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_blank, container, false)

        btnRetroLoader = view.findViewById(R.id.btn_retrofit_loader)
        btnHttpLoader = view.findViewById(R.id.btn_http_loader)
        imgView = view.findViewById(R.id.img_retro_loader)

        btnRetroLoader?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                getImageFromAPI()
            }
        })

        btnHttpLoader?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {

                val dt: DownloadImageTask =
                    DownloadImageTask((view.findViewById<View>(R.id.img_http_loader) as ImageView?)!!).execute(
                        Url
                    ) as DownloadImageTask
            }
        })
        return view
    }

    //Image loader from URL using Retrofit
    fun getImageFromAPI() {
        fun getRetrofitInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl("https://liliumflorals.com")
                .build()
        }

        fun getApiService(): ApiInterface {
            return getRetrofitInstance().create(ApiInterface::class.java)
        }

        val api = getApiService()
        val call: Call<ResponseBody> = api.getImage(Url)
        call?.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {

                        println(response.body()!!.byteStream())
                        val bmp = BitmapFactory.decodeStream(response.body()!!.byteStream())
                        imgView?.setImageBitmap(bmp)

                    }
                }
            }

            override fun onFailure(
                call: Call<ResponseBody?>,
                t: Throwable
            ) {

            }
        })
    }

    //Image loader from URL using asynctask to avoid UI network thread exception
    private class DownloadImageTask(var bmImage: ImageView) :
        AsyncTask<String?, Void?, Bitmap?>() {
        protected override fun doInBackground(vararg params: String?): Bitmap? {
            val urldisplay = Url
            var mBitmap: Bitmap? = null
            var stream: InputStream? = null
            try {
                stream = getHttpConnection(urldisplay)
                val ins = URL(urldisplay).openStream()
                mBitmap = BitmapFactory.decodeStream(ins)
                stream!!.close()
            } catch (e: java.lang.Exception) {
                Log.e("Error", e.message!!)
                e.printStackTrace()
            }
            return mBitmap
        }

        // Makes HttpURLConnection and returns InputStream
        @Throws(IOException::class)
        private fun getHttpConnection(urlString: String): InputStream? {
            var stream: InputStream? = null
            val url = URL(urlString)
            val connection = url.openConnection()
            try {
                val httpConnection =
                    connection as HttpURLConnection
                httpConnection.requestMethod = "GET"
                httpConnection.connect()
                if (httpConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.inputStream
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return stream
        }

        override fun onPostExecute(result: Bitmap?) {
            bmImage.setImageBitmap(result)
        }
    }

    companion object {
        val Url = "https://liliumflorals.com/wp-content/uploads/2015/01/Goodnight-Moon-700x394.jpg"
        var imgView: ImageView? = null
    }

}