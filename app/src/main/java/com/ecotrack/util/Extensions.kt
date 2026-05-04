package com.ecotrack.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Convenience extension functions used across the app. */

fun Double.formatCo2(): String = String.format(Locale.getDefault(), "%.2f kg CO₂", this)

fun Long.toDateString(): String =
    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(this))

fun Long.toDateTimeString(): String =
    SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault()).format(Date(this))
