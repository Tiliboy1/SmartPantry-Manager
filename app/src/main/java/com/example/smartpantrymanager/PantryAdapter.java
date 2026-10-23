package com.example.smartpantrymanager;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that binds PantryItem objects to the tiles of the pantry grid.
 * Each tile shows an emoji, the name, the quantity and a colour-coded expiry chip.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
        void onItemLongClick(PantryItem item);
    }

    private List<PantryItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public PantryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.tvEmoji.setText(Fmt.badgeFor(item.getName()));
        holder.tvName.setText(Fmt.capitalize(item.getName()));
        holder.tvQuantity.setText(Fmt.qty(item.getQuantity()) + " " + item.getUnit());
        bindExpiry(holder.tvExpiry, item.getExpiryDate());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onItemLongClick(item);
            return true;
        });
    }

    /** Green = fresh, orange = expiring within 3 days, red = expired, grey = no date. */
    private void bindExpiry(TextView chip, String expiry) {
        int days = Fmt.daysUntil(expiry);
        int bg, fg;
        String text;
        if (days == Fmt.NO_EXPIRY) {
            text = "No expiry date";
            bg = R.color.chip_plain_bg; fg = R.color.chip_plain_fg;
        } else if (days < 0) {
            text = "Expired";
            bg = R.color.chip_bad_bg; fg = R.color.chip_bad_fg;
        } else if (days == 0) {
            text = "Expires today";
            bg = R.color.chip_warn_bg; fg = R.color.chip_warn_fg;
        } else if (days <= 3) {
            text = "Expires in " + days + (days == 1 ? " day" : " days");
            bg = R.color.chip_warn_bg; fg = R.color.chip_warn_fg;
        } else {
            text = "Fresh \u00B7 " + expiry;
            bg = R.color.chip_ok_bg; fg = R.color.chip_ok_fg;
        }
        chip.setText(text);
        chip.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(chip.getContext(), bg)));
        chip.setTextColor(ContextCompat.getColor(chip.getContext(), fg));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvEmoji, tvName, tvQuantity, tvExpiry;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tvEmoji);
            tvName = itemView.findViewById(R.id.tvName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvExpiry = itemView.findViewById(R.id.tvExpiry);
        }
    }
}
