package com.example.firstclik7

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File
import android.content.pm.PackageManager
import android.widget.Toast

class ArticleAdapter(
    private var articles: List<Article>,
    private val onPhotoClick: (Article) -> Unit,
    private val onDeleteClick: (Article) -> Unit
) : RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNom: TextView = itemView.findViewById(R.id.tv_nom)
        private val tvDescription: TextView = itemView.findViewById(R.id.tv_description)
        private val ivPhoto: ImageView = itemView.findViewById(R.id.iv_photo)
        private val btnSupprimer: ImageButton = itemView.findViewById(R.id.btn_supprimer)

        fun bind(article: Article) {
            tvNom.text = article.nom
            tvDescription.text = article.description

            if (article.cheminPhoto.startsWith("content://")) {
                Glide.with(itemView.context).load(Uri.parse(article.cheminPhoto)).into(ivPhoto)
            } else {
                Glide.with(itemView.context).load(File(article.cheminPhoto)).into(ivPhoto)
            }

            ivPhoto.setOnClickListener { onPhotoClick(article) }
            btnSupprimer.setOnClickListener { onDeleteClick(article) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(articles[position])
    }

    override fun getItemCount() = articles.size

    fun mettreAJourListe(nouvelleListe: List<Article>) {
        articles = nouvelleListe
        notifyDataSetChanged()
    }
}
