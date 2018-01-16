package ru.satek.todo.aconite

import io.aconite.annotations.Header
import io.aconite.annotations.POST

interface AuthApi {
    @POST("/auth/sign-in")
    suspend fun signIn(@Header("Authorization") auth: BasicAuth): SignInResponse
}