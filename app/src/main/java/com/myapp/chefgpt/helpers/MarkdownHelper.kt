package com.myapp.chefgpt.helpers

import android.content.Context
import android.widget.TextView
import io.noties.markwon.Markwon

//Classe per formattare il testo in Markdown

//Il costruttore richiede i parametri di testo, context dell'Activity, e TextView su cui applicare la formattazione
class MarkdownHelper(private val text : String, private val context : Context, private val textView : TextView) {
    private val markwon = Markwon.create(context)
    private val fixedMarkdown = fixNumberedMarkdownList()

    //Metodo per formattare del testo fornito come Markdown list, tipico output dei modelli di Natural Language Processing
    private fun fixNumberedMarkdownList(): String {
        val lines = text.lines()
        val result = StringBuilder()
        val numberedItemRegex = Regex("^\\d+\\.\\s+.*")
        var inList = false

        for (i in lines.indices) {
            val line = lines[i]
            if (line.matches(numberedItemRegex)) {
                inList = true
                result.appendLine(line)
            } else if (inList && line.isNotBlank()) {
                // Se siamo dentro una lista numerata e la riga non inizia con "n. ", la indentiamo
                result.appendLine("   $line")
            } else {
                inList = false
                result.appendLine(line)
            }
        }
        return result.toString()
    }
    fun format(){
        markwon.setMarkdown(textView, fixedMarkdown)
    }
}