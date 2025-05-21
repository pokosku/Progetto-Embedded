package com.myapp.chefgpt.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import com.google.mediapipe.tasks.genai.llminference.LlmInference.LlmInferenceOptions
import com.myapp.chefgpt.ml.AutoModel1
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.tensorflow.lite.support.image.TensorImage

class ModelsHelper(private val context: Context) {
    private lateinit var imageClassificationModel: AutoModel1
    private var textGenerationModel: LlmInference? = null





    fun classifyFood(imageDrawable: Drawable): String{

        val model = AutoModel1.newInstance(context)
        val image = Bitmap.createBitmap((imageDrawable as BitmapDrawable).bitmap)
        val imageBitmap = Bitmap.createScaledBitmap(image, 192, 192, true)
        val input = TensorImage.fromBitmap(imageBitmap)
        val outputs = model.process(input)
        val probability = outputs.probabilityAsCategoryList
        val best = (probability.maxByOrNull { it.score })!!.label
        return best
    }

    suspend fun generateRecipe(){
        textGenerationModel = withContext(Dispatchers.IO){
            loadTextGenerationModel()
        }
    }

    private fun loadTextGenerationModel(): LlmInference? {
        return try {
            val taskOptions = LlmInferenceOptions.builder()
                .setModelPath("/data/local/tmp/llm/gemma3-1B-it-int4.task")
                .setMaxTopK(64)
                .setPreferredBackend(LlmInference.Backend.CPU)
                //TODO : verificare maxTokens
                .setMaxTokens(350)
                .build()
            LlmInference.createFromOptions(context,taskOptions)
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }
}