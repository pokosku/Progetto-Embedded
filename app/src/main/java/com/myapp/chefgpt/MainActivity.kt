package com.myapp.chefgpt

import android.os.Bundle

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable

import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.myapp.chefgpt.ml.AutoModel1
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInference.LlmInferenceOptions
import org.tensorflow.lite.support.image.TensorImage


class MainActivity : AppCompatActivity() {

    private lateinit var takePictureLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the configuration options for the LLM Inference task
        val taskOptions = LlmInferenceOptions.builder()
            .setModelPath("/data/local/tmp/llm/gemma3-1B-it-int4.task")
            .setMaxTopK(64)
            .setPreferredBackend(LlmInference.Backend.CPU)
            .build()

        val buttonToImagePrediction: Button = findViewById(R.id.toImagePredictionButton)
        buttonToImagePrediction.setOnClickListener{ view->
            val intent= Intent(view.context,ImagePredictionActivity::class.java)
            startActivity(intent)
        }
// Create an instance of the LLM Inference task
        val llmInference = LlmInference.createFromOptions(this, taskOptions)


        val model = AutoModel1.newInstance(this)

        val takePictureButton = findViewById<Button>(R.id.takePictureButton)
        val pickImageButton = findViewById<Button>(R.id.pickImageButton)
        val imageView = findViewById<ImageView>(R.id.imageView)
        val predictButton = findViewById<Button>(R.id.predictButton)
        val predictionView = findViewById<TextView>(R.id.predictionView)


        var imageBitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)

        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    imageBitmap = result.data?.extras?.get("data") as Bitmap
                    imageView.setImageBitmap(imageBitmap)
                }
            }

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val imageUri = result.data?.data
                    imageView.setImageURI(imageUri)
                }
            }

        takePictureButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureLauncher.launch(intent)
        }

        pickImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        predictButton.setOnClickListener {
            predictionView.text = "Predicting..."
            try {
                val image = Bitmap.createBitmap((imageView.drawable as BitmapDrawable).bitmap)

                imageBitmap = Bitmap.createScaledBitmap(image, 192, 192, true)

                val input = TensorImage.fromBitmap(imageBitmap)

                val outputs = model.process(input)
                val probability = outputs.probabilityAsCategoryList

//            var categoryBest = probability.maxByOrNull { it.score }
//            val label = categoryBest?.label
//            val score = categoryBest?.score?.times(100)
//            val score_percent = "${score.toString().substring(0,5)}%"
//            predictionView.text = "I'm $score_percent sure this is $label"
                var results: String = "My guesses are:\n"
                var best: String? = ""

                for (i in 0..2) {

                    var categoryBest = probability.maxByOrNull { it.score }

                    val label = categoryBest?.label

                    if (i == 0)
                        best = label
                    val score = categoryBest?.score?.times(100)
                    //val score_percent = "${score.toString().substring(0, 5)}%"
                    probability.remove(categoryBest)
                    categoryBest = probability.maxByOrNull { it.score }
                    //results = "$results\n $label   $score_percent"
                }
                predictionView.text =
                    llmInference.generateResponse("Give me the recipe for $best, without adding anything else")
                imageBitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)

            } catch (e: NullPointerException) {
                predictionView.text = "Upload an image first!"
            }

        }

    }
}