package com.bogdandovgopol.weather.Services

import com.bogdandovgopol.weather.Helpers.Constants
import com.bogdandovgopol.weather.Models.DarkSkyModels.Forecast
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.util.concurrent.ArrayBlockingQueue

class DarkSky {

    private var url: String? = null

    fun getForecast(lat: Double, long: Double): ArrayBlockingQueue<Forecast>? {
        this.url = "${Constants.DARK_SKY_BASE_URL}/${Constants.DARK_SKY_KEY}/$lat,$long?exclude=hourly,flags,offset&units=si"

        val request = Request.Builder().url(url!!).build()

        val blockingQueue: ArrayBlockingQueue<Forecast> = ArrayBlockingQueue(1)

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                println("error")
            }

            override fun onResponse(call: Call, response: Response) {

                if (response.isSuccessful) {

                    val body = response.body()?.string()

                    val gson = GsonBuilder().create()
                    val forecast = gson.fromJson(body, Forecast::class.java)

                    blockingQueue.add(forecast)
                }
            }
        })

        return blockingQueue
    }

}
