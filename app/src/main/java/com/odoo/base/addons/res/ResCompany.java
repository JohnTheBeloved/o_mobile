/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 *
 * Created on 31/12/14 12:53 PM
 */
package com.odoo.base.addons.res;

import android.content.Context;

import com.odoo.App;
import com.odoo.base.addons.abirex.dto.Company;
import com.odoo.base.addons.abirex.dto.Partner;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;
import com.odoo.data.abirex.Columns;

public class ResCompany extends OModel {
    public static final String TAG = ResCompany.class.getSimpleName();

    OColumn name = new OColumn("Name", OVarchar.class);
    OColumn currency_id = new OColumn("Currency", ResCurrency.class,
            OColumn.RelationType.ManyToOne);
    OColumn partner_id = new OColumn(null, ResPartner.class, OColumn.RelationType.ManyToOne);

    ResPartner partnerDao = null;

    public ResCompany(Context context, OUser user) {
        super(context, "res.company", user);
    }

    @Override
     public void initDaos() {
         partnerDao = App.getDao(ResPartner.class);
     }



    public static ODataRow getCurrency(Context context) {
        ResCompany company = new ResCompany(context, null);
        int row_id = company.selectRowId(company.getUser().getCompanyId());
        return company.browse(row_id).getM2ORecord("currency_id").browse();
    }

    public Company get(int id){
        ODataRow oDataRow =   browse(id);
        return fromRow(oDataRow);
    }

    public Company fromRow(ODataRow row){
        Integer id = row.getInt(Columns.id);
        Integer serverId = row.getInt(Columns.server_id);
        Partner partner = partnerDao.get(row.getInt(Columns.Partner.partner_id));
        String name = row.getString(this.name.getName());
        return new Company(id, serverId, name, partner);
    }

    @Override
    public boolean allowCreateRecordOnServer() {
        return false;
    }

    @Override
    public boolean allowUpdateRecordOnServer() {
        return false;
    }

    @Override
    public boolean allowDeleteRecordInLocal() {
        return false;
    }

    public static int myId(Context context) {
        ResCompany company = new ResCompany(context, null);
        return company.selectRowId(company.getUser().getCompanyId());
    }

    public static int myCurrency(Context context) {
        ResCompany company = new ResCompany(context, null);
        ODataRow row = company.browse(company.selectRowId(company.
                getUser().getCompanyId()));
        return row.getM2ORecord("currency_id").browse().getInt(OColumn.ROW_ID);
    }
}
