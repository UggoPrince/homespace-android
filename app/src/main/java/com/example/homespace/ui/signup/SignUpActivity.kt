package com.example.homespace.ui.signup

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.homespace.R
import com.example.homespace.databinding.ActivitySignUpBinding


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sign_up)
        val toolBar = findViewById<Toolbar>(R.id.signupToolbar)
        toolBar.setNavigationOnClickListener {
            // back button pressed
            finish()
        }
    }
}