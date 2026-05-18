package com.kutirakushala.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.chip.Chip;
import com.kutirakushala.R;
import com.kutirakushala.adapters.BusinessAdapter;
import com.kutirakushala.databinding.ActivityMainBinding;
import com.kutirakushala.models.Business;
import com.kutirakushala.utils.FirebaseHelper;
import com.kutirakushala.utils.LocaleHelper;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BusinessAdapter adapter;
    private List<Business> allBusinesses = new ArrayList<>();
    private String selectedCategory = "All";

    // Colors
    private static final int COLOR_SELECTED_BG     = 0xFFE07B39;
    private static final int COLOR_UNSELECTED_BG   = 0x33FFFFFF;
    private static final int COLOR_SELECTED_TEXT   = 0xFFFFFFFF;
    private static final int COLOR_UNSELECTED_TEXT = 0xFF1A1A2E;

    // -----------------------------------------------------------------------
    // Locale wiring
    // -----------------------------------------------------------------------
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    // -----------------------------------------------------------------------
    // Lifecycle
    // -----------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        setupCategoryChips();
        setupClickListeners();
        loadBusinesses();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBusinesses();
    }

    // -----------------------------------------------------------------------
    // Setup
    // -----------------------------------------------------------------------
    private void setupRecyclerView() {
        adapter = new BusinessAdapter(new ArrayList<>(), business -> {
            Intent intent = new Intent(this, BusinessDetailActivity.class);
            intent.putExtra("businessId", business.getId());
            startActivity(intent);
        });
        binding.rvBusinesses.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvBusinesses.setAdapter(adapter);
    }

    private void setupCategoryChips() {
        int[] labelResIds = {
                R.string.category_all,
                R.string.category_food,
                R.string.category_craft,
                R.string.category_textile,
                R.string.category_agriculture,
                R.string.category_other
        };

        String[] categoryKeys = {
                "All", "Food", "Craft", "Textile", "Agriculture", "Other"
        };

        binding.chipGroupCategory.removeAllViews();

        for (int i = 0; i < categoryKeys.length; i++) {
            final String key   = categoryKeys[i];
            final String label = getString(labelResIds[i]);

            Chip chip = new Chip(this);
            chip.setText(label);
            chip.setCheckable(true);

            boolean isAll = i == 0;
            chip.setChecked(isAll);
            styleChip(chip, isAll);

            chip.setOnCheckedChangeListener((btn, isChecked) -> {
                if (isChecked) {
                    for (int j = 0; j < binding.chipGroupCategory.getChildCount(); j++) {
                        styleChip((Chip) binding.chipGroupCategory.getChildAt(j), false);
                    }
                    styleChip(chip, true);
                    selectedCategory = key;
                    filterBusinesses();
                }
            });

            binding.chipGroupCategory.addView(chip);
        }
    }

    private void styleChip(Chip chip, boolean selected) {
        chip.setChipBackgroundColor(
                android.content.res.ColorStateList.valueOf(
                        selected ? COLOR_SELECTED_BG : COLOR_UNSELECTED_BG));
        chip.setTextColor(selected ? COLOR_SELECTED_TEXT : COLOR_UNSELECTED_TEXT);
        chip.setChipStrokeColor(
                android.content.res.ColorStateList.valueOf(
                        selected ? COLOR_SELECTED_BG : 0xFFCCCCCC));
        chip.setChipStrokeWidth(selected ? 0f : 1f);
        chip.setCheckedIconVisible(false);
    }

    private void setupClickListeners() {
        binding.fabAddBusiness.setOnClickListener(v ->
                startActivity(new Intent(this, AddBusinessActivity.class)));

        binding.btnSearch.setOnClickListener(v ->
                startActivity(new Intent(this, SearchActivity.class)));

        binding.btnChangeLanguage.setOnClickListener(v ->
                startActivity(new Intent(this, LanguageSelectionActivity.class)));
    }

    // -----------------------------------------------------------------------
    // Data
    // -----------------------------------------------------------------------
    private void loadBusinesses() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.tvEmpty.setVisibility(View.GONE);

        FirebaseHelper.getInstance().getAllBusinesses(new FirebaseHelper.OnCompleteListener<List<Business>>() {
            @Override
            public void onSuccess(List<Business> result) {
                allBusinesses = result;
                binding.progressBar.setVisibility(View.GONE);
                filterBusinesses();
            }

            @Override
            public void onFailure(String error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this,
                        getString(R.string.failed_to_load, error),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterBusinesses() {
        List<Business> filtered = new ArrayList<>();
        for (Business b : allBusinesses) {
            if (selectedCategory.equals("All") || selectedCategory.equals(b.getCategory())) {
                filtered.add(b);
            }
        }
        adapter.updateData(filtered);
        binding.tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
        binding.tvBusinessCount.setText(
                getString(R.string.businesses_found, filtered.size()));
    }
}
