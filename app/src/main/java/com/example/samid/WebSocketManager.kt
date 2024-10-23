package com.example.samid

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit

object WebSocketManager {
    private var webSocket: WebSocket? = null

    fun connect(client: OkHttpClient, url: String, listener: WebSocketListener): WebSocket {
        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, listener)
        return webSocket!!
    }

    fun getWebSocket(): WebSocket? {
        return webSocket
    }

    fun closeWebSocket() {
        webSocket?.close(1000, null)
        webSocket = null
    }
}
