package com.myapp.chefgpt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ImagePredictionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_imageprediction)
        val string = "zavagay"
        val buttonToFoodResult: Button=findViewById(R.id.toFoodResult)
        buttonToFoodResult.setOnClickListener{ view->
            val intent= Intent(view.context,FoodResultActivity::class.java)
            intent.putExtra("foodname",string)
            startActivity(intent)
        }

    }

}