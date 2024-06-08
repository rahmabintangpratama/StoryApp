package com.dicoding.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivityLoginBinding
import com.dicoding.storyapp.utils.DialogType
import com.dicoding.storyapp.utils.showAlertDialog
import com.dicoding.storyapp.view.ViewModelFactory
import com.dicoding.storyapp.view.custom.EmailEditText
import com.dicoding.storyapp.view.custom.LoginRegisterButton
import com.dicoding.storyapp.view.custom.PasswordEditText
import com.dicoding.storyapp.view.main.MainActivity
import com.dicoding.storyapp.view.signup.SignupActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private lateinit var emailEditText: EmailEditText
    private lateinit var passwordEditText: PasswordEditText
    private lateinit var loginRegisterButton: LoginRegisterButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
        setupObservers()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(250)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(250)
        val message =
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA, 1f).setDuration(250)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(250)
        val edEmail =
            ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(250)
        val layoutEmail =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val password =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(250)
        val layoutPassword =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val edPassword =
            ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(250)
        val account = ObjectAnimator.ofFloat(binding.tvNoAccount, View.ALPHA, 1f).setDuration(250)
        val signup = ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(250)

        val together = AnimatorSet().apply {
            playTogether(account, signup)
        }
        AnimatorSet().apply {
            playSequentially(
                title,
                message,
                email,
                edEmail,
                layoutEmail,
                password,
                edPassword,
                layoutPassword,
                login,
                together
            )
            start()
        }
    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupObservers() {
        binding.loginButton.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            emailEditText = binding.edLoginEmail
            passwordEditText = binding.edLoginPassword
            loginRegisterButton = binding.loginButton

            val isEmailValid = email.isNotBlank()
            val isPasswordValid = password.length >= 8

            if (isEmailValid && isPasswordValid) {
                viewModel.login(email, password)
            } else {
                if (!isEmailValid) {
                    emailEditText.error = getString(R.string.error_empty_email)
                }
                if (!isPasswordValid) {
                    passwordEditText.error = getString(R.string.password_too_short)
                }
            }
        }

        viewModel.login.observe(this) { isLoggedIn ->
            if (isLoggedIn) {
                showAlertDialog(
                    title = R.string.yeah,
                    message = getString(R.string.login_success),
                    icon = R.drawable.ic_success,
                    type = DialogType.SUCCESS,
                    positiveButtonText = R.string.continued,
                    positiveButtonAction = {
                        val intent = Intent(this, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.snackbarText.observe(this) { message ->
            message?.let {
                if (it != "success") {
                    showAlertDialog(
                        title = R.string.error,
                        message = it,
                        icon = R.drawable.ic_error,
                        type = DialogType.ERROR,
                        positiveButtonText = R.string.try_again
                    )
                }
            }
        }
    }
}