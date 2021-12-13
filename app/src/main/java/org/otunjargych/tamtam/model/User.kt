package org.otunjargych.tamtam.model

data class User(
    val id: String = "",
    var name: String = "",
    var last_name: String = "",
    val phone_number: String = "",
    var email: String = "",
    var image: String = ""
)