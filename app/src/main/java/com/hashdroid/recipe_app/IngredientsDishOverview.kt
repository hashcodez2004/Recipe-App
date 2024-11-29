package com.hashdroid.recipe_app

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hashdroid.recipe_app.network.Ingredient
import com.hashdroid.recipe_app.network.RecipieView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class IngredientsDishOverview : BottomSheetDialogFragment() {
    private var recipieId: Int? = null
    private lateinit var ingredientAdapter: IngredientsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipieId = it.getInt(ARG_RECIPIE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ingredients_dishoverview, container, false)

        // Initialize the recycler view
        val ingredientsDishoverview_rv = view.findViewById<RecyclerView>(R.id.recycler_view_dishoverview)
        val gridLayoutManager = GridLayoutManager(context, 3)
        ingredientsDishoverview_rv.layoutManager = gridLayoutManager

        fetchDishOverviewIngredients(ingredientsDishoverview_rv)  // Pass RecyclerView here
        return view
    }

    private fun fetchDishOverviewIngredients(ingredientsDishoverview_rv: RecyclerView) {
        val apiKey = "6511024c4bb146f09491fe45f612b0ab"
        val retrofit = RetrofitClient.retrofit
        val call = recipieId?.let { retrofit.getRecipieView(it, apiKey) }

        call?.enqueue(object : Callback<RecipieView> {
            override fun onResponse(call: Call<RecipieView>, response: Response<RecipieView>) {
                if (response.isSuccessful) {
                    Log.d(TAG, "API call successful: ${response.code()}")
                    Toast.makeText(requireContext(), "API call successful!", Toast.LENGTH_SHORT).show()

                    response.body()?.let {
                        val list = mutableListOf<Ingredient>()

                        // Extract ingredients from analyzed instructions
                        it.analyzedInstructions.forEach { instruction ->
                            instruction.steps.forEach { step ->
                                list.addAll(step.ingredients)
                            }
                        }

                        // Initialize the adapter with the fetched data
                        ingredientAdapter = IngredientsAdapter(list)

                        // Set the adapter to RecyclerView after data is fetched
                        ingredientsDishoverview_rv.adapter = ingredientAdapter

                        // Notify the adapter to update the UI
                        ingredientAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e(TAG, "API call failed: ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "API call failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RecipieView>, t: Throwable) {
                Toast.makeText(requireContext(), t.localizedMessage, Toast.LENGTH_SHORT).show()
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
