package com.hashdroid.recipe_app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hashdroid.recipe_app.com.hashdroid.recipe_app.VerticalAdapter
import com.hashdroid.recipe_app.network.RecipeResponse
import com.hashdroid.recipe_app.network.RecipeResponse2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity2 : AppCompatActivity() {

    private lateinit var horizontalAdapter: HorizontalAdapter
    private lateinit var verticalAdapter: VerticalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        // Initialize RecyclerView
        val recyclerView1 = findViewById<RecyclerView>(R.id.recycler_view1)
        recyclerView1.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val recyclerView2 = findViewById<RecyclerView>(R.id.recycler_view2)
        recyclerView2.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        // Fetch random recipes
        fetchRandomRecipes()
        fetchAllRecipes()
    }



    private fun fetchRandomRecipes() {
        val apiKey = "195f87d5a199467797f27b34555430e1"
        val retrofit = RetrofitClient.retrofit
        val call = retrofit.getRandomRecipes(10, apiKey)
        call.enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                if (response.isSuccessful) {
                    response.body()?.recipes?.let { recipes ->
                        horizontalAdapter = HorizontalAdapter(recipes)
                        findViewById<RecyclerView>(R.id.recycler_view1).adapter = horizontalAdapter
                    }
                }
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                // Handle API call failure
            }
        })
    }

    private fun fetchAllRecipes() {
        val apiKey = "195f87d5a199467797f27b34555430e1"
        val retrofit = RetrofitClient.retrofit
        val call = retrofit.getAllRecipes(50, apiKey)
        call.enqueue(object : Callback<RecipeResponse2> {
            override fun onResponse(call: Call<RecipeResponse2>, response: Response<RecipeResponse2>) {
                if (response.isSuccessful) {
                    response.body()?.results?.let { recipes ->
                        verticalAdapter = VerticalAdapter(recipes)
                        findViewById<RecyclerView>(R.id.recycler_view2).adapter = verticalAdapter
                    }
                }
            }

            override fun onFailure(call: Call<RecipeResponse2>, t: Throwable) {
                // Handle API call failure
            }
        })
    }
}
