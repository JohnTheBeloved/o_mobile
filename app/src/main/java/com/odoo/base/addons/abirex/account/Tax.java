package com.odoo.base.addons.abirex.account;

import android.content.Context;

import com.odoo.base.addons.res.ResCompany;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

public class Tax extends OModel {

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn type_tax_use = new OColumn("Tax Scope", OSelection.class);
    OColumn amount_type = new OColumn("Tax Computation", OSelection.class)
                .addSelection("sale", "Sales")
            .addSelection("purchase", "Purchases")
            .addSelection("none", "None")
            .addSelection("adjustment", "Adjustment");
    OColumn active = new OColumn("Active", OBoolean.class);
    OColumn company_id = new OColumn(null, ResCompany.class, OColumn.RelationType.ManyToOne);
    OColumn children_tax_ids = new OColumn(null, Tax.class, OColumn.RelationType.ManyToMany);

    public Tax(Context context, OUser user) {
        super(context, "res.country.state", user);
    }
}
