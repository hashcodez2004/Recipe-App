package com.hashdroid.recipe_app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hashdroid.recipe_app.network.Equipment

class EquipmentsAdapter(private val recipes: List<Equipment>) : RecyclerView.Adapter<EquipmentViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_view4, parent, false)
        return EquipmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: EquipmentViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.equipment_name.text = recipe.name

        Glide.with(holder.itemView.context)
            .load(recipe.image)
            .into(holder.equipment_image)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

}

class EquipmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    var equipment_name : TextView = itemView.findViewById(R.id.equipment_name)
    var equipment_image : ImageView = itemView.findViewById(R.id.equipment_image)
}