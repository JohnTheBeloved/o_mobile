package com.odoo.data;

import android.content.ContentProviderClient;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContentResolverCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.os.CancellationSignal;
import android.support.v4.os.OperationCanceledException;
import android.util.Log;

import com.odoo.core.orm.OModel;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;

public class DataLoader<D extends CursorItemProxy> extends AsyncTaskLoader<LazyList<D>> implements Loader.OnLoadCompleteListener {

    CancellationSignal mCancellationSignal;

    Uri uri;
    String[] projection;
    String selection;
    String[] selectionArgs;
    String sortOrder;

    private LazyList<D> lazyList;
    private final static String TAG = DataLoader.class.getSimpleName();
    private LazyList.ItemFactory creator;


    public DataLoader(Context context, Uri uri, String[] projection, String selection,
                      String[] selectionArgs, String sortOrder, LazyList.ItemFactory creator) {

        super(context);
        this.uri = uri;
        this.projection = projection;
        this.selection = selection;
        this.selectionArgs = selectionArgs;
        this.sortOrder = sortOrder;
        this.creator = creator;
        Log.i(TAG, "init Asynctask Loader");
    }

    @Override
    public void onLoadComplete(Loader loader, Object data) {

    }

    @Override
    public LazyList<D>  loadInBackground() {
        //loading lazyList here
        try {
            synchronized (this) {
                if (isLoadInBackgroundCanceled()) {
                    throw new OperationCanceledException();
                }
                mCancellationSignal = new CancellationSignal();
             Cursor cursor =  getContext().getContentResolver().query(
                        uri, projection, selection, selectionArgs, sortOrder);
                Log.i(TAG, "load in background");
                lazyList = new LazyList(cursor, creator);
            }
        } catch (Exception e) {
           e.printStackTrace(System.out);
            e.getMessage();
        }
        return lazyList;
    }

    @Override
    public void deliverResult(LazyList<D> lazyList) {
        if (isReset()) {
            // An async query came in while the loader is stopped
            if (lazyList != null) {
                lazyList.closeCursor();
            }
            return;
        }

        LazyList oldData =  this.lazyList;
        this.lazyList = lazyList;

        if (lazyList !=null && isStarted()) {
            super.deliverResult(lazyList);
        }

        if (oldData != null && oldData != lazyList && oldData.cursorIsClosed()) {
            oldData.clear();
            oldData.closeCursor();
        }
    }

    @Override
    public void cancelLoadInBackground() {
        super.cancelLoadInBackground();

        synchronized (this) {
            if (mCancellationSignal != null) {
                mCancellationSignal.cancel();
            }
        }
    }

    /**
     * Starts an asynchronous load of the contacts list data. When the result is ready the callbacks
     * will be called on the UI thread. If a previous load has been completed and is still valid
     * the result may be passed to the callbacks immediately.
     *
     * Must be called from the UI thread
     */
    @Override
    protected void onStartLoading() {
        if (lazyList != null) {
            deliverResult(lazyList);
        }
        if (takeContentChanged() || lazyList == null) {
            forceLoad();
        }
    }


    /**
     * Must be called from the UI thread
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(LazyList<D> lazyList) {
        if (lazyList != null && !lazyList.cursorIsClosed()) {
            lazyList.closeCursor();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        if (lazyList != null && !lazyList.cursorIsClosed()) {
            lazyList.clear();
            lazyList.closeCursor();
        }
        lazyList = null;
    }

    @Override
    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        super.dump(prefix, fd, writer, args);
        writer.print(prefix); writer.print("mUri="); writer.println(uri);
        writer.print(prefix); writer.print("mProjection=");
        writer.println(Arrays.toString(projection));
        writer.print(prefix); writer.print("mSelection="); writer.println(selection);
        writer.print(prefix); writer.print("mSelectionArgs=");
        writer.println(Arrays.toString(selectionArgs));
        writer.print(prefix); writer.print("mSortOrder="); writer.println(sortOrder);
        writer.print(prefix); writer.print("lazyList="); writer.println(lazyList);
        //writer.print(prefix); writer.print("mContentChanged="); writer.println(mContentChanged);
    }




}