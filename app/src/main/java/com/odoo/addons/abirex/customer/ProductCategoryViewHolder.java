package com.odoo.addons.abirex.customer;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.odoo.R;

public class ProductCategoryViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
    ImageView image;
    TextView name;
    TextView type;
    TextView ratingNo;
    TextView price;
    RatingBar rating;
    ImageButton menu;

    ProductCategoryViewHolder(View itemView) {
        super(itemView);
        image = (ImageView) itemView.findViewById(R.id.iv_product_image);
        name = (TextView) itemView.findViewById(R.id.tv_product_name);
        type = (TextView) itemView.findViewById(R.id.tv_product_type);
        ratingNo = (TextView) itemView.findViewById(R.id.tv_product_rating_no);
        price = (TextView) itemView.findViewById(R.id.tv_price);
        rating = (RatingBar) itemView.findViewById(R.id.rb_product_rating);
        menu = (ImageButton) itemView.findViewById(R.id.ib_product_menu);
        menu.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Call");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "SMS");
    }
}