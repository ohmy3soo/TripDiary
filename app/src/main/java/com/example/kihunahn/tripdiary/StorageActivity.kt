package com.example.kihunahn.tripdiary

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_storage.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class StorageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)
        var url = "https://mplatform.seoul.go.kr/api/dule/courseBaseInfo.do"
        Getinformation().execute(url)
    }

    inner class Getinformation : AsyncTask<String, String, String>() {

        override fun onPreExecute() {
            // Before doInBackground
        }

        override fun doInBackground(vararg urls: String?): String {
            var urlConnection: HttpURLConnection? = null
            try {
                val url = URL(urls[0])
                urlConnection = url.openConnection() as HttpURLConnection

                var inString = streamToString(urlConnection.inputStream)
                //textView.setText(inString)
                publishProgress(inString)
            } catch (ex: Exception) {

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
            }
            return " "
        }
        override fun onProgressUpdate(vararg values: String?) {
            var str=""
            try {
                var json = JSONObject(values[0])
                var tmp = json.getJSONArray("list")
                for (i in 0..tmp.length()) {
                    val order = tmp.getJSONObject(i)
                    var location = order.getString("LOCATION")
                    var distance = order.getString("DISTANCE")
                    var time = order.getString("WALK_TIME")
                    var num = order.getString("COURSE_NO")
                    var level = order.getString("COURSE_LEVEL")
                    var name = order.getString("COURSE_NM")
                    str += location + " " +distance + " "+ time +" " + num +" "+level+" "+name + "\n"
                    textView.text = str
                }
            } catch (ex: Exception) {

            }
        }
    }
    fun streamToString(inputStream: InputStream): String {

        val bufferReader = BufferedReader(InputStreamReader(inputStream))
        var line: String
        var result = ""

        try {
            do {
                line = bufferReader.readLine()
                if (line != null) {
                    result += line
                }
            } while (line != null)
            inputStream.close()
        } catch (ex: Exception) {

        }

        return result
    }
}