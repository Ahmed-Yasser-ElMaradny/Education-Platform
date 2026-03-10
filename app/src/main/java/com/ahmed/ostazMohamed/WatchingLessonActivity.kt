package com.ahmed.ostazMohamed

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.ahmed.ostazMohamed.databinding.ActivityWatchingLessonBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.customui.DefaultPlayerUiController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.loadOrCueVideo

class WatchingLessonActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWatchingLessonBinding
    private lateinit var youTubePlayer: YouTubePlayer
    private var isFullScreen = false
    private val onBackPressedButton = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (isFullScreen) {
                exitFullScreen()
                isFullScreen = false
            } else {
                startActivity(Intent(this@WatchingLessonActivity,StudentActivity::class.java))
                finish()
            }
        }



    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupWindowUi()
        binding = ActivityWatchingLessonBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(onBackPressedButton)

        lifecycle.addObserver(binding.youtubePlayerView)

        binding.lessonTitle.setText(intent.extras?.getString("title"))

        val playerOptions = IFramePlayerOptions.Builder(applicationContext)
            .controls(0)      // يخفي UI الافتراضي
            .fullscreen(0)    // يمنع fullscreen الافتراضي
            .rel(0)
            .ivLoadPolicy(3)
            .build()


        binding.youtubePlayerView.addFullscreenListener(object : FullscreenListener {
            override fun onEnterFullscreen(
                fullscreenView: View,
                exitFullscreen: () -> Unit,
            ) {
                isFullScreen = true
                binding.fullscreenContainer.visibility = View.VISIBLE
                binding.fullscreenContainer.addView(fullscreenView)

                // Full Screen remove status and navigation Bar
                WindowInsetsControllerCompat(window!!, binding.main).apply {
                    hide(WindowInsetsCompat.Type.systemBars())
                    systemBarsBehavior =
                        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                }

                if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }

            }

            override fun onExitFullscreen() {
                isFullScreen = false
                binding.fullscreenContainer.visibility = View.GONE
                binding.fullscreenContainer.removeAllViews()

                WindowInsetsControllerCompat(window, binding.main).apply {
                    show(WindowInsetsCompat.Type.systemBars())
                    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                }

                if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_SENSOR) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }
        })

        val youTubePlayerListener = object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                super.onReady(youTubePlayer)
                this@WatchingLessonActivity.youTubePlayer = youTubePlayer
                val uiController =
                    DefaultPlayerUiController(binding.youtubePlayerView, youTubePlayer)
                uiController.showYouTubeButton(false)
                uiController.showMenuButton(false)
                uiController.showFullscreenButton(false)
                binding.youtubePlayerView.setCustomPlayerUi(uiController.rootView)
               // val videoId = "dQw4w9WgXcQ"
                val bundle = intent.extras
                val videoId = bundle?.getString("videoId").toString()
                youTubePlayer.loadOrCueVideo(lifecycle, videoId, 0f)

            }
        }

        binding.btnFullscreen.setOnClickListener {
            if (isFullScreen) {
                exitFullScreen()
                isFullScreen = false
            }else{
                enterFullScreen()
                isFullScreen = true
            }
        }

        binding.youtubePlayerView.enableAutomaticInitialization = false
        binding.youtubePlayerView.initialize(youTubePlayerListener, playerOptions)

    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            enterFullScreen()
        } else {
            exitFullScreen()
        }
    }

    private fun enterFullScreen() {
        // 1. تخلي الفيديو يملأ الشاشة
        binding.youtubePlayerView.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        binding.youtubePlayerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT

        // 2. تخفي أي حاجة تانية في الشاشة (أزرار، نصوص، ناف بار)
        supportActionBar?.hide()

        // 3. تقلب الشاشة بالعرض
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    private fun exitFullScreen() {
        // 1. ترجع حجم الفيديو لطبيعته (مثلاً 250dp أو WRAP_CONTENT)
        binding.youtubePlayerView.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        binding.youtubePlayerView.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT

        supportActionBar?.show()

        // 3. ترجع الشاشة بالطول
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }

    private fun setupWindowUi() {
        // لو أنت فعلاً عايز Fullscreen/Immersive
        supportActionBar?.hide()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        // الطريقة الأحدث لإخفاء الـ system bars بدل systemUiVisibility
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.systemBars())

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }

}
