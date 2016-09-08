package hk.xhy.android.commom.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

/**
 * Created by xuhaoyang on 2/23/16.
 */

public class AsyncLoader<D> extends AsyncTaskLoader<D> {
    private final String TAG = AsyncLoader.class.getSimpleName();

    private D mData;

    private Exception mError;

    private LoaderCallback<D> mLoaderCallback;

    private boolean ignoreOnce = false;

    public AsyncLoader(Context context, LoaderCallback<D> loaderCallback) {
        super(context);
        this.mLoaderCallback = loaderCallback;
    }

    /**
     * This is where the bulk of our work is done.  This function is
     * called in a background thread and should generate a new set of
     * data to be published by the loader.
     */
    @Override
    public D loadInBackground() {
        Log.e(TAG, ">>>loadInBackground");
        try {
            mError = null;
            return mLoaderCallback.onLoadInBackground();
        } catch (Exception e) {
            mError = e;
        }
        return null;
    }

    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override
    public void deliverResult(D data) {
        Log.e(TAG, ">>>deliverResult");
        if (isReset()) {
            // An async query came in while the loader is stopped.  We don't need the result.
            if (mData != null) {
                mData = null;
            }
        }

        mData = data;

        if (isStarted()) {
            if (hasError()) {
                Log.e(TAG, ">>>onLoadError");
                mLoaderCallback.onLoadError(mError);
            } else {
                // If the Loader is currently started, we can immediately deliver its results.
                Log.e(TAG, ">>>onLoadComplete");
                mLoaderCallback.onLoadComplete(mData);
            }
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        Log.e(TAG, ">>>onStartLoading");
        if (isIgnoreOnce()) {
            return;
        }
        if (mData != null) {
            deliverResult(mData);
        }
        if (takeContentChanged() || mData == null) {
            forceLoad();
            mLoaderCallback.onLoadStart();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        Log.e(TAG, ">>>onStopLoading");
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override
    public void onCanceled(D data) {
        Log.e(TAG, ">>>onCanceled");
        super.onCanceled(data);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override
    protected void onReset() {
        Log.e(TAG, ">>>onReset");
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        mData     = null;
        mError    = null;
    }

    public boolean isIgnoreOnce() {
        return ignoreOnce;
    }

    public void setIgnoreOnce(boolean ignoreOnce) {
        this.ignoreOnce = ignoreOnce;
    }

    public boolean hasError() {
        return mError != null;
    }

    public interface LoaderCallback<D> {

        void onLoadStart();

        D onLoadInBackground() throws Exception;

        void onLoadComplete(D data);

        void onLoadError(Exception e);
    }
}