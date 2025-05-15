package com.example.lostfoundapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class ItemAdapter extends ListAdapter<Item, ItemAdapter.ItemViewHolder> {
    private static final String TAG = "ItemAdapter";
    private OnItemClickListener listener;

    public ItemAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Item> DIFF_CALLBACK = new DiffUtil.ItemCallback<Item>() {
        @Override
        public boolean areItemsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            try {
                // Safely check for equality with null checks
                return Objects.equals(oldItem.getName(), newItem.getName()) &&
                       Objects.equals(oldItem.getDescription(), newItem.getDescription()) &&
                       Objects.equals(oldItem.getDate(), newItem.getDate()) &&
                       Objects.equals(oldItem.getLocation(), newItem.getLocation()) &&
                       Objects.equals(oldItem.getType(), newItem.getType());
            } catch (Exception e) {
                Log.e(TAG, "Error comparing items: " + e.getMessage());
                return false;
            }
        }
    };

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        try {
            Item currentItem = getItem(position);
            if (currentItem != null) {
                // Set name with null check
                holder.textViewName.setText(Objects.requireNonNull(currentItem.getName(), ""));
                
                // Set description with null check
                String description = currentItem.getDescription();
                holder.textViewDescription.setText(description != null ? description : "");
                
                // Set location with null check
                String location = currentItem.getLocation();
                holder.textViewLocation.setText("Location: " + (location != null ? location : ""));
                
                // Set date with null check
                String date = currentItem.getDate();
                holder.textViewDate.setText("Date: " + (date != null ? date : ""));
                
                // Set type with null check
                String type = currentItem.getType();
                holder.textViewType.setText("Type: " + (type != null ? type.toUpperCase() : ""));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error binding item: " + e.getMessage());
        }
    }

    public Item getItemAt(int position) {
        try {
            return getItem(position);
        } catch (Exception e) {
            Log.e(TAG, "Error getting item at position " + position + ": " + e.getMessage());
            return null;
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewName;
        private TextView textViewDescription;
        private TextView textViewLocation;
        private TextView textViewDate;
        private TextView textViewType;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_name);
            textViewDescription = itemView.findViewById(R.id.text_view_description);
            textViewLocation = itemView.findViewById(R.id.text_view_location);
            textViewDate = itemView.findViewById(R.id.text_view_date);
            textViewType = itemView.findViewById(R.id.text_view_type);

            itemView.setOnClickListener(v -> {
                try {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        Item item = getItem(position);
                        if (item != null) {
                            listener.onItemClick(item);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error on item click: " + e.getMessage());
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
} 