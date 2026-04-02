package com.example.comisapp.data.models

import com.google.firebase.Timestamp

data class CommentModel (
    val id : String = "",
    val usuario : String = "",
    val comentario : String = "",
    val email: String = "",
    val fecha : Timestamp? = null,
    val rate : Int = 0,
    val fotoUrl: String=""
)