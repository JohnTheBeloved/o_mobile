package com.odoo.base.addons.abirex.dto

import com.odoo.core.orm.OValues

class StockPickingType(var id: Int, var name: String) : DTO{
    override fun toOValues(): OValues {
        var oValues = OValues()
        return oValues
    }
}