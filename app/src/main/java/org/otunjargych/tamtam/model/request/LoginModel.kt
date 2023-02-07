package org.otunjargych.tamtam.model.request



data class LoginRequestBody(
    val login : String,
    val password : String
)

data class LoginResponse(
    val id : String,
    val token : String
)