package com.hashdroid.recipe_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hashdroid.recipe_app.network.Ingredient
import com.hashdroid.recipe_app.network.RecipieView

class IngredientsAdapter(private val recipes: List<Ingredient>) : RecyclerView.Adapter<IngredientViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_view3, parent, false)
        return IngredientViewHolder(view)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.ingredient_name.text = recipe.name

        // Load the image using Glide
        Glide.with(holder.itemView.context)
            .load("https://img.spoonacular.com/ingredients_100x100/" + recipe.image)
            .into(holder.ingredient_image)
    }
}

class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var ingredient_name: TextView = itemView.findViewById(R.id.ingredient_name)
    var ingredient_image: ImageView = itemView.findViewById(R.id.ingredient_image)
}