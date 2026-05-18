package com.kutirakushala.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.kutirakushala.databinding.ActivityAddProductBinding;
import com.kutirakushala.models.Product;
import com.kutirakushala.utils.FirebaseHelper;

public class AddProductActivity extends AppCompatActivity {

    private ActivityAddProductBinding binding;
    private String businessId;
    private Uri productImageUri = null;

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    productImageUri = result.getData().getData();
                    binding.ivProductPreview.setImageURI(productImageUri);
                    binding.ivProductPreview.setVisibility(View.VISIBLE);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        businessId = getIntent().getStringExtra("businessId");
        if (businessId == null) { finish(); return; }

        setupUnitDropdown();
        setupClickListeners();
    }

    private void setupUnitDropdown() {
        String[] units = {"piece", "dozen", "kg", "gram", "litre", "packet", "bundle"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, units);
        binding.spinnerUnit.setAdapter(adapter);
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnPickProductImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        binding.btnSaveProduct.setOnClickListener(v -> validateAndSave());
    }

    private void validateAndSave() {
        String name         = binding.etProductName.getText().toString().trim();
        String description  = binding.etProductDescription.getText().toString().trim();
        String priceStr     = binding.etBulkPrice.getText().toString().trim();
        String minOrderStr  = binding.etMinOrder.getText().toString().trim();
        String dailyCapStr  = binding.etDailyCapacity.getText().toString().trim();
        String unit         = binding.spinnerUnit.getSelectedItem().toString();

        if (name.isEmpty() || priceStr.isEmpty() || minOrderStr.isEmpty() || dailyCapStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price    = Double.parseDouble(priceStr);
        int minOrder    = Integer.parseInt(minOrderStr);
        int dailyCap    = Integer.parseInt(dailyCapStr);

        Product product = new Product(businessId, name, description, price, minOrder, unit, dailyCap);

        binding.btnSaveProduct.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        if (productImageUri != null) {
            FirebaseHelper.getInstance().uploadImage(productImageUri, "products",
                    new FirebaseHelper.OnCompleteListener<String>() {
                        @Override
                        public void onSuccess(String url) {
                            product.setImageUrl(url);
                            saveProduct(product);
                        }
                        @Override
                        public void onFailure(String error) {
                            saveProduct(product);
                        }
                    });
        } else {
            saveProduct(product);
        }
    }

    private void saveProduct(Product product) {
        FirebaseHelper.getInstance().addProduct(product, new FirebaseHelper.OnCompleteListener<String>() {
            @Override
            public void onSuccess(String id) {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(AddProductActivity.this,
                        "Product added!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String error) {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnSaveProduct.setEnabled(true);
                Toast.makeText(AddProductActivity.this,
                        "Failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
