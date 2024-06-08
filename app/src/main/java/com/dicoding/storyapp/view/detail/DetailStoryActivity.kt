package com.dicoding.storyapp.view.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.storyapp.R
import com.dicoding.storyapp.data.response.DetailStoryResponse
import com.dicoding.storyapp.databinding.ActivityDetailStoryBinding
import com.dicoding.storyapp.utils.isNetworkAvailable
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailStoryBinding
    private val viewModel: DetailStoryViewModel by viewModels()
    private var networkCheckJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra(EXTRA_STORY_ID) ?: return

        startNetworkCheck(storyId)
        setupObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopNetworkCheck()
    }

    private fun setupObserver() {
        viewModel.story.observe(this) { detailStory ->
            bindStoryDetails(detailStory)
        }

        viewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        viewModel.error.observe(this) { error ->
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun bindStoryDetails(detailStory: DetailStoryResponse) {
        binding.apply {
            tvDetailName.text = detailStory.story?.name
            tvDetailDescription.text = detailStory.story?.description
            Glide.with(this@DetailStoryActivity)
                .load(detailStory.story?.photoUrl)
                .into(ivDetailPhoto)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun startNetworkCheck(storyId: String) {
        networkCheckJob = lifecycleScope.launch {
            while (true) {
                if (isNetworkAvailable(this@DetailStoryActivity)) {
                    viewModel.viewStoryDetail(storyId)
                    break
                } else {
                    Toast.makeText(
                        this@DetailStoryActivity,
                        getString(R.string.no_internet),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                delay(5000)
            }
        }
    }

    private fun stopNetworkCheck() {
        networkCheckJob?.cancel()
    }

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
    }
}