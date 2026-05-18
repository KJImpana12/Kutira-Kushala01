package com.kutirakushala.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kutirakushala.adapters.BusinessAdapter;
import com.kutirakushala.databinding.ActivitySearchBinding;
import com.kutirakushala.models.Business;
import com.kutirakushala.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private BusinessAdapter adapter;
    private List<Business> allBusinesses = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupRecyclerView();
        setupSearch();
        loadAllBusinesses();

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new BusinessAdapter(new ArrayList<>(), business -> {
            Intent intent = new Intent(this, BusinessDetailActivity.class);
            intent.putExtra("businessId", business.getId());
            startActivity(intent);
        });
        binding.rvResults.setLayoutManager(new LinearLayoutManager(this));
        binding.rvResults.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterResults(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void loadAllBusinesses() {
        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseHelper.getInstance().getAllBusinesses(new FirebaseHelper.OnCompleteListener<List<Business>>() {
            @Override
            public void onSuccess(List<Business> result) {
                allBusinesses = result;
                binding.progressBar.setVisibility(View.GONE);
                adapter.updateData(result);
            }

            @Override
            public void onFailure(String error) {
                binding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void filterResults(String query) {
        List<Business> filtered = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();
        for (Business b : allBusinesses) {
            if (lowerQuery.isEmpty()
                    || b.getBusinessName().toLowerCase().contains(lowerQuery)
                    || b.getCategory().toLowerCase().contains(lowerQuery)
                    || b.getLocation().toLowerCase().contains(lowerQuery)
                    || b.getOwnerName().toLowerCase().contains(lowerQuery)) {
                filtered.add(b);
            }
        }
        adapter.updateData(filtered);
        binding.tvNoResults.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
