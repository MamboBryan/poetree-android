package com.mambo.core.repository

import com.mambo.data.Poem
import com.mambo.local.PoemsDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PoemRepository @Inject constructor() {

    @Inject
    lateinit var poemsDao: PoemsDao

    fun poems(): Flow<List<Poem>> {
        return poemsDao.getAll()
    }

}