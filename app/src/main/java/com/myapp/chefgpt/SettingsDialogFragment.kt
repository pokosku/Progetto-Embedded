package com.myapp.chefgpt

import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import java.util.Locale

class SettingsDialogFragment : DialogFragment() {

    var onDismissListener: (() -> Unit)? = null

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backgroundLayout = view.findViewById<FrameLayout>(R.id.backgroundLayout)
        val contentLayout = view.findViewById<LinearLayout>(R.id.contentLayout)
        val okButton = view.findViewById<Button>(R.id.okButton)

        val themeSpinner = view.findViewById<Spinner>(R.id.themeSpinner)
        val languageSpinner = view.findViewById<Spinner>(R.id.languageSpinner)

        //Chiudi se clicchi FUORI dal contenuto
        backgroundLayout.setOnClickListener {
            dismiss()
        }
        //Ignora i clic SUL contenuto (altrimenti lo chiuderebbe anche lì)
        contentLayout.setOnClickListener {
            // Non fare nulla
        }
        //Chiudi cliccando OK
        okButton.setOnClickListener {
            dismiss()
        }

        // Setta il valore iniziale dello spinner in base al tema salvato
        val prefs = requireContext().getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val savedTheme = prefs.getInt("selected_theme", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        themeSpinner.setSelection(
            when (savedTheme) {
                AppCompatDelegate.MODE_NIGHT_NO -> 0
                AppCompatDelegate.MODE_NIGHT_YES -> 1
                else -> 2 // MODE_NIGHT_FOLLOW_SYSTEM
            }
        )

        themeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedMode = when (position) {
                    0 -> AppCompatDelegate.MODE_NIGHT_NO
                    1 -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }
                if (selectedMode != savedTheme) {
                    prefs.edit().putInt("selected_theme", selectedMode).apply()
                    AppCompatDelegate.setDefaultNightMode(selectedMode)
                    requireActivity().recreate()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        //per la lingua
        val savedLanguage = prefs.getString("selected_language", "en")
        val languages = resources.getStringArray(R.array.language_options)

        val selectedLangIndex = when (savedLanguage) {
            "it" -> languages.indexOf("Italiano")
            else -> languages.indexOf("English")
        }
        languageSpinner.setSelection(selectedLangIndex)

        languageSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val languageCode = when (position) {
                    languages.indexOf("Italiano") -> "it"
                    else -> "en"
                }
                if (languageCode != savedLanguage) {
                    prefs.edit().putString("selected_language", languageCode).apply()
                    changeAppLanguage(requireContext(), languageCode)
                    requireActivity().recreate()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun changeAppLanguage(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // Sfondo semi-trasparente nero (overlay)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}