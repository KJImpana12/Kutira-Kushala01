package com.kutirakushala.activities;

import android.content.Context;
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

import com.kutirakushala.R;
import com.kutirakushala.databinding.ActivityAddBusinessBinding;
import com.kutirakushala.models.Business;
import com.kutirakushala.utils.FirebaseHelper;
import com.kutirakushala.utils.LocaleHelper;

public class AddBusinessActivity extends AppCompatActivity {

    private ActivityAddBusinessBinding binding;
    private Uri profileImageUri = null;
    private Uri workshopImageUri = null;
    private boolean isUploadingProfile = false;

    // English keys for Firebase — never translated
    private final String[] categoryKeys = {
            "Food", "Craft", "Textile", "Agriculture", "Other"
    };

    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    if (isUploadingProfile) {
                        profileImageUri = uri;
                        binding.ivProfilePreview.setImageURI(uri);
                        binding.ivProfilePreview.setVisibility(View.VISIBLE);
                    } else {
                        workshopImageUri = uri;
                        binding.ivWorkshopPreview.setImageURI(uri);
                        binding.ivWorkshopPreview.setVisibility(View.VISIBLE);
                    }
                }
            });

    // ✅ CORRECT position — direct method of the Activity class
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBusinessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupCategoryDropdown();
        setupClickListeners();
    }

    private void setupCategoryDropdown() {
        // Translated labels shown to user
        String[] categoryLabels = {
                getString(R.string.category_food),
                getString(R.string.category_craft),
                getString(R.string.category_textile),
                getString(R.string.category_agriculture),
                getString(R.string.category_other)
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, categoryLabels);
        binding.spinnerCategory.setAdapter(adapter);
    }

    private void setupClickListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnPickProfile.setOnClickListener(v -> {
            isUploadingProfile = true;
            openImagePicker();
        });

        binding.btnPickWorkshop.setOnClickListener(v -> {
            isUploadingProfile = false;
            openImagePicker();
        });

        binding.btnSaveBusiness.setOnClickListener(v -> validateAndSave());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void validateAndSave() {
        String ownerName    = binding.etOwnerName.getText().toString().trim();
        String bizName      = binding.etBusinessName.getText().toString().trim();
        String location     = binding.etLocation.getText().toString().trim();
        String phone        = binding.etPhone.getText().toString().trim();
        String description  = binding.etDescription.getText().toString().trim();
        String weeklyCapStr = binding.etWeeklyCapacity.getText().toString().trim();

        // English key saved to Firebase, not the translated label
        int selectedIndex = binding.spinnerCategory.getSelectedItemPosition();
        String category = categoryKeys[selectedIndex];

        if (ownerName.isEmpty() || bizName.isEmpty() || location.isEmpty()
                || phone.isEmpty() || weeklyCapStr.isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.error_fill_required_fields),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int weeklyCap = Integer.parseInt(weeklyCapStr);

        Business business = new Business(ownerName, bizName, category, location, phone, description);
        business.setWeeklyCapacity(weeklyCap);
        business.setAvailableUnits(weeklyCap);
        business.setAcceptingOrders(true);

        binding.btnSaveBusiness.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);

        if (profileImageUri != null) {
            FirebaseHelper.getInstance().uploadImage(profileImageUri, "profiles",
                    new FirebaseHelper.OnCompleteListener<String>() {
                        @Override
                        public void onSuccess(String url) {
                            business.setProfileImageUrl(url);
                            uploadWorkshopOrSave(business);
                        }
                        @Override
                        public void onFailure(String error) {
                            uploadWorkshopOrSave(business);
                        }
                    });
        } else {
            uploadWorkshopOrSave(business);
        }
    }

    private void uploadWorkshopOrSave(Business business) {
        if (workshopImageUri != null) {
            FirebaseHelper.getInstance().uploadImage(workshopImageUri, "workshops",
                    new FirebaseHelper.OnCompleteListener<String>() {
                        @Override
                        public void onSuccess(String url) {
                            business.setWorkshopImageUrl(url);
                            saveBusiness(business);
                        }
                        @Override
                        public void onFailure(String error) {
                            saveBusiness(business);
                        }
                    });
        } else {
            saveBusiness(business);
        }
    }

    private void saveBusiness(Business business) {
        FirebaseHelper.getInstance().addBusiness(business,
                new FirebaseHelper.OnCompleteListener<String>() {
                    @Override
                    public void onSuccess(String id) {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddBusinessActivity.this,
                                getString(R.string.success_business_registered),
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                    @Override
                    public void onFailure(String error) {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.btnSaveBusiness.setEnabled(true);
                        Toast.makeText(AddBusinessActivity.this,
                                getString(R.string.error_failed_to_save, error),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}