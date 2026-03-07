package com.example.firstclik7

import android.content.Context
import android.media.MediaPlayer
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import com.google.android.material.snackbar.Snackbar


class JCClickHandler(private val context: Context, private val rootView: View) {

    private var clickCount = 0
    private var mediaPlayer: MediaPlayer? = null

    fun onJCClick() {
        clickCount++
        if (clickCount >= 7) {
            clickCount = 0
            jouerSon()
            afficherImageRebond()
        }
    }

    private fun jouerSon() {
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.soundofjc)
            mediaPlayer?.setOnCompletionListener {
                it.release()
            }
            mediaPlayer?.start()
        } catch (e: Exception) {
            e.printStackTrace()
            Snackbar.make(rootView, "Erreur de lecture du son", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun afficherImageRebond() {
        val imageView = ImageView(context).apply {
            setImageResource(R.drawable.picofjc)
            layoutParams = ViewGroup.LayoutParams(200, 200)
            visibility = View.GONE
        }
        (rootView as? ViewGroup)?.addView(imageView)

        imageView.post {
            val parentWidth = (imageView.parent as View).width
            val parentHeight = (imageView.parent as View).height
            imageView.x = (parentWidth - imageView.width) / 2f
            imageView.y = (parentHeight - imageView.height) / 2f
            imageView.visibility = View.VISIBLE

            imageView.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(300)
                .setInterpolator(BounceInterpolator())
                .withEndAction {
                    imageView.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(200)
                        .withEndAction {
                            imageView.postDelayed({
                                (imageView.parent as? ViewGroup)?.removeView(imageView)
                            }, 2000)
                        }
                        .start()
                }
                .start()
        }
    }

    fun release() {
        mediaPlayer?.release()
    }
}