package com.kutirakushala.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kutirakushala.R;
import com.kutirakushala.models.Business;

import java.util.List;

public class BusinessAdapter extends RecyclerView.Adapter<BusinessAdapter.BusinessViewHolder> {

    public interface OnBusinessClickListener {
        void onBusinessClick(Business business);
    }

    private List<Business> businesses;
    private final OnBusinessClickListener listener;

    public BusinessAdapter(List<Business> businesses, OnBusinessClickListener listener) {
        this.businesses = businesses;
        this.listener = listener;
    }

    public void updateData(List<Business> newData) {
        this.businesses = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BusinessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_business_card, parent, false);
        return new BusinessViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusinessViewHolder holder, int position) {
        holder.bind(businesses.get(position));
    }

    @Override
    public int getItemCount() { return businesses.size(); }

    class BusinessViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView ivProfile;
        TextView tvBusinessName, tvOwnerName, tvCategory, tvLocation,
                 tvAvailableUnits, tvOrderStatus;

        BusinessViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView       = itemView.findViewById(R.id.cardView);
            ivProfile      = itemView.findViewById(R.id.ivProfile);
            tvBusinessName = itemView.findViewById(R.id.tvBusinessName);
            tvOwnerName    = itemView.findViewById(R.id.tvOwnerName);
            tvCategory     = itemView.findViewById(R.id.tvCategory);
            tvLocation     = itemView.findViewById(R.id.tvLocation);
            tvAvailableUnits = itemView.findViewById(R.id.tvAvailableUnits);
            tvOrderStatus  = itemView.findViewById(R.id.tvOrderStatus);
        }

        void bind(Business business) {
            tvBusinessName.setText(business.getBusinessName());
            tvOwnerName.setText(business.getOwnerName());
            tvCategory.setText(business.getCategory());
            tvLocation.setText(business.getLocation());
            tvAvailableUnits.setText(business.getAvailableUnits() + " units available");

            boolean accepting = business.isAcceptingOrders();
            tvOrderStatus.setText(accepting ? "Open" : "Closed");
            tvOrderStatus.setBackgroundResource(accepting
                    ? R.drawable.bg_badge_open
                    : R.drawable.bg_badge_closed);

            if (business.getProfileImageUrl() != null && !business.getProfileImageUrl().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(business.getProfileImageUrl())
                        .placeholder(R.drawable.ic_business_placeholder)
                        .circleCrop()
                        .into(ivProfile);
            } else {
                ivProfile.setImageResource(R.drawable.ic_business_placeholder);
            }

            cardView.setOnClickListener(v -> listener.onBusinessClick(business));
        }
    }
}
