package com.kutirakushala.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kutirakushala.R;
import com.kutirakushala.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> products;

    public ProductAdapter(List<Product> products) {
        this.products = products;
    }

    public void updateData(List<Product> newData) {
        this.products = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_card, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(products.get(position));
    }

    @Override
    public int getItemCount() { return products.size(); }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice, tvMinOrder, tvDailyCapacity;

        ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct       = itemView.findViewById(R.id.ivProduct);
            tvName          = itemView.findViewById(R.id.tvProductName);
            tvPrice         = itemView.findViewById(R.id.tvBulkPrice);
            tvMinOrder      = itemView.findViewById(R.id.tvMinOrder);
            tvDailyCapacity = itemView.findViewById(R.id.tvDailyCapacity);
        }

        void bind(Product product) {
            tvName.setText(product.getName());
            tvPrice.setText("₹" + product.getBulkPrice() + " / " + product.getUnit());
            tvMinOrder.setText("Min order: " + product.getMinimumOrder() + " " + product.getUnit() + "s");
            tvDailyCapacity.setText("Can make " + product.getDailyCapacity() + "/day");

            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(product.getImageUrl())
                        .placeholder(R.drawable.ic_product_placeholder)
                        .centerCrop()
                        .into(ivProduct);
            } else {
                ivProduct.setImageResource(R.drawable.ic_product_placeholder);
            }
        }
    }
}
