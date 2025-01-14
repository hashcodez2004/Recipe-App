package com.hashdroid.recipe_app.com.hashdroid.recipe_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hashdroid.recipe_app.R
import com.hashdroid.recipe_app.network.SimilarRecipes

class SimilarRecipesAdapter(private val recipes: List<SimilarRecipes>
) : RecyclerView.Adapter<SimilarRecipesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilarRecipesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_view2, parent, false)
        return SimilarRecipesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    override fun onBindViewHolder(holder: SimilarRecipesViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.dish_name2.text = recipe.title
        holder.cook_time2.text = "Ready in ${recipe.readyInMinutes} min" // Sets the random cook time
    }
}

class SimilarRecipesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var dish_name2: TextView = itemView.findViewById(R.id.dish_name2)
    var cook_time2: TextView = itemView.findViewById(R.id.cook_time2)
}

