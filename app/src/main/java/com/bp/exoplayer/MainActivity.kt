package com.bp.exoplayer

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.bp.exoplayer.databinding.ActivityMainBinding

private const val TAG = "PlayerActivity"

class MainActivity : AppCompatActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private var exoPlayer: ExoPlayer? = null

    private var playWhenReady = true
    private var currentItem = 0
    private var playBackPosition = 0L

    private val playBackStateListener : Player.Listener = playBackStateListener()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    private fun initialExoPlayer() {

        val trackSelector = DefaultTrackSelector(this).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }

        exoPlayer = ExoPlayer.Builder(this)
            .setTrackSelector(trackSelector)
            .build()
            .also { exoPlayer ->
                binding.playerView.player = exoPlayer

//                val mediaItem = MediaItem.fromUri(getString(R.string.media_url_mp3))
//                exoPlayer.addMediaItem(mediaItem)
//                val secondMediaItem = MediaItem.fromUri(getString(R.string.media_url_mp3_1))
//                exoPlayer.addMediaItem(secondMediaItem)
//                val thirdMediaItem = MediaItem.fromUri(getString(R.string.media_url_mp3_2))
//                exoPlayer.addMediaItem(thirdMediaItem)

                val mediaItem = MediaItem.Builder()
                    .setUri(getString(R.string.media_url_dash))
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .build()

                exoPlayer.setMediaItem(mediaItem)

                exoPlayer.playWhenReady = playWhenReady
                exoPlayer.seekTo(currentItem, playBackPosition)
                exoPlayer.addListener(playBackStateListener)
                exoPlayer.prepare()
            }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initialExoPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT <= 23 || exoPlayer == null) {
            initialExoPlayer()
        }
    }

    private fun hideSystemUi() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.playerView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        exoPlayer?.let {
            playBackPosition = it.currentPosition
            currentItem = it.currentMediaItemIndex
            playWhenReady = it.playWhenReady
            it.removeListener(playBackStateListener)
            it.release()
        }
        exoPlayer = null
    }

    private fun playBackStateListener() = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)

            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED"
                else -> "UNKNOWN_STATE"
            }
            Log.d(TAG, stateString)
        }
    }
}
