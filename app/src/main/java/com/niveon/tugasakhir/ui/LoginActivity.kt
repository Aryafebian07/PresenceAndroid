package com.niveon.tugasakhir.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.niveon.tugasakhir.R
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.Observer
import com.niveon.tugasakhir.MainActivity
import com.niveon.tugasakhir.databinding.ActivityLoginBinding
import com.niveon.tugasakhir.model.AdminResponse
import com.niveon.tugasakhir.model.DosenResponse
import com.niveon.tugasakhir.model.MahasiswaResponse
import com.niveon.tugasakhir.ui.viewmodel.LoginViewModel
import com.niveon.tugasakhir.ui.viewmodel.SharedViewModel
import com.niveon.tugasakhir.util.SharedData

class LoginActivity : AppCompatActivity() {
    val TAG = "LoginActivity"
    private lateinit var binding : ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    private lateinit var sharedViewModel: SharedViewModel
    lateinit var etUsername : EditText
    lateinit var etPassword : EditText
    lateinit var btnLogin : Button
    private var userRole: String? = null

    private val loginResultObserver = Observer<Boolean> { isLoggedIn ->
        if (isLoggedIn) {
            when {
                viewModel.adminResult.value != null -> {
                    Log.d(TAG, "as Admin")
                    val adminResponse = viewModel.adminResult.value
                    val successMessage = "Login berhasil sebagai ${adminResponse?.nama ?: ""}"
                    Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
                    viewModel.adminResult.value?.let { adminResponse ->
                        sharedViewModel.setAdminResponse(adminResponse)
                    }
                    SharedData.adminResponse = adminResponse
                    navigateToMainActivity("admin")
                }
                viewModel.mahasiswaResult.value != null -> {
                    Log.d(TAG, "as Mahasiswa")
                    val mahasiswaResponse = viewModel.mahasiswaResult.value
                    val successMessage = "Login berhasil sebagai ${mahasiswaResponse?.nama ?: ""}"
                    Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
                    viewModel.mahasiswaResult.value?.let { mahasiswaResponse ->
                        sharedViewModel.setMahasiswaResponse(mahasiswaResponse)
                    }
                    SharedData.mahasiswaResponse = mahasiswaResponse
                    navigateToMainActivity("mahasiswa")
                }
                viewModel.dosenResult.value != null -> {
                    Log.d(TAG, "as Dosen")
                    val dosenResponse = viewModel.dosenResult.value
                    val successMessage = "Login berhasil sebagai ${dosenResponse?.nama ?: ""}"
                    Toast.makeText(this, successMessage, Toast.LENGTH_SHORT).show()
                    viewModel.dosenResult.value?.let { dosenResponse ->
                        sharedViewModel.setDosenResponse(dosenResponse)
                    }
                    SharedData.dosenResponse = dosenResponse
                    navigateToMainActivity("dosen")
                }
                else -> {
                    Toast.makeText(this, "Peran pengguna tidak valid", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            val errorMessage = viewModel.loginResponseMessage.value ?: "Login gagal"
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fade_in: Animation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val bottom_down: Animation = AnimationUtils.loadAnimation(this, R.anim.bottom_down)

        binding.topLinearLayout.animation = bottom_down
        val handler = Handler()
        val runnable = Runnable {
            binding.cardView.animation = fade_in
            binding.cardView2.animation = fade_in
            binding.textView.animation = fade_in
        }
        handler.postDelayed(runnable, 1000)

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        viewModel.loginResult.observe(this, loginResultObserver)
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

        btnLogin.setOnClickListener {
            performLogin()
        }
    }

    private fun performLogin() {
        val username = etUsername.text.toString()
        val password = etPassword.text.toString()

        if (username.isBlank() || password.isBlank()) {
            Toast.makeText(this, "Username dan password harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.login(username, password)
    }
    private fun navigateToMainActivity(userRole: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userRole", userRole)
        startActivity(intent)
        finish()
    }
}