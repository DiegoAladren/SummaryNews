package com.example.summarynews.newsAPI

data class NewsResponse(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)