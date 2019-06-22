package com.odoo.base.addons.abirex.dao;

import android.content.Context;

import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class UoMDao extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn category_id = new OColumn("UoMDao Category", OVarchar.class, OColumn.RelationType.ManyToOne);
    OColumn active = new OColumn("Active", OBoolean.class);

    public UoMDao(Context context, OUser user) {
        super(context, "res.country.state", user);
    }
}
