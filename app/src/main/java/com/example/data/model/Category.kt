package com.example.data.model

enum class CelebrityCategory(
    val title: String,
    val priorMean: Double,
    val smoothingM: Double,
    val maxExpectedViews: Long
) {
    OYUNCU(
        title = "Oyuncular",
        priorMean = 3.55,
        smoothingM = 25.0,
        maxExpectedViews = 180000L
    ),
    MUZISYEN(
        title = "Şarkıcılar",
        priorMean = 3.65,
        smoothingM = 25.0,
        maxExpectedViews = 210000L
    ),
    SPORCU(
        title = "Sporcular",
        priorMean = 3.80,
        smoothingM = 25.0,
        maxExpectedViews = 240000L
    ),
    FENOMEN(
        title = "Fenomenler",
        priorMean = 3.25,
        smoothingM = 25.0,
        maxExpectedViews = 160000L
    )
}
