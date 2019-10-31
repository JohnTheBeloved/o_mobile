package com.odoo.base.addons.abirex.dto

import com.odoo.core.orm.OValues
import com.odoo.data.abirex.Columns

class PosOrderLine(var id: Int, var name: String, var company: Company, var product: Product,
                   var notice: String, var unitPrice: Float, var quantity: Float,
                   var subTotalWithoutTax: Float, var subTotalWithTax: Float, var discount: Float,
                   var order: PosOrder? = null
                ) : DTO {
    override fun toOValues(): OValues {
        val oValues = OValues()
        oValues.put(Columns.PosOrderLine.company_id, company.id)
        oValues.put(Columns.PosOrderLine.product_id, product.id)
        oValues.put(Columns.PosOrderLine.name, product.name)
        oValues.put(Columns.PosOrderLine.notice, notice)
        oValues.put(Columns.PosOrderLine.qty, quantity)
        oValues.put(Columns.PosOrderLine.price_unit, unitPrice)
        oValues.put(Columns.PosOrderLine.discount, discount)
        oValues.put(Columns.PosOrderLine.price_subtotal, subTotalWithoutTax)
        oValues.put(Columns.PosOrderLine.price_subtotal_incl, subTotalWithTax)
        oValues.put(Columns.PosOrderLine.price_subtotal_incl, subTotalWithTax)
        oValues.put(Columns.PosOrderLine.order_id, order?.id)
        return oValues
    }

    public fun encode2SMS() : String{
        val FIELD = "|"
        return "" + product.serverId + FIELD +
                unitPrice.toString().removeSuffix(".0") + FIELD +
                quantity.toString().removeSuffix(".0") + FIELD
    }

}