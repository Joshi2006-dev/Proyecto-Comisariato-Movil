package com.example.comisapp.ui.helpers

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

fun Timestamp.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
    return sdf.format(this.toDate())
}