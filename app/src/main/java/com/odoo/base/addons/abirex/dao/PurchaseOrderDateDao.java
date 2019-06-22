package com.odoo.base.addons.abirex.dao;

import android.database.Cursor;
import android.support.v4.content.Loader;
import android.util.Log;

import com.odoo.base.addons.abirex.Utils;
import com.odoo.base.addons.abirex.model.PurchaseOrderDate;
import com.odoo.core.orm.fields.OAggregate;
import com.odoo.data.DataLoader;
import com.odoo.data.LazyList;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class PurchaseOrderDateDao {

    public static final String TAG = PurchaseOrderDateDao.class.getSimpleName();

    PurchaseOrderDao purchaseOrderDao;

    public PurchaseOrderDateDao(PurchaseOrderDao purchaseOrderDao){
        this.purchaseOrderDao = purchaseOrderDao;
    }

    OAggregate  group_date = new OAggregate("date_order",  OAggregate.Operation.DATE, "group_date");
    OAggregate  amount_total = new OAggregate("amount_total",  OAggregate.Operation.SUM, "amount_total");
    OAggregate  no_of_purchases = new OAggregate("partner_id",  OAggregate.Operation.COUNT);
    OAggregate  vendor_id_count = new OAggregate("partner_id",  OAggregate.Operation.COUNT_DISTINCT, "vendor_id_count");


    private OAggregate getAggregateColumn(Field field) {
        OAggregate aggregate;
        if (field != null) {
            try {
                field.setAccessible(true);
                aggregate = (OAggregate) field.get(this);
                if (aggregate.getName() == null)
                    aggregate.setName(field.getName());

                    return aggregate;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
            }
        }
        return null;
    }


    public Loader<LazyList<PurchaseOrderDate>> selectAllPurchaseOrderDateProxy(Date dateFrom, Date dateTo) {
        String where = "";
        List<String> args = new ArrayList<>();
        if(dateFrom != null && dateTo != null) {
            where += " date_order BETWEEN  "+group_date.getoColumn()+"(?) and "+ group_date.getoColumn() +"(?)";
            args.add(Utils.getLowerBound(dateFrom));
            args.add(Utils.getUpperBound(dateTo));
        }

        String groupBy = (dateTo == null ? " 0==0) " : ")") + " GROUP BY ( "+ group_date.getAlias() +" ";

        String selection = where == null ? groupBy : where + groupBy;
        String[] selectionArgs = (args.size() > 0) ? args.toArray(new String[args.size()]) : null;

        List<Field> fields = new ArrayList<>();
        fields.addAll(Arrays.asList(getClass().getDeclaredFields()));
        List<String> projection = new ArrayList();

        for (Field field : fields) {
            if (field.getType().isAssignableFrom(OAggregate.class)) {
                OAggregate aggregate = getAggregateColumn(field);
                if(aggregate != null){
                    projection.add(aggregate.getSyntax());
                }
            }
        }

        return new DataLoader(purchaseOrderDao.getContext(), purchaseOrderDao.uri(), projection.toArray(new String[projection.size()]), selection, selectionArgs, null, getPurchaseOrderDateCreator());
    }

    private LazyList.ItemFactory getPurchaseOrderDateCreator(){
        return new LazyList.ItemFactory<PurchaseOrderDate>() {
            @Override
            public PurchaseOrderDate create(Cursor cursor, int index) {
                PurchaseOrderDate purchaseOrderDate = new PurchaseOrderDate();
                List<Field> fields = new ArrayList<>();
                fields.addAll(Arrays.asList(getClass().getDeclaredFields()));
                cursor.moveToPosition(index);
                int groupDateIndex = cursor.getColumnIndex(group_date.getAlias());
                int amountTotalIndex = cursor.getColumnIndex(amount_total.getAlias());
                int noOfPurchaseIndex = cursor.getColumnIndex(no_of_purchases.getAlias());
                int vendorIdsIndex = cursor.getColumnIndex(vendor_id_count.getAlias());
                try {
                    purchaseOrderDate.setPurchaseDate(cursor.getString(groupDateIndex));
                    purchaseOrderDate.setTotalAmount(cursor.getFloat(amountTotalIndex));
                    purchaseOrderDate.setNoOfVendors(cursor.getInt(noOfPurchaseIndex));
                    purchaseOrderDate.setNoOfPurchases(cursor.getInt(vendorIdsIndex));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
                // TODO add parsing other data from cursor
                return purchaseOrderDate;
            }
        };
    }


}
