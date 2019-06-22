package com.odoo.base.addons.abirex.dao;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class ProductTemplateDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn description = new OColumn("Code", OVarchar.class);
    OColumn active = new OColumn("Active", OBoolean.class);

    public ProductTemplateDao(Context context, OUser user) {
        super(context, "product.template", user);
    }
}
