package com.odoo.data;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.odoo.base.addons.abirex.model.PurchaseOrder;

public class PurchaseOrderProxy extends CursorItemProxy {

    private PurchaseOrder purchaseOrder;

    public PurchaseOrderProxy(@NonNull Cursor cursor, int index) {
        super(cursor, index);
        purchaseOrder = new PurchaseOrder();
    }

    public String getName() {
        if (purchaseOrder.getName() == null) {
            Cursor cursor = getCursor();
            cursor.moveToPosition(getIndex());
            int columnIndex = cursor.getColumnIndex("name");
            purchaseOrder.setName(cursor.getString(columnIndex));
        }

        return purchaseOrder.getName();
    }

    // other getter
}