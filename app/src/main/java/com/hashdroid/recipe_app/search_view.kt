package com.example.recipeapp

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hashdroid.recipe_app.R
import com.hashdroid.recipe_app.RetrofitClient
import com.hashdroid.recipe_app.network.DishOverview
import com.hashdroid.recipe_app.network.Recipe
import com.hashdroid.recipe_app.network.SearchViewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SearchViewAdapter
    private val handler = Handler(Looper.getMainLooper()) // For debouncing API calls
    private var searchRunnable: Runnable? = null // Keeps track of the current search task

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_search_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.search_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = SearchViewAdapter(emptyList()) { recipeId ->
            openRecipeDetailFragment(recipeId)
        }

//        adapter = SearchViewAdapter(emptyList())
        recyclerView.adapter = adapter

        // Find EditText
        val editText: EditText = view.findViewById(R.id.search_bar1)
        editText.requestFocus()

        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)

        // Add TextWatcher to EditText
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString().trim()
                searchRunnable?.let { handler.removeCallbacks(it) } // Cancel the previous task
                searchRunnable = Runnable { fetchRecipes(query) }
                handler.postDelayed(searchRunnable!!, 300) // Delay API call by 300ms
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun fetchRecipes(query: String) {
        if (query.isEmpty()) {
            // Clear the RecyclerView if query is empty
            adapter.updateRecipes(emptyList())
            return
        }

        val retrofit = RetrofitClient.retrofit
        val apiKey = "6511024c4bb146f09491fe45f612b0ab"
        val call = retrofit.getAutocompleteRecipes(query, apiKey)

        call.enqueue(object : Callback<List<Recipe>> {
            override fun onResponse(call: Call<List<Recipe>>, response: Response<List<Recipe>>) {
                if (response.isSuccessful) {
                    val recipes = response.body() ?: emptyList()
                    Log.d("API Response", "Number of recipes: ${recipes.size}")
                    adapter.updateRecipes(recipes) // Update RecyclerView adapter
                }
                else{
                    Log.e("API Response", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Recipe>>, t: Throwable) {
                // Handle API failure (e.g., show a message or clear RecyclerView)
                adapter.updateRecipes(emptyList())
            }
        })
    }


    //used for opening new fragment when user clicks on the recycler view item
    private fun openRecipeDetailFragment(recipeId: Int) {
        val fragment = DishOverview.newInstance(recipeId)
        fragment.show(parentFragmentManager, "DishOverview")
    }
}
