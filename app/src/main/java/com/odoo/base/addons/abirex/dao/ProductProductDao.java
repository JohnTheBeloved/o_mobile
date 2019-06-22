/**
 * Odoo, Open Source Management Solution
 * Copyright (C) 2012-today Odoo SA (<http:www.odoo.com>)
 * <p/>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version
 * <p/>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details
 * <p/>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http:www.gnu.org/licenses/>
 * <p/>
 * Created on 30/12/14 4:00 PM
 */
package com.odoo.base.addons.abirex.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.Loader;
import android.util.Log;

import com.odoo.BuildConfig;
import com.odoo.base.addons.abirex.Utils;
import com.odoo.base.addons.abirex.model.Product;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OBlob;
import com.odoo.core.orm.fields.types.OBoolean;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;
import com.odoo.data.DataLoader;
import com.odoo.data.LazyList;

import static com.odoo.core.orm.fields.OColumn.*;

public class ProductProductDao extends OModel {

    public static final String TAG = ProductProductDao.class.getSimpleName();
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID +
            ".core.provider.content.sync.product_product";

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100).setRequired();
    OColumn active = new OColumn("Active", OBoolean.class).setDefaultValue(false);
    OColumn image = new OColumn("Image", OBlob.class).setDefaultValue(false);
    OColumn image_small = new OColumn("Avatar", OBlob.class).setDefaultValue(false);
    OColumn lst_price = new OColumn("Sale Price", OFloat.class);
    OColumn qty_available = new OColumn("Quantity", OFloat.class);
    OColumn default_code = new OColumn("Default Code", OVarchar.class);
    OColumn code = new OColumn("Code", OVarchar.class);
    OColumn product_tmpl_id = new OColumn(null, ProductTemplateDao.class, RelationType.ManyToOne)
            .addDomain("active","=",true);



    @Odoo.Domain("[['uom_id', '=', @uom_id]]")
    OColumn uom_id = new OColumn("UOM", UoMDao.class, OColumn.RelationType.ManyToOne);

    public ProductProductDao(Context context, OUser user) {
        super(context, "product.product", user);
        setHasMailChatter(true);
    }


    public Loader<LazyList<Product>> selectAll() {
        return new DataLoader(getContext(), uri(), null, null, null, null, getProductCreator());
    }

    private LazyList.ItemFactory getProductCreator(){
        return new LazyList.ItemFactory<Product>() {
            @Override
            public Product create(Cursor cursor, int index) {
                Product product = new Product();
                cursor.moveToPosition(index);
                int nameCol = cursor.getColumnIndex("name");
                int activeCol = cursor.getColumnIndex("active");
                int imageCol = cursor.getColumnIndex("image");
                int imageSmallCol = cursor.getColumnIndex("image_small");
                int priceCol = cursor.getColumnIndex("lst_price");
                int quantityCol = cursor.getColumnIndex("qty_available");
                int defaultCodeCol = cursor.getColumnIndex("default_code");
                int codeCol = cursor.getColumnIndex("code");
                product.setName(cursor.getString(nameCol));
                product.setActive(Boolean.valueOf(cursor.getString(activeCol)));
                product.setImage(Utils.getBitmapFromString(getContext(), cursor.getString(imageCol)));
                product.setImageSmall(Utils.getBitmapFromString(getContext(), cursor.getString(imageSmallCol)));
                product.setPrice(cursor.getFloat(priceCol));
                product.setQuantity(cursor.getDouble(quantityCol));
                product.setDefaultCode(cursor.getString(defaultCodeCol));
                product.setCode(cursor.getString(codeCol));
                //Not set
                product.setProductType("Analgesic");
                return product;
            }
        };
    }

    @Override
    public Uri uri() {

        return buildURI(AUTHORITY);
    }

    @Override
    public void onModelUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onSyncStarted(){
        Log.e(TAG, "ProductProductDao->onSyncStarted");
    }

    @Override
    public void onSyncFinished(){
        Log.e(TAG, "ProductProductDao->onSyncFinished");
    }
}
