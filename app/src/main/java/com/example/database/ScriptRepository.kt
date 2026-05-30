package com.example.database

import kotlinx.coroutines.flow.Flow

class ScriptRepository(private val scriptDao: ScriptDao) {
    val allScripts: Flow<List<ScriptEntity>> = scriptDao.getAllScripts()
    val favoriteScripts: Flow<List<ScriptEntity>> = scriptDao.getFavoriteScripts()

    fun searchScripts(query: String): Flow<List<ScriptEntity>> {
        return scriptDao.searchScripts("%$query%")
    }

    suspend fun insertScript(script: ScriptEntity): Long {
        return scriptDao.insertScript(script)
    }

    suspend fun updateScript(script: ScriptEntity) {
        scriptDao.updateScript(script)
    }

    suspend fun deleteScript(script: ScriptEntity) {
        scriptDao.deleteScript(script)
    }

    suspend fun deleteAll() {
        scriptDao.deleteAll()
    }
}
