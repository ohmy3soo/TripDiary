package com.example.kihunahn.tripdiary

import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ListView
import com.example.kihunahn.tripdiary.adapter.giladpater
import com.example.kihunahn.tripdiary.inform.gilinform
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class StorageActivity : AppCompatActivity() {
    var listView: ListView? =  null
    var adapter: giladpater? = null
    var result = ArrayList<gilinform>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)
        var url = "https://mplatform.seoul.go.kr/api/dule/courseBaseInfo.do"
        listView = findViewById(R.id.listView)

        Getinformation().execute(url)
        adapter = giladpater(this, result)

        listView?.adapter = adapter
        adapter?.notifyDataSetChanged()
    }

    inner class Getinformation : AsyncTask<String, String, String>(){

        override fun onPreExecute() {
            // Before doInBackground
        }

        override fun doInBackground(vararg urls: String?): String {
            var urlConnection: HttpURLConnection? = null
            try {
                val url = URL(urls[0])
                urlConnection = url.openConnection() as HttpURLConnection

                var inString = streamToString(urlConnection.inputStream)

                var str=""
                try {
                    var json = JSONObject(inString)
                    var tmp = json.getJSONArray("list")
                    for (i in 0..tmp.length()) {
                        val order = tmp.getJSONObject(i)
                        var location = order.getString("LOCATION")
                        var distance = order.getString("DISTANCE")
                        var time = order.getString("WALK_TIME")
                        var num = order.getString("COURSE_NO")
                        var level = order.getString("COURSE_LEVEL")
                        var name = order.getString("COURSE_NM")
                        str = location + " " + distance + " " + time + " " + num + " " + level + " " + name + "\n"
                        var user: gilinform = gilinform(str)
                        result.add(user)
                    }
                } catch (ex: Exception) {

                }
                //publishProgress(inString)
            } catch (ex: Exception) {

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
            }
            return " "
        }
        override fun onProgressUpdate(vararg values: String?) {

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