package com.hashdroid.recipe_app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hashdroid.recipe_app.network.FavoritesAdapter
import com.hashdroid.recipe_app.network.FavouritesDB

class FavoritesFragment : Fragment() {
    private lateinit var db: FavouritesDB
    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FavouritesDB.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favourites, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view2)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        favoritesAdapter = FavoritesAdapter(emptyList())
        recyclerView.adapter = favoritesAdapter


        db.favouritesDAO().getAllFavorites().observe(viewLifecycleOwner) { favorites ->
            if (!favorites.isNullOrEmpty()) {
                Log.d("FavoritesFragment", "Items in DB: ${favorites.size}")
                favoritesAdapter.updateData(favorites)
            } else {
                Log.d("FavoritesFragment", "Database is empty!")
                Log.d("FavoritesFragment", "$favorites")
            }
        }


        return view
    }
}

