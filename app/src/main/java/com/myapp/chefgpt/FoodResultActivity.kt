package com.myapp.chefgpt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FoodResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foodresult)

        val buttonToRecipeResult: Button =findViewById(R.id.toRecipeResult)
        buttonToRecipeResult.setOnClickListener{ view->
            val intent= Intent(view.context,RecipeResultActivity::class.java)
            startActivity(intent)
        }
        val FoodNameTv : TextView=findViewById(R.id.FoodName)
        FoodNameTv.text=intent.getStringExtra("foodname")
    }
}