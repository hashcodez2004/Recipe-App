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
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hashdroid.recipe_app.IngredientsDishOverview
import com.hashdroid.recipe_app.R
import com.hashdroid.recipe_app.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DishOverview: BottomSheetDialogFragment() {
    private lateinit var button_dishOverview: LinearLayout
    private var recipiId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments.let {
            recipiId = it?.getInt(ARG_RECIPE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_dish_overview, container, false)

        //passing the id to the next fragment on button click
        button_dishOverview = view.findViewById(R.id.btn_dishoverview)

        button_dishOverview.setOnClickListener{
            val nextFragment = recipiId?.let { it1 -> IngredientsDishOverview.newInstance(it1) }
//            val bundle = Bundle().apply {
//                recipiId?.let { it1 -> putInt("ARG_RECIPE_ID", it1) }
//            }
//            nextFragment.arguments = bundle


            dismiss()
            if (nextFragment != null) {
                nextFragment.show(parentFragmentManager, "IngredientsDishOverview")
            }
        }


        // Call fetchDishOverview
        fetchDishOverview()

        // Return the view at the end
        return view
    }


    private fun fetchDishOverview() {
        val apiKey = "6511024c4bb146f09491fe45f612b0ab"
        val retrofit = RetrofitClient.retrofit
        val call = recipiId?.let { retrofit.getRecipieView(it, apiKey) }

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