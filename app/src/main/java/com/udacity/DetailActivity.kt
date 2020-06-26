package com.udacity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val libraryName = intent.getStringExtra("library_name")

        tv_file_name.text = libraryName
        tv_status.text = getString(R.string.success_status)

        btn_ok.setOnClickListener {
            // Go back to the main screen via an intent
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
