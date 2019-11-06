package com.victorshinya.buweets

import java.util.*

data class NaturalLanguageUnderstandingModel(
    val text: String,
    val emotion: Emotion,
    val language: String,
    val sentiment: Sentiment,
    val date: Date
)

data class Emotion(val document: EmotionDocument)

data class EmotionDocument(val emotion: EmotionContent)

data class EmotionContent(
    val anger: Double,
    val disgust: Double,
    val fear: Double,
    val joy: Double,
    val sadness: Double
)

data class Sentiment(val document: SentimentDocument)

data class SentimentDocument(val label: String, val score: Double)