package com.example.samid

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class CheckNow : AppCompatActivity() {

    private lateinit var checkBtn: Button
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.check_now)

        checkBtn = findViewById(R.id.check_btn)
        loadingDialog = LoadingDialog(this)  // Inicializamos el LoadingDialog con la actividad actual

        checkBtn.setOnClickListener {
            loadingDialog.startLoadingDialog()  // Mostramos el diálogo de carga al hacer clic en el botón
        }
    }
}
