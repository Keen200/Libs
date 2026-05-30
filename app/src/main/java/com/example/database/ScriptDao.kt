package com.example.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptDao {
    @Query("SELECT * FROM autolisp_scripts ORDER BY timestamp DESC")
    fun getAllScripts(): Flow<List<ScriptEntity>>

    @Query("SELECT * FROM autolisp_scripts WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteScripts(): Flow<List<ScriptEntity>>

    @Query("SELECT * FROM autolisp_scripts WHERE title LIKE :query OR prompt LIKE :query OR code LIKE :query ORDER BY timestamp DESC")
    fun searchScripts(query: String): Flow<List<ScriptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScript(script: ScriptEntity): Long

    @Update
    suspend fun updateScript(script: ScriptEntity)

    @Delete
    suspend fun deleteScript(script: ScriptEntity)

    @Query("DELETE FROM autolisp_scripts")
    suspend fun deleteAll()
}
