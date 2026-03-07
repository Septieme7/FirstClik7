package com.example.firstclik7

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete

@Dao
interface ArticleDao {
    @Insert
    suspend fun inserer(article: Article)

    @Query("SELECT * FROM articles ORDER BY id DESC")
    suspend fun obtenirTous(): List<Article>

    @Delete
    suspend fun supprimer(article: Article)
}