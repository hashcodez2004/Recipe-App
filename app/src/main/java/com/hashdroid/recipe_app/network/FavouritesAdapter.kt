package com.hashdroid.recipe_app.network

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hashdroid.recipe_app.R

class FavoritesAdapter(private var data: List<FavouritesEntity>,
    private val onRecipeClick: (Int) -> Unit) :
    RecyclerView.Adapter<FavoritesAdapter.FavoritesViewHolder>() {

    inner class FavoritesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.dish_name2)
        val image = view.findViewById<ImageView>(R.id.dish_image2)
        val time = view.findViewById<TextView>(R.id.cook_time2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoritesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view2, parent, false)
        return FavoritesViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoritesViewHolder, position: Int) {
        val item = data[position]
        Log.d("FavoritesAdapter", "Binding item at position $position: ${item.img_title}")
        holder.time.text = item.cooking_time.toString()
        holder.title.text = item.img_title
        Glide.with(holder.itemView.context).load(item.img_url).into(holder.image)

        holder.itemView.setOnClickListener{
            onRecipeClick(item.id)
        }
    }

    override fun getItemCount() = data.size

    fun updateData(newData: List<FavouritesEntity>) {
        data = newData
        notifyDataSetChanged()
    }

}
