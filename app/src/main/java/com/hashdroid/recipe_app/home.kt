package com.hashdroid.recipe_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hashdroid.recipe_app.com.hashdroid.recipe_app.VerticalAdapter
import com.hashdroid.recipe_app.network.RecipeResponse
import com.hashdroid.recipe_app.network.RecipeResponse2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private lateinit var horizontalAdapter: HorizontalAdapter
    private lateinit var verticalAdapter: VerticalAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize RecyclerView
        val recyclerView1 = view.findViewById<RecyclerView>(R.id.recycler_view1)
        recyclerView1.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val recyclerView2 = view.findViewById<RecyclerView>(R.id.recycler_view2)
        recyclerView2.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        // Fetch random recipes
        fetchRandomRecipes(view)
        fetchAllRecipes(view)

        return view
    }

    private fun fetchRandomRecipes(view: View) {
        val apiKey = "195f87d5a199467797f27b34555430e1"
        val retrofit = RetrofitClient.retrofit
        val call = retrofit.getRandomRecipes(10, apiKey)
        call.enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                if (response.isSuccessful) {
                    response.body()?.recipes?.let { recipes ->
                        horizontalAdapter = HorizontalAdapter(recipes)
                        view.findViewById<RecyclerView>(R.id.recycler_view1).adapter = horizontalAdapter
                    }
                }
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                // Handle API call failure
            }
        })
    }

    private fun fetchAllRecipes(view: View) {
        val apiKey = "195f87d5a199467797f27b34555430e1"
        val retrofit = RetrofitClient.retrofit
        val call = retrofit.getAllRecipes(50, apiKey)
        call.enqueue(object : Callback<RecipeResponse2> {
            override fun onResponse(call: Call<RecipeResponse2>, response: Response<RecipeResponse2>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let { recipes ->
                        verticalAdapter = VerticalAdapter(recipes)
                        view.findViewById<RecyclerView>(R.id.recycler_view2).adapter = verticalAdapter
                    }
                }
            }

            override fun onFailure(call: Call<RecipeResponse2>, t: Throwable) {
                // Handle API call failure
            }
        })
    }
}
