package com.myapp.chefgpt

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class RecipeResultActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reciperesult)

        val textView: TextView = findViewById(R.id.textRecipe)
        val imageView: ImageView= findViewById(R.id.imageView)

        val imageUriString = intent.getStringExtra("foodimage")
        val imageUri = Uri.parse(imageUriString)
        imageView.setImageURI(imageUri)



        val ricetta = """
            🍝 Ricetta della Pasta alla Carbonara
            
            Ingredienti (per 2 persone):
            - 200g di spaghetti
            - 100g di guanciale
            - 2 tuorli d'uovo
            - 50g di pecorino romano grattugiato
            - Sale q.b.
            - Pepe nero q.b.

            Procedimento:
            1. Porta a ebollizione una pentola d’acqua salata e cuoci gli spaghetti al dente.
            2. Nel frattempo, taglia il guanciale a listarelle e rosolalo in padella finché diventa croccante.
            3. In una ciotola, sbatti i tuorli con il pecorino e un po’ di pepe nero.
            4. Scola la pasta e conservala un po’ d’acqua di cottura.
            5. Unisci la pasta al guanciale, mescola bene, poi a fuoco spento aggiungi il composto di uova e pecorino.
            6. Aggiungi un cucchiaio di acqua di cottura per rendere il tutto cremoso.
            7. Servi con una spolverata di pepe e pecorino.

            Buon appetito! 😋
        """.trimIndent()

        textView.text=ricetta

    }
}