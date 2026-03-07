package com.example.firstclik7

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.content.pm.PackageManager

class AjoutArticleDialog(
    private val onArticleAjoute: (nom: String, description: String, cheminPhoto: String) -> Unit
) : DialogFragment() {

    private lateinit var btnPrendrePhoto: Button
    private lateinit var ivApercu: ImageView
    private lateinit var etNom: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnSauvegarder: Button

    private var photoUri: Uri? = null
    private var currentPhotoPath: String? = null
    private var photoBitmap: Bitmap? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_ajout_article, null)

        initViews(view)

        btnPrendrePhoto.setOnClickListener {
            prendrePhoto()
        }

        btnSauvegarder.setOnClickListener {
            val nom = etNom.text.toString().trim()
            val description = etDescription.text.toString().trim()
            if (nom.isNotEmpty() && photoBitmap != null) {
                val chemin = PhotoUtils.sauvegarderPhoto(requireContext(), photoBitmap!!)
                if (chemin != null) {
                    onArticleAjoute(nom, description, chemin)
                    dismiss()
                } else {
                    Toast.makeText(context, "Erreur lors de l'enregistrement de la photo", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Veuillez remplir le nom et prendre une photo", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setView(view)
        return builder.create()
    }

    private fun initViews(view: View) {
        btnPrendrePhoto = view.findViewById(R.id.btn_prendre_photo)
        ivApercu = view.findViewById(R.id.iv_apercu)
        etNom = view.findViewById(R.id.et_nom)
        etDescription = view.findViewById(R.id.et_description)
        btnSauvegarder = view.findViewById(R.id.btn_sauvegarder)
        view.findViewById<Button>(R.id.btn_annuler).setOnClickListener { dismiss() }
    }

    private fun prendrePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            val photoFile = creerFichierImage()
            photoUri = FileProvider.getUriForFile(requireContext(),
                "${requireContext().packageName}.fileprovider", photoFile)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Erreur lors de la création du fichier", Toast.LENGTH_SHORT).show()
        }
    }

    private fun creerFichierImage(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File.createTempFile(imageFileName, ".jpg", storageDir)
        currentPhotoPath = imageFile.absolutePath
        return imageFile
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            photoBitmap = BitmapFactory.decodeFile(currentPhotoPath)
            if (photoBitmap != null) {
                ivApercu.setImageBitmap(photoBitmap)
                ivApercu.visibility = View.VISIBLE
                btnSauvegarder.isEnabled = true
            } else {
                Toast.makeText(context, "Erreur lors du chargement de l'image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }
}
