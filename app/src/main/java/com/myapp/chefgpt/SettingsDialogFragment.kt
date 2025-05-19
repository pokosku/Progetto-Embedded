package com.myapp.chefgpt

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.DialogFragment

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
        val darkmodeButton = view.findViewById<SwitchCompat>(R.id.dmodeButton)
        val backgroundLayout = view.findViewById<FrameLayout>(R.id.backgroundLayout)
        val contentLayout = view.findViewById<LinearLayout>(R.id.contentLayout)
        val okButton = view.findViewById<Button>(R.id.okButton)


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
        //TODO : salvare la preferenza
        darkmodeButton.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (isChecked) {
                // se è attivato metti la darkmode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                // sennò segui il tema di default del sistema
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
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

        // Salva la preferenza
//        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
//        prefs.edit().putString("theme", themePref).apply()
//

}