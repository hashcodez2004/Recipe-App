package com.hashdroid.recipe_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hashdroid.recipe_app.network.DishOverview
import com.hashdroid.recipe_app.network.Equipment
import com.hashdroid.recipe_app.network.FavouritesDB
import com.hashdroid.recipe_app.network.FavouritesEntity
import com.hashdroid.recipe_app.network.RecipieView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Full_Recipie_Dishoverview : BottomSheetDialogFragment() {
    private lateinit var equipmentsAdapter: EquipmentsAdapter
    private var recipeId: Int? = null
    private lateinit var button_dishOverview: LinearLayout
    private lateinit var btn1: ImageView
    private lateinit var btn2: ImageView
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
        }

        database = FavouritesDB.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_full__recipie__dishoverview, container, false)

        val back=view.findViewById<ImageView>(R.id.back_arrow)
        back.setOnClickListener{
            val nextFragment = recipeId?.let { id -> IngredientsDishOverview.newInstance(id) }
            dismiss()
            nextFragment?.show(parentFragmentManager, "IngredientsDishOverview")
        }

        // NEW: Favorite icon handling
        val favoriteImage = view.findViewById<ImageView>(R.id.favourites_dishOverview)
        favoriteImage.setOnClickListener {
            toggleFavoriteStatus(favoriteImage) // NEW: Handle favorite toggle
        }

        recipeId?.let { checkIfFavorite(it) }

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

        //passing id to the next fragment
        button_dishOverview = view.findViewById(R.id.btn_fullRecipie_dishoverview)
        button_dishOverview.setOnClickListener {
            val nextFragment = recipeId?.let {
                id -> similar_recipe_dishoverview.newInstance(
                recipeId = id,
                imgUrl = imageUrl,
                imgTitle = imgTitle,
                cookingTime = cookingTime,
                heading = heading
                )
            }
            dismiss()
            nextFragment?.show(parentFragmentManager, "fragment_full__recipie__dishoverview")
        }

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

        fetchRecipeView()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomsheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomsheet.let {
            val behaviour = it?.let { it1 -> BottomSheetBehavior.from(it1) }
            val maxHeight = (resources.displayMetrics.heightPixels * 0.8).toInt()
            if (it != null) {
                it.layoutParams.height = maxHeight
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
                if (isFavorite) R.drawable.heart_filled
                else R.drawable.heart_empty
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

    private fun fetchRecipeView() {
//        val apiKey = "7e09bf0f61914144b91065b5d90803ea" //"6511024c4bb146f09491fe45f612b0ab" //"195f87d5a199467797f27b34555430e1" //"7e09bf0f61914144b91065b5d90803ea"
        val retrofit = RetrofitClient.retrofit
        val call = recipeId?.let { retrofit.getRecipieView(it) }

        call?.enqueue(object : Callback<RecipieView> {
            override fun onResponse(call: Call<RecipieView>, response: Response<RecipieView>) {
                Log.d("TAG", "onResponse: Success")
                if (response.isSuccessful) {
                    response.body()?.let {

                        imgTitle = it.title
                        imageUrl = it.image
                        cookingTime = it.readyInMinutes
                        heading = it.title

                        val title = view?.findViewById<TextView>(R.id.imgTitle_dishOverview)
                        title?.text = it.title

                        //loading equipments
                        val list2 = mutableListOf<Equipment>()
                        it.analyzedInstructions.forEach {
                            it.steps.forEach {
                                list2.addAll(it.equipment)
                            }
                        }
                        equipmentsAdapter = EquipmentsAdapter(list2)
                        view?.findViewById<RecyclerView>(R.id.equipments_rv)?.adapter = equipmentsAdapter

                        val recipeid_textview3 = view?.findViewById<TextView>(R.id.recipeid_textview3)
                        if (recipeid_textview3 != null) {
                            recipeid_textview3.text = it.instructions
                        }

                        val recipeid_textview6 = view?.findViewById<TextView>(R.id.recipeid_textview6)
                        if (recipeid_textview6 != null) {
                            recipeid_textview6.text = it.summary
                        }
                    }
                }
            }

            override fun onFailure(p0: Call<RecipieView>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        private const val ARG_RECIPE_ID = "recipe_id"

        fun newInstance(recipeId: Int) = Full_Recipie_Dishoverview().apply {
            arguments = Bundle().apply {
                putInt(ARG_RECIPE_ID, recipeId)
            }
        }
    }
}