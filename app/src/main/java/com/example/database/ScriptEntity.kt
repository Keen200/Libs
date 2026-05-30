package com.example.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "autolisp_scripts")
data class ScriptEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val prompt: String,
    val code: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val category: String = "Utility"
)
