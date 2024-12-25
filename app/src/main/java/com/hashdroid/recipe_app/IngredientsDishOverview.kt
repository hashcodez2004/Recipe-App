package com.hashdroid.recipe_app

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hashdroid.recipe_app.network.DishOverview
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

class IngredientsDishOverview : BottomSheetDialogFragment() {
    private var recipieId: Int? = null
    private lateinit var ingredientAdapter: IngredientsAdapter
    private lateinit var ingredientsDishoverview_rv: RecyclerView
    private lateinit var button: LinearLayout
    private lateinit var btn1: ImageView
    private lateinit var database: FavouritesDB
    private var isFavorite: Boolean = false
    private lateinit var imgTitle: String
    private lateinit var imageUrl: String
    private var cookingTime: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipieId = it.getInt(ARG_RECIPIE_ID)
        }

        database = FavouritesDB.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ingredients_dishoverview, container, false)

        val back=view.findViewById<ImageView>(R.id.back_arrow)
        back.setOnClickListener{
            val nextFragment = recipieId?.let {
                    id -> DishOverview.newInstance(id)
            }

            dismiss()
            nextFragment?.show(parentFragmentManager, "DishOverview")
        }

        btn1 = view.findViewById(R.id.nutrition_image)
        btn1.setOnClickListener{
            val nextFragment = recipieId?.let { id -> DishOverview.newInstance(id) }
            dismiss()
            nextFragment?.show(parentFragmentManager, "IngredientsDishOverview")
        }

        // NEW: Favorite icon handling
        val favoriteImage = view.findViewById<ImageView>(R.id.favourites_dishOverview)
        favoriteImage.setOnClickListener {
            toggleFavoriteStatus(favoriteImage) // NEW: Handle favorite toggle
        }

        recipieId?.let { checkIfFavorite(it) }

        //initialised the recycler view
        ingredientsDishoverview_rv = view.findViewById(R.id.recycler_view_dishoverview)
        val gridLayoutManager = GridLayoutManager(context, 3)
        ingredientsDishoverview_rv.layoutManager = gridLayoutManager

        button = view.findViewById(R.id.btn_ingredients_dishoverview)
        button.setOnClickListener {
            val nextFragment = recipieId?.let {
                id -> Full_Recipie_Dishoverview.newInstance(id)
            }

            dismiss()
            nextFragment?.show(parentFragmentManager, "Full_Recipe_Dishoverview")
        }

        fetchDishOverviewIngredients()
        return view
    }

    private fun checkIfFavorite(recipeId: Int) {
        // Observe LiveData to get the favorites list
        database.favouritesDAO().getAllFavorites().observe(viewLifecycleOwner) { favoritesList ->
            val favorite = favoritesList.find { it.id == recipeId }
            isFavorite = favorite != null
            val favoriteImage = view?.findViewById<ImageView>(R.id.favourites_dishOverview)
            favoriteImage?.setImageResource(
                if (isFavorite) R.drawable.heart_filled
                else R.drawable.heart_empty
            )
        }
    }

    private fun toggleFavoriteStatus(favoriteImage: ImageView) {
        lifecycleScope.launch {
            recipieId?.let { id ->
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomsheet = dialog!!.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)!!
        val behaviour = BottomSheetBehavior.from(bottomsheet)
        behaviour.maxHeight = (resources.displayMetrics.heightPixels * 0.8).toInt()
        behaviour.peekHeight = (resources.displayMetrics.heightPixels * 0.8).toInt()
    }

    private fun fetchDishOverviewIngredients(){
        val retrofit = RetrofitClient.retrofit
        val call = recipieId?.let { retrofit.getRecipieView(it) }

        call?.enqueue(object : Callback<RecipieView> {
            override fun onResponse(call: Call<RecipieView>, response: Response<RecipieView>) {
                if(response.isSuccessful) {
                    Log.d(TAG, "API call successful: ${response.code()}")
                    Toast.makeText(requireContext(), "API call successful!", Toast.LENGTH_SHORT).show()
                    response.body()?.let {

                        val title = view?.findViewById<TextView>(R.id.imgTitle_dishOverview)
                        title?.text = it.title

                        val list = mutableListOf<Ingredient>()
                        it.analyzedInstructions.forEach {
                            it.steps.forEach {
                                list.addAll(it.ingredients)
                            }
                        }

                        imgTitle = it.title
                        imageUrl = it.image
                        cookingTime = it.cookingMinutes

                        ingredientAdapter = IngredientsAdapter(list)
                        ingredientsDishoverview_rv.adapter = ingredientAdapter
                    }
                }
                else {
                    Log.e(TAG, "API call failed: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "API call failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(p0: Call<RecipieView>, p1: Throwable) {
                Toast.makeText(requireContext(), p1.localizedMessage, Toast.LENGTH_SHORT).show()
            }

        })
    }

    companion object {
        private const val ARG_RECIPIE_ID = "recipie_id"

        fun newInstance(recipieId: Int) =
            IngredientsDishOverview().apply {
                arguments = Bundle().apply {
                    putInt(ARG_RECIPIE_ID, recipieId)
                }
            }
    }
}