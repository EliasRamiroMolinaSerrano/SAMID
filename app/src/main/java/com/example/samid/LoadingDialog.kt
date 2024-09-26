package com.example.samid

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater

class LoadingDialog(private val activity: Activity) {

    private lateinit var dialog: AlertDialog

    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.custom_dialog_loading, null))
        builder.setCancelable(true)

        dialog = builder.create()
        dialog.show()
    }

    fun dismissDialog() {
        if (::dialog.isInitialized) {
            dialog.dismiss()
        }
    }
}
