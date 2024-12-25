package com.hashdroid.recipe_app.network

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hashdroid.recipe_app.IngredientsDishOverview
import com.hashdroid.recipe_app.R
import com.hashdroid.recipe_app.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DishOverview: BottomSheetDialogFragment() {
    private lateinit var button_dishOverview: LinearLayout
    private var recipiId: Int? = null
    private lateinit var database: FavouritesDB
    private var isFavorite: Boolean = false
    private lateinit var imgTitle: String
    private lateinit var imageUrl: String
    private var cookingTime: Int = 0

    private val TAG = "DishOverview"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            recipiId = it?.getInt(ARG_RECIPE_ID)
        }

        database = FavouritesDB.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_dish_overview, container, false)

        val back=view.findViewById<ImageView>(R.id.back_arrow)
        back.setOnClickListener{
            dismiss()
        }

        //passing the id to the next fragment on button click
        button_dishOverview = view.findViewById(R.id.btn_dishoverview)
        button_dishOverview.setOnClickListener{
            val nextFragment = recipiId?.let { id -> IngredientsDishOverview.newInstance(id) }

            dismiss()
            nextFragment?.show(parentFragmentManager, "IngredientsDishOverview")
        }


        // NEW: Favorite icon handling
        val favoriteImage = view.findViewById<ImageView>(R.id.favourites_dishOverview)
        favoriteImage.setOnClickListener {
            toggleFavoriteStatus(favoriteImage) // NEW: Handle favorite toggle
        }

        Log.d("recipeId", "${recipiId}")
        recipiId?.let { checkIfFavorite(it) }

        // Call fetchDishOverview
        fetchDishOverview()

        // Return the view at the end
        return view
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val bottomsheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
//        bottomsheet.let {
//            val behaviour = it?.let { it1 -> BottomSheetBehavior.from(it1) }
//            val maxHeight = (resources.displayMetrics.heightPixels * 0.8).toInt()
//            if (it != null) {
//                it.layoutParams.height = maxHeight
//            }
//            behaviour?.state = BottomSheetBehavior.STATE_EXPANDED
//        }
//    }

    private fun checkIfFavorite(recipeId: Int) {
        // Observe LiveData to get the favorites list
        database.favouritesDAO().getAllFavorites().observe(viewLifecycleOwner) { favoritesList ->
            val favorite = favoritesList.find { it.id == recipeId }
            isFavorite = favorite != null
            val favoriteImage = view?.findViewById<ImageView>(R.id.favourites_dishOverview)
            favoriteImage!!.setImageResource(
                if (isFavorite) R.drawable.heart_filled
                else R.drawable.heart_empty
            )
        }
    }

    private fun toggleFavoriteStatus(favoriteImage: ImageView) {
        lifecycleScope.launch {
            recipiId?.let { id ->
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


    private fun fetchDishOverview() {
//        val apiKey = "6511024c4bb146f09491fe45f612b0ab"
        val retrofit = RetrofitClient.retrofit
        val call = recipiId?.let { retrofit.getRecipieView(it) }

        call?.enqueue(object : Callback<RecipieView> {
            override fun onResponse(call: Call<RecipieView>, response: Response<RecipieView>) {
                if(response.isSuccessful) {
                    Log.d(TAG, "API call successful: ${response.code()}")
                    Toast.makeText(requireContext(), "API call successful!", Toast.LENGTH_SHORT).show()
                    response.body()?.let {
                        val imgTitle_dishOverview = view?.findViewById<TextView>(R.id.imgTitle_dishOverview)
                        imgTitle_dishOverview?.text = it.title
                        val img_dishOverview = view?.findViewById<ImageView>(R.id.img_dishOverview)
                        view?.let { it1 ->
                            if (img_dishOverview != null) {
                                Glide.with(requireContext())
                                    .load(it.image)
                                    .into(img_dishOverview)
                            }
                        }

                        imgTitle = it.title
                        imageUrl = it.image
                        cookingTime = it.readyInMinutes

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
        private const val ARG_RECIPE_ID = "recipe_id"

        fun newInstance(recipeId: Int) = DishOverview().apply {
            arguments = Bundle().apply {
                putInt(ARG_RECIPE_ID, recipeId)
            }
        }
    }
}