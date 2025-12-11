package com.example.prmylab.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prmylab.R;
import com.example.prmylab.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<Product> products
 = new ArrayList<>();
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position){
        Product product = products.get(position);
        holder.tvTitle.setText(product.getTitle());
        holder.tvPrice.setText(String.format("$%.2f", product.getPrice()));
    }
    @Override
    public int getItemCount(){
        return products.size();
    }
    public void setProducts(List<Product> products){
        this.products = products;
        notifyDataSetChanged();
    }
    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle, tvPrice;
        ImageView ivThumbnail;
        public ProductViewHolder(@NonNull View itemView){
            super (itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
        }
    }
}
