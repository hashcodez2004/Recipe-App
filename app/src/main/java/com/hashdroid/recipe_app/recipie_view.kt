package com.hashdroid.recipe_app

import android.content.ContentValues.TAG
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.bumptech.glide.Glide
import com.hashdroid.recipe_app.network.Equipment
import com.hashdroid.recipe_app.network.Ingredient
import com.hashdroid.recipe_app.network.RecipeResponse
import com.hashdroid.recipe_app.network.RecipieView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class recipie_view : Fragment() {
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var equipmentsAdapter: EquipmentsAdapter
    private var recipeId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipeId = it.getInt(ARG_RECIPE_ID)
        }
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

        fetchRecipeView()

        return view
    }

    private fun fetchRecipeView() {
        val apiKey = "195f87d5a199467797f27b34555430e1"
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

        fun newInstance(recipeId: Int) = recipie_view().apply {
            arguments = Bundle().apply {
                putInt(ARG_RECIPE_ID, recipeId)
            }
        }
    }
}
