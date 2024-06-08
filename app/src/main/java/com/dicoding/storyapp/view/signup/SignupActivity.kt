package com.dicoding.storyapp.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.storyapp.R
import com.dicoding.storyapp.databinding.ActivitySignupBinding
import com.dicoding.storyapp.utils.DialogType
import com.dicoding.storyapp.utils.showAlertDialog
import com.dicoding.storyapp.view.custom.EmailEditText
import com.dicoding.storyapp.view.custom.LoginRegisterButton
import com.dicoding.storyapp.view.custom.NameEditText
import com.dicoding.storyapp.view.custom.PasswordEditText
import com.dicoding.storyapp.view.login.LoginActivity

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel: SignupViewModel by viewModels()
    private lateinit var nameEditText: NameEditText
    private lateinit var emailEditText: EmailEditText
    private lateinit var passwordEditText: PasswordEditText
    private lateinit var loginRegisterButton: LoginRegisterButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
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

        val signup = ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA, 1f).setDuration(250)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(250)
        val name = ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA, 1f).setDuration(250)
        val layoutName =
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val edName = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(250)
        val email = ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA, 1f).setDuration(250)
        val layoutEmail =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val edEmail =
            ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(250)
        val password =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(250)
        val layoutPassword =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(250)
        val edPassword =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(250)
        val account = ObjectAnimator.ofFloat(binding.tvHaveAccount, View.ALPHA, 1f).setDuration(250)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(250)

        val together = AnimatorSet().apply {
            playTogether(account, login)
        }
        AnimatorSet().apply {
            playSequentially(
                title,
                name,
                edName,
                layoutName,
                email,
                edEmail,
                layoutEmail,
                password,
                edPassword,
                layoutPassword,
                signup,
                together
            )
            start()
        }
    }

    private fun setupView() {
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            nameEditText = binding.edRegisterName
            emailEditText = binding.edRegisterEmail
            passwordEditText = binding.edRegisterPassword
            loginRegisterButton = binding.signupButton

            val isNameValid = name.isNotBlank()
            val isEmailValid = email.isNotBlank()
            val isPasswordValid = password.length >= 8

            if (isNameValid && isEmailValid && isPasswordValid) {
                signupViewModel.signup(name, email, password)
            } else {
                if (!isNameValid) {
                    nameEditText.error = getString(R.string.error_empty_name)
                }
                if (!isEmailValid) {
                    emailEditText.error = getString(R.string.error_empty_email)
                }
                if (!isPasswordValid) {
                    passwordEditText.error = getString(R.string.password_too_short)
                }
            }
        }

        binding.btnLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun setupObservers() {
        signupViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        signupViewModel.snackbarText.observe(this) { message ->
            message?.let {
                if (it != "User created") {
                    showAlertDialog(
                        title = R.string.error,
                        message = it,
                        icon = R.drawable.ic_error,
                        type = DialogType.ERROR,
                        positiveButtonText = R.string.try_again
                    )
                } else {
                    showAlertDialog(
                        title = R.string.hore,
                        message = getString(R.string.signup_success),
                        icon = R.drawable.ic_success,
                        type = DialogType.SUCCESS,
                        positiveButtonText = R.string.continued,
                        positiveButtonAction = {
                            finish()
                        }
                    )
                }
            }
        }

        signupViewModel.signup.observe(this) { isSuccess ->
            if (!isSuccess) {
                showAlertDialog(
                    title = R.string.error,
                    message = getString(R.string.signup_failed),
                    icon = R.drawable.ic_error,
                    type = DialogType.ERROR,
                    positiveButtonText = R.string.try_again
                )
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}