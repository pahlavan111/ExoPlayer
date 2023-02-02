package com.bp.exoplayer

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.media3.exoplayer.ExoPlayer
import com.bp.exoplayer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initialExoPlayer()
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun initialExoPlayer() {
        player = ExoPlayer.Builder(this)
            .build()
            .also {
                binding.playerView.player = it
            }
    }
}