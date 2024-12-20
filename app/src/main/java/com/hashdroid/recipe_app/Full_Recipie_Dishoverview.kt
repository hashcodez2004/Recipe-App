package com.hashdroid.recipe_app

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hashdroid.recipe_app.network.Equipment
import com.hashdroid.recipe_app.network.RecipieView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Full_Recipie_Dishoverview : BottomSheetDialogFragment() {
    private lateinit var equipmentsAdapter: EquipmentsAdapter
    private var recipeId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            recipeId = it.getInt(ARG_RECIPE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_full__recipie__dishoverview, container, false)

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

    private fun fetchRecipeView() {
        val apiKey = "6511024c4bb146f09491fe45f612b0ab" //"195f87d5a199467797f27b34555430e1" //"7e09bf0f61914144b91065b5d90803ea"
        val retrofit = RetrofitClient.retrofit
        val call = recipeId?.let { retrofit.getRecipieView(it, apiKey) }

        call?.enqueue(object : Callback<RecipieView> {
            override fun onResponse(call: Call<RecipieView>, response: Response<RecipieView>) {
                Log.d("TAG", "onResponse: Success")
                if (response.isSuccessful) {
                    response.body()?.let {

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