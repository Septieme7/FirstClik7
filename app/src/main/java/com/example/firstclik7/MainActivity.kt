package com.example.firstclik7

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAjouter: FloatingActionButton
    private lateinit var fabUp: FloatingActionButton
    private lateinit var tvJC: TextView
    private lateinit var ivLogo: ImageView

    private lateinit var articleAdapter: ArticleAdapter
    private lateinit var articleDao: ArticleDao
    private lateinit var jcClickHandler: JCClickHandler

    private var articles = mutableListOf<Article>()

    companion object {
        const val PERMISSION_REQUEST_CAMERA = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = ArticleDatabase.getInstance(this)
        articleDao = db.articleDao()

        initViews()

        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), PERMISSION_REQUEST_CAMERA)
        }

        val layoutManager = FlexboxLayoutManager(this).apply {
            flexDirection = FlexDirection.ROW
            justifyContent = JustifyContent.FLEX_START
            flexWrap = FlexWrap.WRAP
        }
        recyclerView.layoutManager = layoutManager

        articleAdapter = ArticleAdapter(
            articles = articles,
            onPhotoClick = { article ->
                ZoomDialog(article.cheminPhoto).show(supportFragmentManager, "ZoomDialog")
            },
            onDeleteClick = { article ->
                lifecycleScope.launch {
                    articleDao.supprimer(article)
                    chargerArticles()
                }
            }
        )
        recyclerView.adapter = articleAdapter

        chargerArticles()

        fabAjouter.setOnClickListener {
            AjoutArticleDialog { nom, description, cheminPhoto ->
                lifecycleScope.launch {
                    val article = Article(nom = nom, description = description, cheminPhoto = cheminPhoto)
                    articleDao.inserer(article)
                    chargerArticles()
                }
            }.show(supportFragmentManager, "AjoutArticleDialog")
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && fabUp.isGone) {
                    fabUp.isVisible = true
                } else if (dy < 0 && fabUp.isVisible) {
                    fabUp.isGone = true
                }
            }
        })
        fabUp.setOnClickListener {
            recyclerView.smoothScrollToPosition(0)
        }

        jcClickHandler = JCClickHandler(this, findViewById(android.R.id.content))
        tvJC.setOnClickListener {
            jcClickHandler.onJCClick()
        }

        ivLogo.setOnClickListener {
            ZoomDialog("android.resource://${packageName}/drawable/iconduck")
                .show(supportFragmentManager, "LogoDialog")
        }
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        fabAjouter = findViewById(R.id.fab_ajouter)
        fabUp = findViewById(R.id.fab_up)
        tvJC = findViewById(R.id.tv_jc)
        ivLogo = findViewById(R.id.iv_logo)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun chargerArticles() {
        lifecycleScope.launch {
            val liste = articleDao.obtenirTous()
            articles.clear()
            articles.addAll(liste)
            articleAdapter.mettreAJourListe(articles)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_theme -> {
                val currentMode = resources.configuration.uiMode and
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK
                if (currentMode == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission caméra accordée", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission caméra refusée", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        jcClickHandler.release()
    }
}
