package com.example.samid

import android.content.SharedPreferences
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
    private lateinit var spo2Chart: LineChart
    private var spo2Entries = ArrayList<Entry>()
    private var spo2Index = 0
    private val heartRateEntries = mutableListOf<Entry>()
    private var dataIndex = 0
    private var isUpdating = false // To avoid overlapping updates
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // Initialize UI components
        spo2Chart = findViewById(R.id.spo2ChartView)
        setupSpo2Chart()
        heartRateChart = findViewById(R.id.heartRateChartView)
        bpmTextView = findViewById(R.id.bpmTextView)
        spo2TextView = findViewById(R.id.spo2TextView)
        sharedPreferences = getSharedPreferences("HealthData", MODE_PRIVATE)


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

                    // Check for "final_bpm" and "final_spo2"
                    if (jsonData.has("final_bpm") && jsonData.has("final_spo2")) {
                        val avgBpm = jsonData.getInt("final_bpm") // Use the correct key
                        val avgSpo2 = jsonData.getInt("final_spo2") // Use the correct key

                        Log.d("WebSocket", "Average BPM: $avgBpm, Average SpO2: $avgSpo2")

                        // Update UI on the main thread
                        runOnUiThread {
                            updateBpmAndSpo2(avgBpm, avgSpo2)
                        }
                    } else if (jsonData.has("beat")) {
                        val beatValue = jsonData.getInt("beat")
                        updateHeartRateChart(beatValue) // Update chart with beat value
                    } else if (jsonData.has("spo2")) {
                        val spo2Value = jsonData.getInt("spo2")
                        runOnUiThread {
                            updateSpo2Chart(spo2Value) // Update SpO2 chart with new value
                        }
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

    private fun saveHealthData(bpm: Int, spo2: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("last_bpm", bpm)
        editor.putInt("last_spo2", spo2)
        editor.apply()
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
                setDrawValues(false) // Disable numbers on the data points
            }

            // Remove description and legend
            heartRateChart.description.isEnabled = false
            heartRateChart.legend.isEnabled = false

            // Set the updated data to the chart
            heartRateChart.data = LineData(heartRateDataSet)
            heartRateChart.invalidate() // Refresh the chart
        }
    }

    private fun updateBpmAndSpo2(avgBpm: Int?, avgSpo2: Int?) {
        runOnUiThread {
            bpmTextView.text = avgBpm?.toString() ?: "N/A"
            spo2TextView.text = avgSpo2?.toString() ?: "N/A"

            // Save the values to SharedPreferences
            if (avgBpm != null && avgSpo2 != null) {
                saveHealthData(avgBpm, avgSpo2)
            }
        }
    }

    private fun setupSpo2Chart() {
        // Add an initial entry with a value of 0
        spo2Entries.add(Entry(0f, 0f)) // Initialize chart with a 0 value

        val spo2DataSet = LineDataSet(spo2Entries, "SpO2 Level").apply {
            color = Color.BLUE
            lineWidth = 2f
            setDrawCircles(false)
            setDrawValues(false) // Disable numbers on the data points
        }

        spo2Chart.data = LineData(spo2DataSet)
        spo2Chart.setBackgroundColor(Color.WHITE)
        spo2Chart.setNoDataText("No SpO2 data yet")

        // Remove description and legend
        spo2Chart.description.isEnabled = false
        spo2Chart.legend.isEnabled = false

        spo2Chart.invalidate()
    }



    private fun updateSpo2Chart(spo2Value: Int) {
        if (spo2Index > 100) {
            spo2Entries.removeAt(0) // Remove the oldest value if we have more than 100
        }
        spo2Entries.add(Entry(spo2Index++.toFloat(), spo2Value.toFloat()))
        val spo2DataSet = LineDataSet(spo2Entries, "SpO2 Level").apply {
            color = Color.BLUE
            lineWidth = 2f
            setDrawCircles(false)
            setDrawFilled(false)
        }

        // Update the dataset and chart
        spo2Chart.data = LineData(spo2DataSet)
        spo2Chart.data.notifyDataChanged()
        spo2Chart.notifyDataSetChanged()
        spo2Chart.invalidate()

        // Scroll the chart to the latest data point
        spo2Chart.moveViewToX(spo2Index.toFloat() - 100)
    }


    override fun onDestroy() {
        super.onDestroy()
        webSocket.close(1000, null) // Close the WebSocket connection when activity is destroyed
    }
}
