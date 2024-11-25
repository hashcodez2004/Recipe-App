package com.hashdroid.recipe_app.network

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hashdroid.recipe_app.R

class SearchViewAdapter(private var recipes: List<Recipe>,   //made the recipes var intead of val because it is now mutable
//    private val onItemClick: (Int) -> Unit
    ) : RecyclerView.Adapter<SearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_view5, parent, false)
        return SearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.dishTitle.text = recipe.title

//        holder.root.setOnClickListener{
//            onItemClick(recipe.id)
//        }
    }

    // Added this method to dynamically update the adapter's data
    fun updateRecipes(newRecipes: List<Recipe>) {
        recipes = newRecipes // Update the list of recipes
        notifyDataSetChanged() // Notify the adapter to refresh the RecyclerView
    }
}

class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var dishTitle = itemView.findViewById<TextView>(R.id.dishTitle_searchView)
    var root = itemView
}