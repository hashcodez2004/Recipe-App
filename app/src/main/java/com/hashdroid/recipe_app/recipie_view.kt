package com.hashdroid.recipe_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class recipie_view : Fragment() {

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
        val view = inflater.inflate(R.layout.fragment_recipie_view, container, false)

        val recipeIdTextView: TextView = view.findViewById(R.id.recipe_id_text_view)
        recipeIdTextView.text = "Recipe ID: $recipeId"

        // Here you can use recipeId to fetch and display data or call an API
        return view
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
