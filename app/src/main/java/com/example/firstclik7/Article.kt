package com.example.firstclik7

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class Article(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nom: String,
    val description: String,
    val cheminPhoto: String
)