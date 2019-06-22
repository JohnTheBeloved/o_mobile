package com.odoo.base.addons.abirex.model

import android.graphics.Bitmap

class Product {
    var id: Int = 0
    var name: String = ""
    var active: Boolean = false
    var image: Bitmap? = null
    var imageSmall: Bitmap? = null
    var price: Float? = 0F
    var quantity: Double? = 0.0
    var defaultCode: String = ""
    var code: String = ""
    var productType: String = ""
}