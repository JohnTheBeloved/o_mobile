package com.odoo.data;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class LazyList<T> extends ArrayList<T> {

    private final Cursor mCursor;
    private final ItemFactory<T> mCreator;

    public LazyList(Cursor cursor, ItemFactory<T> creator) {
        mCursor = cursor;
        mCreator = creator;
    }

    @Override
    public T get(int index) {
        int size = super.size();
        if (index < size) {
            // find item in the collection
            T item = super.get(index);
            if (item == null) {
                item = mCreator.create(mCursor, index);
                set(index, item);
            }
            return item;
        } else {
            // we have to grow the collection
            for (int i = size; i < index; i++) {
                add(null);
            }
            // create last object, add and return
            T item = mCreator.create(mCursor, index);
            add(item);
            return item;
        }
    }

    public List getList(){
        return this;
    }

    @Override
    public int size() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public void closeCursor() {
        //mCursor.close();
    }
    public boolean cursorIsClosed() {
        return mCursor.isClosed();
    }

    public interface ItemFactory<T> {
        T create(Cursor cursor, int index);
    }

}