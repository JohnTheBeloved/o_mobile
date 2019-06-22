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
 * Created on 8/1/15 12:44 PM
 */
package com.odoo.core.support.list;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SectionIndexer;

import com.odoo.core.orm.ODataRow;
import com.odoo.core.utils.logger.OLog;
import com.odoo.data.LazyList;

import java.util.HashMap;

import odoo.controls.OForm;

public class OLazyListAdapter<P> implements ListAdapter,
        AdapterView.OnItemClickListener, SectionIndexer {
    public static final String TAG = OLazyListAdapter.class.getSimpleName();

    private Integer mLayout = null;
    private LayoutInflater mInflater = null;
    private OnViewCreateListener mOnViewCreateListener = null;
    private HashMap<Integer, OnRowViewClickListener> mViewClickListeners = new HashMap<>();
    private HashMap<String, View> mViewCache = new HashMap<String, View>();
    private Boolean mCacheViews = false;
    private OnViewBindListener mOnViewBindListener = null;
    private BeforeBindUpdateData mBeforeBindUpdateData = null;
    private Context mContext = null;
    private IOnItemClickListener mIOnItemClickListener = null;
    private AbsListView mListView = null;
    private Boolean hasIndexers = false;
    private String mIndexerColumn = null;
    private HashMap<String, Integer> azIndexers = new HashMap<>();
    private String[] sections = new String[0];
    private DataSetObserver observer;
    private LazyList<P> list;

    public OLazyListAdapter(Context context,  int layout, LazyList<P> list) {
        mLayout = layout;
        mInflater = LayoutInflater.from(context);
        mContext = context;
        this.list = list;
    }

    public OLazyListAdapter allowCacheView(Boolean cache) {
        mCacheViews = cache;
        return this;
    }

    public View getCachedView(Integer position) {
        if (mViewCache.size() > position) {
            return mViewCache.get("view_" + position);
        }
        return null;
    }

    public void reset(LazyList<P> lazyList){
        LazyList old = list;
        if (old != null) {
            old.clear();
            old.closeCursor();
            list = null;
        }
        list = lazyList;
    }


    public void bindView(View view, P dataProxy ) {


        if (view instanceof OForm) {
            OForm form = (OForm) view;
            //form.initForm(row);
        }
        if (mOnViewBindListener != null) {
            mOnViewBindListener.onViewBind(view, dataProxy);
        }
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

        this.observer = observer;
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        observer = null;

    }

    @Override
    public int getCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        P proxyModel = (P) getItem(position);
        view = getCachedView(position);
        if (mCacheViews && view != null) {
            return view;
        }
        view = newView(mContext, position, (ViewGroup) view);
        final View mView = view;
        for (final int id : mViewClickListeners.keySet()) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (mView.findViewById(id) != null) {
                        mView.findViewById(id).setOnClickListener(
                                new View.OnClickListener() {

                                    @Override
                                    public void onClick(View v) {
                                        OnRowViewClickListener listener = mViewClickListeners
                                                .get(id);
                                        listener.onRowViewClick(position, v,
                                                mView);
                                    }
                                });
                    } else {
                        OLog.log("View @id/"
                                + mContext.getResources().getResourceEntryName(
                                id) + " not found");
                    }
                }
            }, 100);
        }
        if (view == null) {
            view = newView(mContext, position, viewGroup);
        }
         bindView(view, proxyModel);
        return view;
    }

    @Override
    public int getItemViewType(int position) {
        return 1;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return list != null ? list.isEmpty() : true;
    }

    public View newView(Context context, Integer position, ViewGroup viewGroup) {
        View view;
        if (mCacheViews && getCachedView(position) != null) {
            view = getCachedView(position);
            if (!view.isDirty()) {
                return view;
            }
        }
        if (mOnViewCreateListener != null) {
            view = mOnViewCreateListener.onViewCreated(context, viewGroup, position);
            if (view == null) {
                view = mInflater.inflate(mLayout, viewGroup, false);
            }
        } else
            view = mInflater.inflate(mLayout, viewGroup, false);
        if (mCacheViews) {
            mViewCache.put("view_" + position, view);
        }
        return view;
    }


//    @Override
//    public void notifyDataSetChanged() {
//        super.notifyDataSetChanged();
//        if (hasIndexers && mIndexerColumn != null) {
//            Cursor cr = getCursor();
//            if (cr.getCount() > 0) {
//                int pos = cr.getCount() - 1;
//                if (cr.moveToLast()) {
//                    List<String> keys = new ArrayList<>();
//                    do {
//                        int index = cr.getColumnIndex(mIndexerColumn);
//                        if (index > -1) {
//                            String colValue = cr.getString(index);
//                            azIndexers.put(colValue.substring(0, 1), pos);
//                            keys.add(colValue.substring(0, 1));
//                        }
//                        pos--;
//                    } while (cr.moveToPrevious());
//                    Collections.sort(keys);
//                    sections = keys.toArray(new String[keys.size()]);
//                }
//            }
//        }
//    }

    public View inflate(int resource, ViewGroup viewGroup) {
        return mInflater.inflate(resource, viewGroup, false);
    }

    public int getResource() {
        return mLayout;
    }

    public void setOnRowViewClickListener(int view_id,
                                          OnRowViewClickListener listener) {
        mViewClickListeners.put(view_id, listener);
    }

    public void setOnViewCreateListener(OnViewCreateListener viewCreateListener) {
        mOnViewCreateListener = viewCreateListener;
    }

    public void setOnViewBindListener(OnViewBindListener bindListener) {
        mOnViewBindListener = bindListener;
    }

    public void setBeforeBindUpdateData(BeforeBindUpdateData updater) {
        mBeforeBindUpdateData = updater;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    public interface OnRowViewClickListener {
        public void onRowViewClick(Integer position, View view,
                                   View parent);
    }

    public interface OnViewBindListener<P> {
        public void onViewBind(View view, P data );
    }

    public interface BeforeBindUpdateData {
        public ODataRow updateDataRow(Cursor cr);
    }

    public interface OnViewCreateListener {
        public View onViewCreated(Context context, ViewGroup view,
                                  int position);
    }

    public void handleItemClickListener(AbsListView absListView, IOnItemClickListener listener) {
        mIOnItemClickListener = listener;
        mListView = absListView;
        if (mListView != null && mIOnItemClickListener != null) {
            mListView.setOnItemClickListener(this);
        }
    }

    private Boolean mDoubleClick = false;
    private Integer mDoubleClickItemIndex = -1;

    @Override
    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
        if (mDoubleClick && mDoubleClickItemIndex == position) {
            mDoubleClick = false;
            mIOnItemClickListener.onItemDoubleClick(view, position);
        } else {
            mDoubleClick = true;
            mDoubleClickItemIndex = position;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mDoubleClick) {
                    mDoubleClick = false;
                    mDoubleClickItemIndex = -1;
                    mIOnItemClickListener.onItemClick(view, position);
                }
            }
        }, 500);
    }

    public void setHasSectionIndexers(boolean hasSectionIndexers, String feild) {
        hasIndexers = hasSectionIndexers;
        mIndexerColumn = feild;
    }

    // Section Indexers
    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return azIndexers.get(sections[sectionIndex]);
    }

    @Override
    public int getSectionForPosition(int position) {
        return azIndexers.get(sections[position]);
    }

}
