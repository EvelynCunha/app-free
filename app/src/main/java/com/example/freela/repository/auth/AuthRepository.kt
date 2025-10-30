package com.example.freela.repository.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AuthRepository(private val auth: FirebaseAuth) {

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

    suspend fun createUser(email: String, password: String): Result<Unit> =
        try {
            suspendCancellableCoroutine { cont ->
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            cont.resume(Result.success(Unit))
                        } else {
                            val e = task.exception
                            if (e != null) {
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
}
