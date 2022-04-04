package com.sayt.background_location.api

import android.os.AsyncTask
import android.util.Log
import java.io.BufferedOutputStream
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


class CallAPI : AsyncTask<String?, String?, String>() {

    override fun doInBackground(vararg p0: String?): String {
        val urlString = p0[0] // URL to call
        val data = p0[1] //data to post
        var out: OutputStream? = null

        urlString?.let { Log.e("TAG___", it) }
        try {
            val url = URL(urlString)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.readTimeout = 15000
            urlConnection.connectTimeout = 15000
            urlConnection.requestMethod = "POST"
            urlConnection.doInput = true
            urlConnection.doOutput = true
            out = BufferedOutputStream(urlConnection.outputStream)
            val writer = BufferedWriter(OutputStreamWriter(out, "UTF-8"))
            writer.write(data)
            writer.flush()
            writer.close()
            out.close()
            urlConnection.connect()
        } catch (e: Exception) {
            println(e.message)
        }
        return ""
    }
}