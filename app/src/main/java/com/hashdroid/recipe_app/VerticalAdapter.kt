package com.hashdroid.recipe_app.com.hashdroid.recipe_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hashdroid.recipe_app.R
import com.hashdroid.recipe_app.network.Recipe
import kotlin.random.Random

class VerticalAdapter(private val recipes: List<Recipe>,
    private val onItemClick: (Int) -> Unit //This is my lambda function for click handling
    ) : RecyclerView.Adapter<VerticalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VerticalViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_view2, parent, false)
        return VerticalViewHolder(view)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    override fun onBindViewHolder(holder: VerticalViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.dish_name2.text = recipe.title
        val randomCookTime = Random.nextInt(10, 51) // Generates a random number between 10 and 50
        holder.cook_time2.text = "Ready in $randomCookTime min" // Sets the random cook time

        // Load the image using Glide
        Glide.with(holder.itemView.context)
            .load(recipe.image)
            .into(holder.dish_image2)

        // Handling the click event
        holder.root.setOnClickListener {
            onItemClick(recipe.id) //pass the ID to the listener
        }
    }
}

class VerticalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var dish_name2: TextView = itemView.findViewById(R.id.dish_name2)
    var cook_time2: TextView = itemView.findViewById(R.id.cook_time2)
    var dish_image2: ImageView = itemView.findViewById(R.id.dish_image2)
    var root = itemView
}

