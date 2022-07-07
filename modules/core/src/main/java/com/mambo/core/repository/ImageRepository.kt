package com.mambo.core.repository

import android.net.Uri
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Poetree
 * @author Mambo Bryan
 * @email mambobryan@gmail.com
 * Created 4/17/22 at 10:43 PM
 */

class ImageRepository @Inject constructor() {

    private val storageRef = Firebase.storage.reference.child("images")

    suspend fun upload(id: String, uri: Uri) =
        storageRef
            .child("$id.jpg")
            .putFile(uri)
            .await()

    suspend fun delete(id: String) =
        storageRef
            .child("$id.jpg")
            .delete()
            .await()

}