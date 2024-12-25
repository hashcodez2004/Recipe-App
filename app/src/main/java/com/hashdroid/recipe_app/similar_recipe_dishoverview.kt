package com.hashdroid.recipe_app

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hashdroid.recipe_app.IngredientsDishOverview.Companion
import com.hashdroid.recipe_app.com.hashdroid.recipe_app.SimilarRecipesAdapter
import com.hashdroid.recipe_app.com.hashdroid.recipe_app.VerticalAdapter
import com.hashdroid.recipe_app.network.DishOverview
import com.hashdroid.recipe_app.network.FavouritesDB
import com.hashdroid.recipe_app.network.FavouritesEntity
import com.hashdroid.recipe_app.network.Ingredient
import com.hashdroid.recipe_app.network.RecipeResponse2
import com.hashdroid.recipe_app.network.RecipieView
import com.hashdroid.recipe_app.network.SimilarRecipes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class similar_recipe_dishoverview : BottomSheetDialogFragment() {
    private var recipeId: Int? = null
    private lateinit var adapter: SimilarRecipesAdapter
    private lateinit var btn1: ImageView
    private lateinit var btn2: ImageView
    private lateinit var btn3: ImageView
    private lateinit var database: FavouritesDB
    private var isFavorite: Boolean = false
    private lateinit var imgTitle: String
    private lateinit var imageUrl: String
    private var cookingTime: Int = 0
    private lateinit var heading: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipeId = it.getInt(ARG_RECIPE_ID)
            imgTitle = it.getString(ARG_IMG_TITLE, "")
            imageUrl = it.getString(ARG_IMG_URL, "")
            cookingTime = it.getInt(ARG_COOKING_TIME, 0)
            heading = it.getString(ARG_HEADING, "")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_similar_recipe_dishoverview, container, false)

        val back=view.findViewById<ImageView>(R.id.back_arrow)
        back.setOnClickListener{
            val nextFragment = recipeId?.let { id -> Full_Recipie_Dishoverview.newInstance(id) }
            dismiss()
            nextFragment?.show(parentFragmentManager, "Full_Recipie_Dishoverview")
        }

        database = FavouritesDB.getDatabase(requireContext())

        val title = view.findViewById<TextView>(R.id.imgTitle_dishOverview)
        title.text = heading
        
        btn1 = view.findViewById(R.id.ingredient_image)
        btn1.setOnClickListener{
            val nextFragment = recipeId?.let { id -> DishOverview.newInstance(id) }
            dismiss()
            nextFragment?.show(parentFragmentManager, "DishOverview")
        }

        btn2 = view.findViewById(R.id.full_recipie_image)
        btn2.setOnClickListener{
            val nextFragment = recipeId?.let { id -> IngredientsDishOverview.newInstance(id) }
            dismiss()
            nextFragment?.show(parentFragmentManager, "IngredientsDishOverview")
        }

        btn3 = view.findViewById(R.id.similar_recipes_image)
        btn3.setOnClickListener{
            val nextFragment = recipeId?.let { id -> Full_Recipie_Dishoverview.newInstance(id) }
            dismiss()
            nextFragment?.show(parentFragmentManager, "Full_Recipie_Dishoverview")
        }

        // NEW: Favorite icon handling
        val favoriteImage = view.findViewById<ImageView>(R.id.favourites_dishOverview)
        favoriteImage.setOnClickListener {
            toggleFavoriteStatus(favoriteImage) // NEW: Handle favorite toggle
        }

        recipeId?.let { checkIfFavorite(it) }

        fetchSimilarRecipes(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomsheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomsheet.let {
            val behaviour = it?.let { it1 -> BottomSheetBehavior.from(it1) }
            val peakHeight = (resources.displayMetrics.heightPixels * 0.8).toInt()
            if (it != null) {
                it.layoutParams.height = peakHeight
            }
            behaviour?.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun checkIfFavorite(recipeId: Int) {
        // Observe LiveData to get the favorites list
        database.favouritesDAO().getAllFavorites().observe(viewLifecycleOwner) { favoritesList ->
            val favorite = favoritesList.find { it.id == recipeId }
            isFavorite = favorite != null
            val favoriteImage = view?.findViewById<ImageView>(R.id.favourites_dishOverview)
            favoriteImage?.setImageResource(
                if (isFavorite){
                    Log.d("DB Debugging", "present in DB")
                    R.drawable.heart_filled
                }
                else{
                    Log.d("DB Debugging", "not present in DB")
                    R.drawable.heart_empty
                }
            )
        }
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

        val favorite = FavouritesEntity(recipeId, imgTitle, imageUrl, cookingTime)
        withContext(Dispatchers.IO) {
            Log.d("DatabaseDebug", "Title: $imgTitle, Image URL: $imageUrl, Cooking Time: $cookingTime")

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

    private fun fetchSimilarRecipes(view: View) {
//        val apiKey = "7e09bf0f61914144b91065b5d90803ea" //"6511024c4bb146f09491fe45f612b0ab" //"7e09bf0f61914144b91065b5d90803ea" //195f87d5a199467797f27b34555430e1
        val retrofit = RetrofitClient.retrofit
        val call: Call<List<SimilarRecipes>>? = recipeId?.let { retrofit.getSimilarRecipes(it) }
        call?.enqueue(object : Callback<List<SimilarRecipes>>{
            override fun onResponse(call: Call<List<SimilarRecipes>>, response: Response<List<SimilarRecipes>>) {
                Log.d("TAG", "onResponse: Success")
                if (response.isSuccessful) {
                    response.body()?.let { recipes ->
                        Log.d("size", "${recipes.size}")
                        val recyclerView = view.findViewById<RecyclerView>(R.id.similar_recipes_rv)
                        recyclerView.layoutManager = LinearLayoutManager(context)

                        adapter = SimilarRecipesAdapter(recipes)
                        recyclerView.adapter = adapter

                    }
                }
            }

            override fun onFailure(call: Call<List<SimilarRecipes>>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

//    companion object {
//        private const val ARG_RECIPE_ID = "recipie_id"
//
//        fun newInstance(recipeId: Int) =
//            similar_recipe_dishoverview().apply {
//                arguments = Bundle().apply {
//                    putInt(ARG_RECIPE_ID, recipeId)
//                }
//            }
//    }

    companion object {
        private const val ARG_RECIPE_ID = "recipie_id"
        private const val ARG_IMG_URL = "imgUrl"
        private const val ARG_IMG_TITLE = "imgTitle"
        private const val ARG_COOKING_TIME = "cookingTime"
        private const val ARG_HEADING = "heading"

        fun newInstance(recipeId: Int, imgUrl: String, imgTitle: String, cookingTime: Int, heading: String?) =
            similar_recipe_dishoverview().apply {
                arguments = Bundle().apply {
                    putInt(ARG_RECIPE_ID, recipeId)
                    putString(ARG_IMG_URL, imgUrl)
                    putString(ARG_IMG_TITLE, imgTitle)
                    putInt(ARG_COOKING_TIME, cookingTime)
                    putString(ARG_HEADING, heading)
                }
            }
    }
}