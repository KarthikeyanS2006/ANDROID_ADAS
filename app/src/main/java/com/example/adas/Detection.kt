package com.example.adas

data class Detection(
    val label: String,
    val x1: Int,
    val y1: Int,
    val x2: Int,
    val y2: Int,
    val confidence: Float
)
