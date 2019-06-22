package com.odoo.base.addons.abirex.dao;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.Loader;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.odoo.BuildConfig;
import com.odoo.base.addons.abirex.model.PurchaseOrder;
import com.odoo.base.addons.res.ResCompany;
import com.odoo.base.addons.res.ResCurrency;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.base.addons.res.ResUsers;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OFloat;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;
import com.odoo.data.DataLoader;
import com.odoo.data.LazyList;
import com.odoo.data.PurchaseOrderProxy;

import static com.odoo.core.orm.fields.OColumn.RelationType;

public class PurchaseOrderDao extends OModel {

    public static final String TAG = PurchaseOrderDao.class.getSimpleName();
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID +
            ".core.provider.content.sync.purchase_order";

    OColumn name = new OColumn("Name", OVarchar.class).setSize(100).setRequired();
    OColumn origin = new OColumn("Origin", OVarchar.class);
    OColumn partner_ref = new OColumn("Vendor Reference", OVarchar.class);
    OColumn date_order = new OColumn("Order Date", ODateTime.class);
    OColumn date_approve = new OColumn("Date Approved", ODateTime.class);
    OColumn partner_id = new OColumn("Partner Id", ResPartner.class, RelationType.ManyToOne);
    OColumn currency_id = new OColumn("Currency", ResCurrency.class, RelationType.ManyToOne);
    OColumn state = new OColumn("Vendor Reference", OSelection.class)
            .addSelection("draft", "RFQ")
            .addSelection("sent", "RFQ Sent")
            .addSelection("to approve", "To Approve")
            .addSelection("purchase", "PurchaseList Order")
            .addSelection("done", "Locked")
            .addSelection("cancel", "Cancelled");
    OColumn company_id = new OColumn("Company", ResCompany.class, RelationType.ManyToOne);
    OColumn user_id = new OColumn(null, ResUsers.class, RelationType.ManyToOne);

    OColumn amount_untaxed = new OColumn("Untaxed Amount", OFloat.class);
    OColumn amount_tax = new OColumn("Taxes", OFloat.class);
    OColumn amount_total = new OColumn("Total Amount", OFloat.class);

    public PurchaseOrderDao(Context context, OUser user) {
        super(context, "purchase.order", user);
        setHasMailChatter(true);
    }

    @Override
    public Uri uri() {

        return buildURI(AUTHORITY);
    }

    public Loader<LazyList<PurchaseOrderProxy>> selectAllPurchaseOrderProxy(Context context, Uri uri, String[] projection, String selection,
                                                                            String[] selectionArgs, String sortOrder) {
        return new DataLoader(context, uri, null, selection, selectionArgs, sortOrder, getPurchaseOrderCreator());

    }

    private LazyList.ItemFactory getPurchaseOrderCreator(){
       return new LazyList.ItemFactory<PurchaseOrder>() {
            @Override
            public PurchaseOrder create(Cursor cursor, int index) {
                PurchaseOrder user = new PurchaseOrder();
                cursor.moveToPosition(index);
                int columnIndex = cursor.getColumnIndex("name");
                user.setName( cursor.getString(columnIndex));
                // TODO add parsing other data from cursor
                return user;
            }
        };
    }

    @Override
    public void onModelUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @Override
    public void onSyncStarted(){
        Log.e(TAG, "PurchaseOrderDao->onSyncStarted");
    }

    @Override
    public void onSyncFinished(){
        Log.e(TAG, "PurchaseOrderDao->onSyncFinished");
    }
}
