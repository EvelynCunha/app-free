package com.example.freela.repository.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthRepository(private val auth: FirebaseAuth) {

    fun getCurrentUserName(): String? {
        return auth.currentUser?.displayName
    }

    suspend fun login(email: String, password: String): Result<Unit> =
        try {
            suspendCancellableCoroutine { cont ->
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            cont.resume(Result.success(Unit))
                        } else {
                            val e = task.exception
                            if (e is FirebaseAuthException) {
                                cont.resume(Result.failure(e))
                            } else {
                                cont.resume(Result.failure(Exception("Erro desconhecido")))
                            }
                        }
                    }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun createUser(email: String, password: String, name: String): Result<Unit> =
        try {
            suspendCancellableCoroutine { cont ->
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            if (user != null) {
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build()

                                user.updateProfile(profileUpdates)
                                    .addOnCompleteListener { updateTask ->
                                        if (updateTask.isSuccessful) {
                                            cont.resume(Result.success(Unit))
                                        } else {
                                            cont.resume(Result.failure(updateTask.exception ?: Exception("Erro ao atualizar perfil")))
                                        }
                                    }
                            } else {
                                cont.resume(Result.failure(Exception("Usuário não encontrado após criação.")))
                            }
                        } else {
                            cont.resume(Result.failure(task.exception ?: Exception("Erro desconhecido ao criar usuário.")))
                        }
                    }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
}
