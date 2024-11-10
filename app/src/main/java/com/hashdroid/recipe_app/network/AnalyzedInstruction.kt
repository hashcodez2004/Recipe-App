package com.hashdroid.recipe_app.network

data class AnalyzedInstruction(
    val name: String,
    val steps: List<Step>
)