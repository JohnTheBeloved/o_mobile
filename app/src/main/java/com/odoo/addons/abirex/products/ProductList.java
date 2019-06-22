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
 * Created on 30/12/14 3:28 PM
 */
package com.odoo.addons.abirex.products;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.base.addons.abirex.dao.ProductProductDao;
import com.odoo.base.addons.abirex.dao.UoMDao;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.odoo.core.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OControls;

import java.util.ArrayList;
import java.util.List;

public class ProductList extends BaseFragment implements ISyncStatusObserverListener,
LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener,
OCursorListAdapter.OnViewBindListener, IOnSearchViewChangeListener, View.OnClickListener,
AdapterView.OnItemClickListener {

    public static final String TAG = ProductProductDao.class.getSimpleName();
    public static final String KEY = ProductList.class.getSimpleName();
    public static final String EXTRA_KEY_TYPE = "extra_key_type";
    private View mView;
    private String mCurFilter = null;
    private OCursorListAdapter mAdapter = null;
    private boolean syncRequested = false;

    //Action Views
    Button activeFilterButton;


    //Filter tokens
    private String activeFilter = "";



    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setHasSyncStatusObserver(KEY, this, db());
        return inflater.inflate(R.layout.common_listview, container, false);
    }


    private void setupView(){
        activeFilterButton =
                (Button) getActivity().findViewById(R.id.filter_template);
        activeFilterButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_active, 0, 0,0);
        activeFilterButton.setText(getString(R.string.active));


        activeFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = ((Button)v);
                String text = button.getText().toString();
                String inactive = getString(R.string.inactive);
                String active = getString(R.string.active);
                String all = getString(R.string.all);
                if (text.equals(active)) {
                    text = inactive;
                    activeFilter = inactive;
                    setButtonTint(R.color.colorAccent);
                }else if(text.equals(inactive)){

                        text = all;
                        activeFilter = all;
                        setButtonTint(R.color.colorPrimaryGrey);
                }else if( text.equals(all)){
                        text = active;
                        activeFilter = active;
                        setButtonTint(R.color.colorPrimaryWhite);
                }
                button.setText(text);
                reload();
            }
        });
    }

    private void reload(){
        getLoaderManager().restartLoader(0, null, this);
    }

    private void setButtonTint(int color){
        DrawableCompat.setTint(activeFilterButton.getCompoundDrawables()[0],
                ContextCompat.getColor(getContext(), color));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasSwipeRefreshView(view, R.id.swipe_container, this);
        mView = view;
        ListView mProductsList = (ListView) view.findViewById(R.id.listview);
        mAdapter = new OCursorListAdapter(getActivity(), null, R.layout.product_row_item);
        mAdapter.setOnViewBindListener(this);
        mAdapter.setHasSectionIndexers(true, "name");
        mProductsList.setAdapter(mAdapter);
        mProductsList.setFastScrollAlwaysVisible(true);
        mProductsList.setOnItemClickListener(this);
        setHasFloatingButton(view, R.id.fabButton, mProductsList, this);
        getLoaderManager().initLoader(0, null, this);
        setupView();
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        Bitmap img;
        if (row.getString("image_small").equals("false") || row.getString("image_small").equals("")) {
            //img = BitmapUtils.getBitmap(getContext(), R.drawable.ic_medical_drug_pill);
            img = BitmapUtils.getAlphabetImage(getActivity(), row.getString("name"));
        } else {
            img = BitmapUtils.getBitmapImage(getActivity(), row.getString("image_small"));
        }
        OControls.setImage(view, R.id.list_item_image, img);
        ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.list_item_stock_level);

        String productName = row.getString("name");
        productName = productName.length() > 30 ? productName.substring(0,25) + "..." : productName;
        OControls.setText(view, R.id.list_item_name, productName);
        UoMDao uomModel = new UoMDao(App.getContext(), null);
        String uom = uomModel.browse(row.getInt("uom_id")).getString("name");
        String sellingPrice = "" + (!row.getString("lst_price").equals("false") ? row.getFloat("lst_price") : "0.00");
        Float qty = !row.getString("qty_available").equals("false") ? row.getFloat("qty_available"): 0F;
        OControls.setText(view,R.id.list_item_price, "₦"+sellingPrice);
        OControls.setText(view,R.id.list_item_qty, qty+"");
        Float percentage = (qty*10);
        progressBar.setProgress(percentage.intValue());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        String where = "";
        List<String> args = new ArrayList<>();
        if (mCurFilter != null) {
            where += " name like ? ";
            args.add("%" + mCurFilter + "%");
        }
        if(activeFilter.equals(getString(R.string.active))){
            where += " ( active = 'true' )";
        }
        String selection = where;
        String[] selectionArgs = (args.size() > 0) ? args.toArray(new String[args.size()]) : null;
        return new CursorLoader(getActivity(), db().uri(),
        null, selection, selectionArgs, "id");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
        if (data.getCount() > 0) {
            TextView listTitle = (TextView) mView.findViewById(R.id.list_title);
            listTitle.setText("Displaying " + data.getCount() + " Products");
            Log.d(TAG, "Count is...." + data.getCount());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setVisible(mView, R.id.swipe_container);
                    OControls.setGone(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.swipe_container, ProductList.this);

                }
            }, 500);
        } else {
            Log.d(TAG, "None Count is...." + data.getCount());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setGone(mView, R.id.swipe_container);
                    OControls.setVisible(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.data_list_no_item, ProductList.this);
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
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public Class<ProductProductDao> database() {
        return ProductProductDao.class;
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("ProductList")
                .setIcon(R.drawable.ic_action_products)
                .setInstance(new ProductList()));

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
            parent().sync().requestSync(ProductProductDao.AUTHORITY);
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
        IntentUtils.startActivity(getActivity(), ProductDetails.class, data);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
//        loadActivity(row);
    }
}
