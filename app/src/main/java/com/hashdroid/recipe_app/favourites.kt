package com.hashdroid.recipe_app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.hashdroid.recipe_app.network.FavoritesAdapter
import com.hashdroid.recipe_app.network.FavouritesDB
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {
    private lateinit var db: FavouritesDB
    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            db = Room.databaseBuilder(
                requireContext(),
                FavouritesDB::class.java,
                "favourites_db"
            ).build()
        }
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
            favorites?.let {
                Log.d("FavoritesFragment", "Number of items in DB: ${it.size}")
                favoritesAdapter.updateData(it)
            }
        }

        return view
    }
}

