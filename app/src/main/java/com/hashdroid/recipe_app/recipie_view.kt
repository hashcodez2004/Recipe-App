package com.hashdroid.recipe_app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hashdroid.recipe_app.network.Equipment
import com.hashdroid.recipe_app.network.FavouritesDB
import com.hashdroid.recipe_app.network.FavouritesEntity
import com.hashdroid.recipe_app.network.Ingredient
import com.hashdroid.recipe_app.network.RecipieView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class recipie_view : Fragment() {
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var equipmentsAdapter: EquipmentsAdapter
    private var recipeId: Int? = null
    private lateinit var database: FavouritesDB
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipeId = it.getInt(ARG_RECIPE_ID)
        }

        database = FavouritesDB.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        //inflating layout for the fragment
        val view = inflater.inflate(R.layout.fragment_recipie_view, container, false)

        //initializing RecyclerView
        val ingredients_rv = view.findViewById<RecyclerView>(R.id.ingredients_rv)
        ingredients_rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val equipment_rv = view.findViewById<RecyclerView>(R.id.equipments_rv)
        equipment_rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val toggle_img1 = view.findViewById<ImageView>(R.id.nutrition_image)
        val toggle_img2 = view.findViewById<ImageView>(R.id.Bad_for_health_image)
        val toggle_img3 = view.findViewById<ImageView>(R.id.Good_for_health_image)
        val nutrition_textview1 = view.findViewById<TextView>(R.id.nutrition_textview)
        val Bad_for_health_textview2 = view.findViewById<TextView>(R.id.Bad_for_health_textview)
        val Good_for_health_textview3 = view.findViewById<TextView>(R.id.Good_for_health_textview)

        //making text view initially gone
        nutrition_textview1.isVisible = false
        Bad_for_health_textview2.isVisible = false
        Good_for_health_textview3.isVisible = false

        toggle_img1.setOnClickListener {
            // Toggle the visibility of the TextView
            nutrition_textview1.visibility = if (nutrition_textview1.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }

            if (nutrition_textview1.visibility == View.VISIBLE) {
                toggle_img1.setImageResource(R.drawable.drop_up_arrow)
            } else {
                toggle_img1.setImageResource(R.drawable.drop_down_arrow)
            }
        }

        toggle_img2.setOnClickListener {
            // Toggle the visibility of the TextView
            Bad_for_health_textview2.visibility = if (Bad_for_health_textview2.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }

            if (Bad_for_health_textview2.visibility == View.VISIBLE) {
                toggle_img2.setImageResource(R.drawable.drop_up_arrow)
            } else {
                toggle_img2.setImageResource(R.drawable.drop_down_arrow)
            }
        }

        toggle_img3.setOnClickListener {
            // Toggle the visibility of the TextView
            Good_for_health_textview3.visibility = if (Good_for_health_textview3.visibility == View.VISIBLE) {
                View.GONE
            } else {
                View.VISIBLE
            }

            if (Good_for_health_textview3.visibility == View.VISIBLE) {
                toggle_img3.setImageResource(R.drawable.drop_up_arrow)
            } else {
                toggle_img3.setImageResource(R.drawable.drop_down_arrow)
            }
        }

        // NEW: Favorite icon handling
        val favoriteImage = view.findViewById<ImageView>(R.id.favourites_img)
        favoriteImage.setOnClickListener {
            toggleFavoriteStatus(favoriteImage) // NEW: Handle favorite toggle
        }

        recipeId?.let { checkIfFavorite(it) }

        fetchRecipeView()

        return view
    }

    private fun toggleFavoriteStatus(favoriteImage: ImageView) {
        lifecycleScope.launch {
            recipeId?.let { id ->
                if (isFavorite) {
                    removeFromFavorites(id) // NEW: Remove from favorites
                    favoriteImage.setImageResource(R.drawable.heart_empty)
                    isFavorite = false
                } else {
                    addToFavorites(id) // NEW: Add to favorites
                    favoriteImage.setImageResource(R.drawable.heart_filled)
                    isFavorite = true
                }
            }
        }
    }

    private suspend fun addToFavorites(recipeId: Int) {
        val title = view?.findViewById<TextView>(R.id.recipeid_textview1)?.text.toString()
        val imageUrl = view?.findViewById<ImageView>(R.id.fragment_dish_image)?.tag.toString()
        val cookingTime = view?.findViewById<TextView>(R.id.box1_text2)?.text.toString().toInt()

        val favorite = FavouritesEntity(recipeId, title, imageUrl, cookingTime)
        withContext(Dispatchers.IO) {
            database.favouritesDAO().addToFavorites(favorite)
        }
        Toast.makeText(requireContext(), "Added to favorites", Toast.LENGTH_SHORT).show()
    }


    private suspend fun removeFromFavorites(recipeId: Int) {
        val favorite = FavouritesEntity(recipeId, "", "", 0) // Only ID is needed to remove
        withContext(Dispatchers.IO) {
            database.favouritesDAO().removeFromFavorites(favorite)
        }
        Toast.makeText(requireContext(), "Removed from favorites", Toast.LENGTH_SHORT).show()
    }

    private fun fetchRecipeView() {
        val apiKey = "6511024c4bb146f09491fe45f612b0ab" //"195f87d5a199467797f27b34555430e1" //"7e09bf0f61914144b91065b5d90803ea"
        val retrofit = RetrofitClient.retrofit
        val call = recipeId?.let { retrofit.getRecipieView(it, apiKey) }

        call?.enqueue(object : Callback<RecipieView> {
            override fun onResponse(call: Call<RecipieView>, response: Response<RecipieView>) {
                Log.d("TAG", "onResponse: Success")
                if (response.isSuccessful) {
                    response.body()?.let {
                        val list = mutableListOf<Ingredient>()
                        it.analyzedInstructions.forEach {
                            it.steps.forEach {
                                list.addAll(it.ingredients)
                            }
                        }

                        ingredientsAdapter = IngredientsAdapter(list)
                        view?.findViewById<RecyclerView>(R.id.ingredients_rv)?.adapter = ingredientsAdapter

                        val list2 = mutableListOf<Equipment>()
                        it.analyzedInstructions.forEach {
                            it.steps.forEach {
                                list2.addAll(it.equipment)
                            }
                        }
                        equipmentsAdapter = EquipmentsAdapter(list2)
                        view?.findViewById<RecyclerView>(R.id.equipments_rv)?.adapter = equipmentsAdapter

                        view?.let { it1 ->
                            Glide.with(requireContext())
                                .load(it.image)
                                .into(it1.findViewById(R.id.fragment_dish_image))
                        }

                        val recipeid_textview1 = view?.findViewById<TextView>(R.id.recipeid_textview1)
                        if (recipeid_textview1 != null) {
                            recipeid_textview1.text = it.title
                        }

                        val recipeid_textview3 = view?.findViewById<TextView>(R.id.recipeid_textview3)
                        if (recipeid_textview3 != null) {
                            recipeid_textview3.text = it.instructions
                        }

                        val recipeid_textview6 = view?.findViewById<TextView>(R.id.recipeid_textview6)
                        if (recipeid_textview6 != null) {
                            recipeid_textview6.text = it.summary
                        }

                        val box1_text2 = view?.findViewById<TextView>(R.id.box1_text2)
                        if (box1_text2 != null) {
                            box1_text2.text = it.readyInMinutes.toString()
                        }

                        val box2_text2 = view?.findViewById<TextView>(R.id.box2_text2)
                        if (box2_text2 != null) {
                            box2_text2.text = it.servings.toString()
                        }

                        val box3_text2 = view?.findViewById<TextView>(R.id.box3_text2)
                        if (box3_text2 != null) {
                            box3_text2.text = it.pricePerServing.toString()
                        }
                    }
                }
            }

            override fun onFailure(p0: Call<RecipieView>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun checkIfFavorite(recipeId: Int) {
        // Observe LiveData to get the favorites list
        database.favouritesDAO().getAllFavorites().observe(viewLifecycleOwner) { favoritesList ->
            val favorite = favoritesList.find { it.id == recipeId }
            isFavorite = favorite != null
            val favoriteImage = view?.findViewById<ImageView>(R.id.favourites_img)
            favoriteImage?.setImageResource(
                if (isFavorite) R.drawable.heart_filled
                else R.drawable.heart_empty
            )
        }
    }


    companion object {
        private const val ARG_RECIPE_ID = "recipe_id"

        fun newInstance(recipeId: Int) = recipie_view().apply {
            arguments = Bundle().apply {
                putInt(ARG_RECIPE_ID, recipeId)
            }
        }
    }
}
