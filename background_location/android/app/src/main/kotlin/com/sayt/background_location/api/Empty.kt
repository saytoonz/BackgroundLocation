package com.sayt.background_location.api

import android.R
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HttpsURLConnection


class Empty(urlString: String) :
    AsyncTask<String?, Void?, Boolean>() {
    private val TAG = "post json example"
    private var urlString: String = urlString


    override fun onPreExecute() {
        Log.e(TAG, "1 - RequestVoteTask is about to start...")
    }

     override fun doInBackground(vararg p0: String?): Boolean {
         val data = p0[0]
         var status = false
        var response = ""
        Log.e(TAG, "2 - pre Request to response...")
        try {
            response = data?.let { performPostCall(urlString, it) }.toString()
            Log.e(TAG, "3 - give Response...")
            Log.e(TAG, "4 $response")
        } catch (e: Exception) {
            // displayLoding(false);
            Log.e(TAG, "Error ...")
        }
        Log.e(TAG, "5 - after Response...")
        if (!response.equals("", ignoreCase = true)) {
            try {
                Log.e(TAG, "6 - response !empty...")
                //
                val jRoot = JSONObject(response)
                val d = jRoot.getJSONObject("d")
                val ResultType = d.getInt("ResultType")
                Log.e("ResultType", ResultType.toString() + "")
                if (ResultType == 1) {
                    status = true
                }
            } catch (e: JSONException) {
                Log.e(TAG, "Error " + e.message)
            } finally {
            }
        } else {
            Log.e(TAG, "6 - response is empty...")
            status = false
        }
        return status
    }

    override fun onPostExecute(result: Boolean) {
        //
        Log.e(TAG, "7 - onPostExecute ...")
        if (result) {
            Log.e(TAG, "8 - Update UI ...")
            // setUpdateUI(adv);
        } else {
            Log.e(TAG, "8 - Finish ...");
        }
    }

    fun performPostCall(
        requestURL: String,
        postDataParams: String
    ): String {
        val url: URL
        var response = ""
        try {
            url = URL(requestURL)
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
            conn.readTimeout = 15000
            conn.connectTimeout = 15000
            conn.requestMethod = "POST"
            conn.doInput = true
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "application/json")
            Log.e(TAG, "11 - url : ${url.host}")
            Log.e(TAG, "111 - url : ${url}")

            Log.e(TAG, "12 - root : $postDataParams")
            val outputBytes = postDataParams.toByteArray(charset("UTF-8"))
            val os: OutputStream = conn.outputStream
            os.write(outputBytes)
            val responseCode: Int = conn.responseCode
            Log.e(TAG, "13 - responseCode : $responseCode")
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                Log.e(TAG, "14 - HTTP_OK")
                var line: String
                val br = BufferedReader(
                    InputStreamReader(
                        conn.inputStream
                    )
                )
                while (br.readLine().also { line = it } != null) {
                    response += line
                }
            } else {
                Log.e(TAG, "14 - False - HTTP_OK")
                response = ""
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return response
    }

}
