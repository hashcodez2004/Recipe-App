package com.hashdroid.recipe_app.network

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hashdroid.recipe_app.R

class SearchViewAdapter(
    private var recipes: List<Recipe>,   //made the recipes var instead of val because it is now mutable
    private val onItemClick: (Int) -> Unit
    ) : RecyclerView.Adapter<SearchViewHolder>() {

    private var query: String = "" // Added to store the search keyword

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
        holder.dishTitle.text = getHighlightedText(recipe.title, query)

        holder.root.setOnClickListener{
            onItemClick(recipe.id)
        }
    }

    // Added this method to dynamically update the adapter's data
    fun updateRecipes(newRecipes: List<Recipe>, searchQuery: String) {
        recipes = newRecipes // Update the list of recipes
        query = searchQuery // Update the query used for highlighting
        notifyDataSetChanged() // Notify the adapter to refresh the RecyclerView
    }

    // Helper function to highlight the query in the text
    private fun getHighlightedText(text: String, query: String): SpannableString {
        val spannable = SpannableString(text)
        val start = text.lowercase().indexOf(query.lowercase())
        if (start >= 0) {
            val end = start + query.length
            spannable.setSpan(
                StyleSpan(Typeface.BOLD),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        return spannable
    }
}

class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var dishTitle = itemView.findViewById<TextView>(R.id.dishTitle_searchView)
    var root = itemView
}