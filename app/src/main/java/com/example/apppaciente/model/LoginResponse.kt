// LoginResponse.kt
package com.example.apppaciente.model

data class LoginResponse(
    val status: Boolean,
    val data: UserData?,
    val message: String?
)

data class UserData(
    val id: Int,
    val token: String,
    val nombre_usuario: String,
    val nombre: String,
    val ape_completo: String,
    val fecha_nac: String,
    val documento: String,
    val email: String,
    val foto: String,
    val direccion: String,
    val telefono: String,
    val notificacion: Int
)
