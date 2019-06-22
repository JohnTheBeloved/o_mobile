package com.odoo.base.addons.abirex.purchase;

import android.content.Context;

import com.odoo.base.addons.abirex.account.Tax;
import com.odoo.base.addons.abirex.dao.PurchaseOrderDao;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBlob;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class PurchaseOrderLine extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn sequence = new OColumn("Sequence", OInteger.class);
    OColumn product_qty = new OColumn("Quantity", OFloat.class);
    OColumn product_uom_qty = new OColumn("Total Quantity", OFloat.class);
    OColumn taxes_id = new OColumn(null, Tax.class, OColumn.RelationType.ManyToMany);
    OColumn product_id = new OColumn("Product", OVarchar.class, OColumn.RelationType.ManyToOne);
    OColumn active = new OColumn("Active", OBoolean.class);
    OColumn product_image = new OColumn("Product Image", OBlob.class);
    OColumn product_type = new OColumn("Product Type", OSelection.class);
    OColumn price_unit = new OColumn("Unit Price", OFloat.class);


    OColumn price_subtotal = new OColumn("Subtotal", OFloat.class);
    OColumn price_total = new OColumn("Total", OFloat.class);
    OColumn price_tax = new OColumn("Tax", OFloat.class);

    OColumn order_id = new OColumn("Order Ref", PurchaseOrderDao.class, OColumn.RelationType.ManyToOne);


    OColumn date_order = new OColumn("Order Date", ODateTime.class);

    public PurchaseOrderLine(Context context, OUser user) {
        super(context, "purchase.order.line", user);
    }
}
