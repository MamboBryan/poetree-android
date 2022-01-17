package com.mambo.core.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.mambo.data.models.Poem
import com.mambo.local.PoemsDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PoemRepository @Inject constructor() {

    @Inject
    lateinit var poemsDao: PoemsDao

    fun poems(): Flow<List<Poem>> = poemsDao.getAll()

    fun getLocalPoems(query: String) = Pager(PagingConfig(10)){
        poemsDao.getLocalPoems()
    }.flow

    fun getPoems() = poemsDao.getLocalPoems()

    suspend fun save(poem: Poem) = poemsDao.insert(poem)

    suspend fun update(poem: Poem) = poemsDao.update(poem)

    suspend fun delete(poem: Poem) = poemsDao.delete(poem)
}
