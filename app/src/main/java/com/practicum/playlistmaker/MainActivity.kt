package com.practicum.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSearch = findViewById<Button>(R.id.btn_search)
        val btnLibrary = findViewById<Button>(R.id.btn_library)
        val btnSettings = findViewById<Button>(R.id.btn_settings)

        val btnSearchOnClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажали на кнопку поиска!", Toast.LENGTH_SHORT).show()
            }
        }

        btnSearch.setOnClickListener(btnSearchOnClickListener)

        btnLibrary.setOnClickListener{
            Toast.makeText(this@MainActivity, "Нажали на кнопку медиатеки!", Toast.LENGTH_SHORT).show()
        }

        btnSettings.setOnClickListener{
            Toast.makeText(this@MainActivity, "Нажали на кнопку настроек!", Toast.LENGTH_SHORT).show()
        }
    }
}