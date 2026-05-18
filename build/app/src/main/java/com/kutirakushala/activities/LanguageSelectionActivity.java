package com.kutirakushala.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.kutirakushala.R;
import com.kutirakushala.utils.LocaleHelper;

public class LanguageSelectionActivity extends AppCompatActivity {

    private CardView cardEnglish, cardKannada, cardHindi;
    private String selectedLanguage = "en";

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_selection);

        cardEnglish = findViewById(R.id.cardEnglish);
        cardKannada = findViewById(R.id.cardKannada);
        cardHindi   = findViewById(R.id.cardHindi);
        Button btnContinue = findViewById(R.id.btnContinue);

        // Pre-select saved language
        selectedLanguage = LocaleHelper.getLanguage(this);
        updateSelection();

        cardEnglish.setOnClickListener(v -> { selectedLanguage = "en"; updateSelection(); });
        cardKannada.setOnClickListener(v -> { selectedLanguage = "kn"; updateSelection(); });
        cardHindi.setOnClickListener(v ->   { selectedLanguage = "hi"; updateSelection(); });

        btnContinue.setOnClickListener(v -> {
            // Save and apply locale
            LocaleHelper.setLocale(this, selectedLanguage);

            // Full restart so every Activity re-inflates with new locale
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void updateSelection() {
        int unselected = 0xFFFFFFFF;
        int selected   = 0xFFE07B39;

        cardEnglish.setCardBackgroundColor(unselected);
        cardKannada.setCardBackgroundColor(unselected);
        cardHindi.setCardBackgroundColor(unselected);

        switch (selectedLanguage) {
            case "en": cardEnglish.setCardBackgroundColor(selected); break;
            case "kn": cardKannada.setCardBackgroundColor(selected); break;
            case "hi": cardHindi.setCardBackgroundColor(selected);   break;
        }
    }
}