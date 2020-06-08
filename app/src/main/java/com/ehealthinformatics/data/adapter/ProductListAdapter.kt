package com.ehealthinformatics.data.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.ehealthinformatics.R
import com.ehealthinformatics.app.listeners.OnItemClickListener
import com.ehealthinformatics.app.viewholder.ProductViewHolder
import com.ehealthinformatics.config.OConstants
import com.ehealthinformatics.core.utils.BitmapUtils
import com.ehealthinformatics.data.dto.Product

class  ProductListAdapter public constructor(val onItemClickListener: OnItemClickListener<Product>) : RecyclerView.Adapter<ProductViewHolder>() {

    private lateinit var productLazyList: List<Product>
    private lateinit var itemView: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_product_small, null)
        this.itemView = itemView
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productLazyList[holder.adapterPosition]
        holder.name.text = product.name
        holder.category.text = product.qtyAvailable.toString()
        holder.uom.text = product.productTemplate.uom?.name
        holder.price.text = OConstants.CURRENCY_SYMBOL + product.price.toString()
        holder.icon.setImageBitmap(product.imageSmall)

        itemView.setOnClickListener {
            onItemClickListener.onItemClick(it, product, position)
        }
        holder.setIsRecyclable(false)

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return if(::productLazyList.isInitialized)  productLazyList.size else 0
    }

     fun changeList(productLazyList: List<Product>) {
        this.productLazyList = productLazyList
        notifyDataSetChanged()
    }



}
