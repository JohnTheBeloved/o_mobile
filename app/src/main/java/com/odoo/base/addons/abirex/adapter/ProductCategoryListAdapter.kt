package com.odoo.base.addons.abirex.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton

import com.odoo.R
import com.odoo.addons.abirex.viewholder.ProductCategoryViewHolder
import com.odoo.base.addons.abirex.model.Product
import com.odoo.data.LazyList

class ProductCategoryListAdapter public constructor(private val contextMenuCallback: ContextMenuCallback
                                                     ) : RecyclerView.Adapter<ProductCategoryViewHolder>() {

    private lateinit var productLazyList: LazyList<Product>
    private var listStyle = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCategoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(if (listStyle) R.layout.layout_customer_product_list_item else R.layout.layout_customer_product_grid_item, null)
        return ProductCategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductCategoryViewHolder, position: Int) {
        val product = productLazyList[position]
        holder.name.text = product.name
        holder.type.text = product.productTemplate.productType
        holder.price.text = "₦" + product.price.toString()
        setOnClickListener(holder.menu, product.id, product.name)


    }

    override fun getItemCount(): Int {
        return productLazyList.size
    }

     fun changeList(productLazyList: LazyList<Product>) {
        this.productLazyList = productLazyList
        notifyDataSetChanged()
    }

     fun changeToGrid(toGrid: Boolean) {
        listStyle = !toGrid
    }

    interface ContextMenuCallback {
        fun onContextMenuClick(view: ImageButton, id: Int, title: String)
    }

    private fun setOnClickListener(view: ImageButton, id: Int, title: String) {
        view.setOnClickListener {
            contextMenuCallback.onContextMenuClick(view, id, title)
        }
    }

}
