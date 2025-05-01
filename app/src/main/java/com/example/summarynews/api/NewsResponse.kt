package com.example.summarynews.api

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)