package com.example.firstclik7

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView

class ZoomDialog(private val cheminPhoto: String) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_zoom, container, false)
        val photoView: PhotoView = view.findViewById(R.id.photo_view)
        val btnFermer: ImageButton = view.findViewById(R.id.btn_fermer)

        when {
            cheminPhoto.startsWith("content://") ->
                Glide.with(this).load(Uri.parse(cheminPhoto)).into(photoView)
            cheminPhoto.startsWith("android.resource://") ->
                Glide.with(this).load(cheminPhoto).into(photoView)
            else ->
                Glide.with(this).load(cheminPhoto).into(photoView)
        }

        btnFermer.setOnClickListener { dismiss() }

        return view
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }
}