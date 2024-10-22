package com.example.samid

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class WebsocketImplementation : AppCompatActivity() {
    private lateinit var bpmTextView: TextView
    private lateinit var spo2TextView: TextView
    private lateinit var webSocket: WebSocket
    private lateinit var heartRateChart: LineChart

    private val heartRateEntries = mutableListOf<Entry>()
    private var dataIndex = 0

    private var isUpdating = false // To avoid overlapping updates

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        heartRateChart = findViewById(R.id.heartRateChartView)
        bpmTextView = findViewById(R.id.bpmTextView)
        spo2TextView = findViewById(R.id.spo2TextView)

        setupCharts()
        setupWebSocket()

        // Set up the back button
        val backButton = findViewById<ImageView>(R.id.flecha) // replace with the actual ID of your back button
        backButton.setOnClickListener {
            finish() // This will finish the current activity and go back to the previous one
        }
    }

    private fun setupWebSocket() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("ws://192.168.0.115:8081") // Update this URL if needed
            .build()

        val webSocketListener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d("WebSocket", "Connection established")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                Log.d("WebSocket", "Message received: $text")
                try {
                    val jsonData = JSONObject(text)
                    if (jsonData.has("beat")) {
                        val beatValue = jsonData.getInt("beat")
                        updateHeartRateChart(beatValue) // Update chart with beat value
                    } else if (jsonData.has("average_bpm") && jsonData.has("average_spo2")) {
                        // Handle final average output from ESP32
                        val avgBpm = jsonData.getInt("average_bpm")
                        val avgSpo2 = jsonData.getInt("average_spo2")

                        // Log the received average values
                        Log.d("WebSocket", "Average BPM: $avgBpm, Average SpO2: $avgSpo2")

                        // Update the BPM and SpO2 TextViews
                        runOnUiThread {
                            updateBpmAndSpo2(avgBpm, avgSpo2) // Call to update BPM and SpO2
                        }

                        // Optionally update the heart rate chart here to reflect final state
                        updateHeartRateChart(0) // or any logic to reflect the final state
                    }
                } catch (e: Exception) {
                    Log.e("WebSocket", "Error processing message: ${e.message}")
                }
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                // Handle binary messages (if any)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                Log.d("WebSocket", "Closing connection: $code, $reason")
                webSocket.close(1000, null)
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                Log.e("WebSocket", "Error in WebSocket: ${t.message}")
            }
        }

        Log.d("WebSocket", "Attempting to connect...")
        webSocket = client.newWebSocket(request, webSocketListener)
    }

    private fun setupCharts() {
        heartRateChart.axisRight.isEnabled = false // Disable the right Y-axis
        heartRateChart.setPinchZoom(false) // Disable pinch zoom
        heartRateChart.setDrawGridBackground(false) // No background grid
        heartRateChart.axisLeft.setDrawGridLines(false) // Disable Y-axis gridlines
        heartRateChart.xAxis.setDrawGridLines(false) // Disable X-axis gridlines
        heartRateChart.legend.isEnabled = false // Disable the legend
        heartRateChart.description.isEnabled = false // Disable description
        heartRateChart.axisLeft.setDrawLabels(false) // Disable Y-axis labels
        heartRateChart.xAxis.setDrawLabels(false) // Disable X-axis labels

        // Set the visible X range, e.g., display the last 100 data points
        heartRateChart.setVisibleXRangeMaximum(100f) // Show 100 points at a time
        heartRateChart.setVisibleXRangeMinimum(100f) // Fix the window to 100 points
        heartRateChart.moveViewToX(0f) // Start at the beginning of the chart
    }

    private fun updateHeartRateChart(beatValue: Int) {
        if (!isUpdating) { // Avoid overlapping updates
            isUpdating = true

            GlobalScope.launch(Dispatchers.Main) {
                if (beatValue == 1) {
                    // Add a point before the beat
                    heartRateEntries.add(Entry(dataIndex.toFloat(), 0f))
                    // Add the beat
                    heartRateEntries.add(Entry(dataIndex.toFloat(), 1f))

                    // Update the chart immediately
                    updateChart()

                    // Simulate a delay for the animation
                    delay(200)

                    // Add more points after the beat, moving back to baseline (0)
                    for (i in 1..10) {
                        heartRateEntries.add(Entry((dataIndex + i).toFloat(), 0f))
                        updateChart() // Update chart on each step
                        delay(10) // Adjust this delay for speed control
                    }

                    // Increment dataIndex for the next beat, skipping some spaces
                    dataIndex += 10
                } else {
                    // Increment dataIndex even when no beat is detected
                    dataIndex++
                }

                // Shift the chart's visible area to the latest data point
                heartRateChart.moveViewToX(dataIndex.toFloat() - 100) // Keep 100 points visible and move the chart left

                // Animate the removal of old data points
                animateBeatRemoval()

                isUpdating = false // Reset updating state
            }
        }
    }

    private fun animateBeatRemoval() {
        // Ensure this runs only if we have entries to remove
        if (heartRateEntries.size > 100) { // Only remove if more than 100 points exist
            GlobalScope.launch(Dispatchers.Main) {
                // Loop to remove the oldest beat gradually (smooth transition)
                for (i in 0 until 30) { // Adjust 5 to control how many steps the animation takes
                    if (heartRateEntries.isNotEmpty()) {
                        heartRateEntries.removeAt(0) // Remove the oldest entry (leftmost)
                        updateChart() // Refresh chart after each removal

                        delay(100) // Adjust the delay for smoother or faster animation
                    }
                }
            }
        }
    }

    private fun updateChart() {
        runOnUiThread {
            // Create a LineDataSet and update the chart
            val heartRateDataSet = LineDataSet(heartRateEntries, "Heart Rate").apply {
                color = Color.RED // Set the line color to red
                lineWidth = 5f // Make the line thicker for better visibility
                setDrawCircles(false) // Disable circles on data points
                setDrawFilled(false) // Disable filling the area under the line
            }

            // Set the updated data to the chart
            heartRateChart.data = LineData(heartRateDataSet)
            heartRateChart.invalidate() // Refresh the chart
        }
    }

    private fun updateBpmAndSpo2(avgBpm: Int?, avgSpo2: Int?) {
        // Update the BPM and SpO2 text views if final output is provided
        avgBpm?.let {
            bpmTextView.text = it.toString()
        }

        avgSpo2?.let {
            spo2TextView.text = it.toString()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, null) // Close the WebSocket connection when activity is destroyed
    }
}
