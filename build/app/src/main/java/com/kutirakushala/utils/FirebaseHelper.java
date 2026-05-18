package com.kutirakushala.utils;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kutirakushala.models.Business;
import com.kutirakushala.models.Product;

import java.util.List;
import java.util.UUID;

public class FirebaseHelper {

    private static final String TAG = "FirebaseHelper";
    private static final String BUSINESSES_COLLECTION = "businesses";
    private static final String PRODUCTS_COLLECTION = "products";

    private static FirebaseHelper instance;
    private final FirebaseFirestore db;
    private final FirebaseStorage storage;

    private FirebaseHelper() {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
    }

    public static synchronized FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    // ── Business Operations ─────────────────────────────────────────

    public void addBusiness(Business business, OnCompleteListener<String> listener) {
        db.collection(BUSINESSES_COLLECTION)
                .add(business)
                .addOnSuccessListener(ref -> listener.onSuccess(ref.getId()))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void updateBusiness(Business business, OnCompleteListener<Void> listener) {
        db.collection(BUSINESSES_COLLECTION)
                .document(business.getId())
                .set(business)
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void getAllBusinesses(OnCompleteListener<List<Business>> listener) {
        db.collection(BUSINESSES_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> {
                    List<Business> list = snapshot.toObjects(Business.class);
                    listener.onSuccess(list);
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void getBusinessesByCategory(String category, OnCompleteListener<List<Business>> listener) {
        db.collection(BUSINESSES_COLLECTION)
                .whereEqualTo("category", category)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> listener.onSuccess(snapshot.toObjects(Business.class)))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void getBusinessById(String id, OnCompleteListener<Business> listener) {
        db.collection(BUSINESSES_COLLECTION)
                .document(id)
                .get()
                .addOnSuccessListener(snapshot -> {
                    Business business = snapshot.toObject(Business.class);
                    if (business != null) business.setId(snapshot.getId());
                    listener.onSuccess(business);
                })
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    /** Update only the capacity/order-status fields (Capacity Meter). */
    public void updateCapacityMeter(String businessId, int availableUnits,
                                    boolean isAcceptingOrders,
                                    OnCompleteListener<Void> listener) {
        db.collection(BUSINESSES_COLLECTION)
                .document(businessId)
                .update(
                        "availableUnits", availableUnits,
                        "isAcceptingOrders", isAcceptingOrders
                )
                .addOnSuccessListener(aVoid -> listener.onSuccess(null))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // ── Product Operations ──────────────────────────────────────────

    public void addProduct(Product product, OnCompleteListener<String> listener) {
        db.collection(PRODUCTS_COLLECTION)
                .add(product)
                .addOnSuccessListener(ref -> listener.onSuccess(ref.getId()))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void getProductsForBusiness(String businessId, OnCompleteListener<List<Product>> listener) {
        db.collection(PRODUCTS_COLLECTION)
                .whereEqualTo("businessId", businessId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(snapshot -> listener.onSuccess(snapshot.toObjects(Product.class)))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    public void deleteProduct(String productId, OnCompleteListener<Void> listener) {
        db.collection(PRODUCTS_COLLECTION)
                .document(productId)
                .delete()
                .addOnSuccessListener(v -> listener.onSuccess(null))
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // ── Image Upload ────────────────────────────────────────────────

    public void uploadImage(Uri imageUri, String folder, OnCompleteListener<String> listener) {
        String fileName = folder + "/" + UUID.randomUUID().toString() + ".jpg";
        StorageReference ref = storage.getReference().child(fileName);

        ref.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        ref.getDownloadUrl()
                                .addOnSuccessListener(uri -> listener.onSuccess(uri.toString()))
                                .addOnFailureListener(e -> listener.onFailure(e.getMessage()))
                )
                .addOnFailureListener(e -> listener.onFailure(e.getMessage()));
    }

    // ── Callback Interface ──────────────────────────────────────────

    public interface OnCompleteListener<T> {
        void onSuccess(T result);
        void onFailure(String error);
    }
}
