package com.odoo.base.addons.abirex.model


import com.odoo.base.addons.abirex.model.PurchaseOrder
import com.odoo.data.LazyList

import java.text.DecimalFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

class PurchaseOrderDate {
    private var simpleDateFormat: SimpleDateFormat? = null
    var purchaseDate: Date? = null
    var noOfVendors: Int = 0
    var totalAmount: Float = 0.toFloat()
    var noOfPurchases: Int = 0

    var purchaseOrderDaos: List<PurchaseOrder>? = null
        private set


    val totalAmountString: String
        get() {
            val formatter: DecimalFormat
            val totalAmount = totalAmount
            if (totalAmount <= 99999)
                formatter = DecimalFormat("###,###,##0.00")
            else
                formatter = DecimalFormat("#,##,##,###.00")

            return "₦ " + formatter.format(totalAmount.toDouble())
        }

    constructor() {
        simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    }

    constructor(purchaseDate: Date, noOfVendors: Int, totalAmount: Float, noOfPurchases: Int) {
        this.purchaseDate = purchaseDate
        this.noOfVendors = noOfVendors
        this.totalAmount = totalAmount
        this.noOfPurchases = noOfPurchases
        simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
    }

    fun setPurchaseOrders(purchaseOrders: LazyList<PurchaseOrder>) {
        this.purchaseOrderDaos = purchaseOrders
    }

    @Throws(ParseException::class)
    fun setPurchaseDate(purchaseDate: String) {
        this.purchaseDate = simpleDateFormat!!.parse(purchaseDate)
    }
}
