package com.odoo.addons.abirex.purchase;


import android.app.DatePickerDialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.base.addons.abirex.dao.PurchaseOrderDao;
import com.odoo.base.addons.abirex.dao.PurchaseOrderDateDao;
import com.odoo.base.addons.abirex.model.PurchaseOrderDate;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.odoo.core.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.OLazyListAdapter;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OControls;
import com.odoo.data.LazyList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PurchaseList extends BaseFragment implements ISyncStatusObserverListener,
LoaderManager.LoaderCallbacks<LazyList<PurchaseOrderDate>>, SwipeRefreshLayout.OnRefreshListener,
OLazyListAdapter.OnViewBindListener<PurchaseOrderDate>, IOnSearchViewChangeListener, View.OnClickListener,
AdapterView.OnItemClickListener {

    public static final String TAG = PurchaseList.class.getSimpleName();
    public static final String KEY = PurchaseList.class.getSimpleName();
    public static final String EXTRA_KEY_TYPE = "extra_key_type";
    private View mView;
    private String mCurFilter = null;
    private Date mDateFilter = null;
    private OLazyListAdapter<PurchaseOrderDate> mAdapter = null;
    private boolean syncRequested = false;
    private DataSetObserver dataSetObserver;
    final Calendar myCalendar = Calendar.getInstance();
    Button dateFilter;
    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dateFilter.setText(sdf.format(myCalendar.getTime()));
        mDateFilter = myCalendar.getTime();
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setHasSyncStatusObserver(KEY, this, db());
        View view =  inflater.inflate(R.layout.common_listview, container, false);
        dateFilter =
                (Button) getActivity().findViewById(R.id.filter_template);
        dateFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        setTitle("Purchases");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasSwipeRefreshView(view, R.id.swipe_container, this);
         dataSetObserver = new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

            }
        };
        mView = view;
        ListView mProductsList = (ListView) view.findViewById(R.id.listview);
        mAdapter = new OLazyListAdapter(getActivity(),  R.layout.purchase_row_item, null);
        mAdapter.setOnViewBindListener(this);
        mAdapter.setHasSectionIndexers(true, "name");
        mAdapter.registerDataSetObserver(new MyDataSetObserver());
       mProductsList.setFastScrollAlwaysVisible(true);
        mProductsList.setOnItemClickListener(this);
        getLoaderManager().initLoader(0, null, this);

    }


    @Override
    public void onViewBind(View view, PurchaseOrderDate purchaseOrderDate) {

        OControls.setText(view, R.id.no_of_vendors, String.format("%s purchase(s) from %s vendors", purchaseOrderDate.getNoOfPurchases(),  purchaseOrderDate.getNoOfVendors()));
        OControls.setText(view, R.id.tv_purchase_time, getFormattedDate(purchaseOrderDate.getPurchaseDate()));
        OControls.setText(view, R.id.tv_no_of_products, purchaseOrderDate.getNoOfPurchases()+"");
        OControls.setText(view, R.id.tv_total_amount, purchaseOrderDate.getTotalAmountString());
    }

    boolean mDataValid;
    private class MyDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            mDataValid = false;
            notifyDataSetInvalidated();
        }
    }

    private void notifyDataSetChanged(){

    }


    private void notifyDataSetInvalidated(){

    }

    @Override
    public Loader<LazyList<PurchaseOrderDate>> onCreateLoader(int id, Bundle data) {
        PurchaseOrderDao purchaseOrderDao = new PurchaseOrderDao(getContext(), null);
        PurchaseOrderDateDao purchaseOrderDateDao = new PurchaseOrderDateDao(purchaseOrderDao);
        return purchaseOrderDateDao.selectAllPurchaseOrderDateProxy(mDateFilter, mDateFilter);
    }

    @Override
    public void onLoadFinished(Loader<LazyList<PurchaseOrderDate>> loader, final LazyList<PurchaseOrderDate> purchaseOrders) {
         if (purchaseOrders != null && purchaseOrders.size() > 0) {
            Log.d(TAG, "Count is...." + purchaseOrders.size());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setVisible(mView, R.id.swipe_container);
                    OControls.setGone(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.swipe_container, PurchaseList.this);
                    ListView mProductsList = (ListView) mView.findViewById(R.id.listview);
                    mAdapter.reset(purchaseOrders);
                    mProductsList.setAdapter(mAdapter.allowCacheView(true));
                    mProductsList.setVisibility(View.VISIBLE);

                }
            }, 500);
        } else {
            Log.d(TAG, "None Count is...." + purchaseOrders.size());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setGone(mView, R.id.swipe_container);
                    OControls.setVisible(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.data_list_no_item, PurchaseList.this);
                    OControls.setImage(mView, R.id.icon, R.drawable.ic_action_products);
                    OControls.setText(mView, R.id.title, _s(R.string.label_no_product_found));
                    OControls.setText(mView, R.id.subTitle, "");
                }
            }, 500);
            if (db().isEmptyTable() && !syncRequested) {
                syncRequested = true;
                onRefresh();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<LazyList<PurchaseOrderDate>> loader) {

    }

    @Override
    public Class<PurchaseOrderDao> database() {
        return PurchaseOrderDao.class;
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("PurchaseList")
                .setIcon(R.drawable.ic_action_products)
                .setInstance(new PurchaseList()));

        return items;
    }


    @Override
    public void onStatusChange(Boolean refreshing) {
        // Sync Status
        getLoaderManager().restartLoader(0, null, this);
    }


    @Override
    public void onRefresh() {
        if (inNetwork()) {
            mDateFilter = null;
            parent().sync().requestSync(PurchaseOrderDao.AUTHORITY);
            setSwipeRefreshing(true);
        } else {
            hideRefreshingProgress();
            Toast.makeText(getActivity(), _s(R.string.toast_network_required), Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_partners_2, menu);
        setHasSearchView(this, menu, R.id.menu_partner_search_2);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSearchViewTextChange(String newFilter) {
        mCurFilter = newFilter;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public void onSearchViewClose() {
        // nothing to do
    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.fabButton:
//            loadActivity(null);
//            break;
//        }
    }

    private void loadActivity(ODataRow row) {
        Bundle data = new Bundle();
        if (row != null) {
            data = row.getPrimaryBundleData();
        }
        IntentUtils.startActivity(getActivity(), PurchaseDetails.class, data);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
//        loadActivity(row);
    }

    public static String getFormattedDate(Date date){
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        //2nd of march 2015
        int day=cal.get(Calendar.DATE);

        if(!((day>10) && (day<19)))
            switch (day % 10) {
                case 1:
                    return new SimpleDateFormat("E, d'st' 'of' MMMM yyyy").format(date);
                case 2:
                    return new SimpleDateFormat("E, d'nd' 'of' MMMM yyyy").format(date);
                case 3:
                    return new SimpleDateFormat("E, d'rd' 'of' MMMM yyyy").format(date);
                default:
                    return new SimpleDateFormat("E, d'th' 'of' MMMM yyyy").format(date);
            }
        return new SimpleDateFormat("E, d'th' 'of' MMMM yyyy").format(date);
    }

}
