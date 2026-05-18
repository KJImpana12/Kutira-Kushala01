package com.kutirakushala.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.kutirakushala.R;
import com.kutirakushala.adapters.ProductAdapter;
import com.kutirakushala.databinding.ActivityBusinessDetailBinding;
import com.kutirakushala.models.Business;
import com.kutirakushala.models.Product;
import com.kutirakushala.utils.FirebaseHelper;

import java.util.ArrayList;
import java.util.List;

public class BusinessDetailActivity extends AppCompatActivity {

    private ActivityBusinessDetailBinding binding;
    private Business business;
    private String businessId;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBusinessDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        businessId = getIntent().getStringExtra("businessId");
        if (businessId == null) { finish(); return; }

        setupRecyclerView();
        loadBusiness();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts(); // Refresh products when returning from AddProduct
    }

    private void setupRecyclerView() {
        productAdapter = new ProductAdapter(new ArrayList<>());
        binding.rvProducts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvProducts.setAdapter(productAdapter);
    }

    private void loadBusiness() {
        binding.progressBar.setVisibility(View.VISIBLE);
        FirebaseHelper.getInstance().getBusinessById(businessId, new FirebaseHelper.OnCompleteListener<Business>() {
            @Override
            public void onSuccess(Business result) {
                business = result;
                binding.progressBar.setVisibility(View.GONE);
                populateUI();
                loadProducts();
            }

            @Override
            public void onFailure(String error) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(BusinessDetailActivity.this,
                        "Failed to load business", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUI() {
        if (business == null) return;

        binding.tvBusinessName.setText(business.getBusinessName());
        binding.tvOwnerName.setText("By " + business.getOwnerName());
        binding.tvLocation.setText(business.getLocation());
        binding.tvCategory.setText(business.getCategory());
        binding.tvDescription.setText(business.getDescription());
        binding.tvWeeklyCapacity.setText("Weekly Capacity: " + business.getWeeklyCapacity() + " units");

        // Capacity Meter
        updateCapacityMeterUI();

        // Profile image
        if (business.getProfileImageUrl() != null && !business.getProfileImageUrl().isEmpty()) {
            Glide.with(this)
                    .load(business.getProfileImageUrl())
                    .placeholder(R.drawable.ic_business_placeholder)
                    .circleCrop()
                    .into(binding.ivProfile);
        }

        // Workshop image
        if (business.getWorkshopImageUrl() != null && !business.getWorkshopImageUrl().isEmpty()) {
            binding.ivWorkshop.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(business.getWorkshopImageUrl())
                    .placeholder(R.drawable.ic_workshop_placeholder)
                    .into(binding.ivWorkshop);
        }
    }

    private void updateCapacityMeterUI() {
        if (business == null) return;

        int available = business.getAvailableUnits();
        int total = Math.max(business.getWeeklyCapacity(), 1);
        int percent = (int) ((available / (float) total) * 100);
        percent = Math.min(percent, 100);

        binding.capacityProgressBar.setProgress(percent);
        binding.tvAvailableUnits.setText(available + " units available this week");
        binding.tvCapacityPercent.setText(percent + "% capacity");

        boolean accepting = business.isAcceptingOrders();
        binding.tvOrderStatus.setText(accepting ? "✅ Accepting Orders" : "⏸ Not Accepting Orders");
        binding.tvOrderStatus.setBackgroundResource(accepting
                ? R.drawable.bg_status_accepting
                : R.drawable.bg_status_not_accepting);
    }

    private void loadProducts() {
        FirebaseHelper.getInstance().getProductsForBusiness(businessId,
                new FirebaseHelper.OnCompleteListener<List<Product>>() {
                    @Override
                    public void onSuccess(List<Product> result) {
                        productAdapter.updateData(result);
                        binding.tvNoProducts.setVisibility(result.isEmpty() ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(BusinessDetailActivity.this,
                                "Failed to load products", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setupClickListeners() {
        // Back
        binding.btnBack.setOnClickListener(v -> finish());

        // Direct Connect — call the entrepreneur
        binding.btnDirectConnect.setOnClickListener(v -> {
            if (business != null && business.getPhone() != null) {
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + business.getPhone()));
                startActivity(intent);
            }
        });

        // Capacity Meter update dialog
        binding.btnUpdateCapacity.setOnClickListener(v -> showCapacityUpdateDialog());

        // Add product
        binding.fabAddProduct.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddProductActivity.class);
            intent.putExtra("businessId", businessId);
            startActivity(intent);
        });
    }

    private void showCapacityUpdateDialog() {
        View dialogView = LayoutInflater.from(this)
                .inflate(R.layout.dialog_update_capacity, null);

        EditText etUnits = dialogView.findViewById(R.id.etAvailableUnits);
        Switch switchAccepting = dialogView.findViewById(R.id.switchAcceptingOrders);

        if (business != null) {
            etUnits.setText(String.valueOf(business.getAvailableUnits()));
            switchAccepting.setChecked(business.isAcceptingOrders());
        }

        new AlertDialog.Builder(this)
                .setTitle("Update Capacity Meter")
                .setView(dialogView)
                .setPositiveButton("Update", (dialog, which) -> {
                    String unitsStr = etUnits.getText().toString().trim();
                    if (unitsStr.isEmpty()) {
                        Toast.makeText(this, "Please enter available units", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int units = Integer.parseInt(unitsStr);
                    boolean accepting = switchAccepting.isChecked();

                    FirebaseHelper.getInstance().updateCapacityMeter(
                            businessId, units, accepting,
                            new FirebaseHelper.OnCompleteListener<Void>() {
                                @Override
                                public void onSuccess(Void result) {
                                    business.setAvailableUnits(units);
                                    business.setAcceptingOrders(accepting);
                                    updateCapacityMeterUI();
                                    Toast.makeText(BusinessDetailActivity.this,
                                            "Capacity updated!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(String error) {
                                    Toast.makeText(BusinessDetailActivity.this,
                                            "Update failed: " + error, Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
