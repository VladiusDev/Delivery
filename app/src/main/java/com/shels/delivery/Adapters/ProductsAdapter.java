package com.shels.delivery.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.shels.delivery.Data.Product;
import com.shels.delivery.R;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder>{

    private List<Product> products;

    public ProductsAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product, parent, false);

        return new ProductsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        Product product = products.get(position);
        int amount = product.getAmount();
        int scanned = product.getScanned();
        String name = product.getName();
        String characteristic = product.getCharacteristic();

        holder.amount.setText(Integer.toString(amount));
        holder.productName.setText(name);
        holder.productCharacteristic.setText(characteristic);
        holder.scanned.setText(Integer.toString(scanned));

        if (amount == scanned) {
            holder.cardView.setCardBackgroundColor(holder.cardView.getContext().getColor(R.color.product_scanned_ok));
        }else if(scanned > amount) {
            holder.cardView.setCardBackgroundColor(holder.cardView.getContext().getColor(R.color.product_scanned_many));
        }else{
            holder.cardView.setCardBackgroundColor(holder.cardView.getContext().getColor(R.color.textColorPrimary));
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder{
        TextView productName;
        TextView productCharacteristic;
        TextView amount;
        TextView scanned;
        CardView cardView;

        public ProductsViewHolder(@NonNull View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.product_name);
            productCharacteristic = itemView.findViewById(R.id.product_characteristic);
            amount = itemView.findViewById(R.id.product_amount);
            scanned = itemView.findViewById(R.id.product_scanned);
            cardView = itemView.findViewById(R.id.product_cardView);
        }
    }

    public void setProducts(List<Product> products) {
        this.products = products;

        notifyDataSetChanged();
    }
}
