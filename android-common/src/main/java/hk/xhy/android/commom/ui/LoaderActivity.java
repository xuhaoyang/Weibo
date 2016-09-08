package hk.xhy.android.commom.ui;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;

import hk.xhy.android.commom.loader.AsyncLoader;

import java.util.Random;

/**
 * Created by xuhaoyang on 2/24/16.
 */

public abstract class LoaderActivity<D> extends BaseActivity implements LoaderManager.LoaderCallbacks<D>, AsyncLoader.LoaderCallback<D> {
    private final String TAG = LoaderActivity.class.getSimpleName();

    protected final int LOADER_ID = new Random().nextInt();

    //Activity and Fragment just has only one LoaderManager，and there are only to communicate to LoaderManager
    protected LoaderManager mLoaderManager;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.destroyLoader();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLoaderManager != null) {
            AsyncLoader<D> asyncLoader = (AsyncLoader<D>) mLoaderManager.getLoader(LOADER_ID);
            asyncLoader.setIgnoreOnce(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mLoaderManager != null) {
            AsyncLoader<D> asyncLoader = (AsyncLoader<D>) mLoaderManager.getLoader(LOADER_ID);
            asyncLoader.setIgnoreOnce(true);
        }
    }

    protected void ensureLoaderManager() {
        Log.e(TAG, ">>>ensureLoaderManager");
        if (mLoaderManager == null) {
            mLoaderManager = getSupportLoaderManager();
        }
        LoaderManager.enableDebugLogging(true);
    }

    protected void initLoader() {
        Log.e(TAG, String.format(">>>initLoader(%d)", LOADER_ID));
        ensureLoaderManager();
        mLoaderManager.initLoader(LOADER_ID, null, this);
    }

    public void restartLoader() {
        Log.e(TAG, String.format(">>>restartLoader(%d)", LOADER_ID));
        ensureLoaderManager();
        mLoaderManager.restartLoader(LOADER_ID, null, this);
    }

    public void destroyLoader() {
        Log.e(TAG, String.format(">>>destroyLoader(%d)", LOADER_ID));
        ensureLoaderManager();
        mLoaderManager.destroyLoader(LOADER_ID);
    }

    protected void startLoading() {
        Log.e(TAG, String.format(">>>startLoading(%d)", LOADER_ID));
        ensureLoaderManager();
        AsyncLoader<D> asyncLoader = (AsyncLoader<D>) mLoaderManager.getLoader(LOADER_ID);
        asyncLoader.startLoading();
    }

    protected void stopLoading() {
        Log.e(TAG, String.format(">>>stopLoading(%d)", LOADER_ID));
        ensureLoaderManager();
        AsyncLoader<D> asyncLoader = (AsyncLoader<D>) mLoaderManager.getLoader(LOADER_ID);
        asyncLoader.stopLoading();
    }

    protected void forceLoad() {
        Log.e(TAG, String.format(">>>forceLoad(%d)", LOADER_ID));
        ensureLoaderManager();
        AsyncLoader<D> asyncLoader = (AsyncLoader<D>) mLoaderManager.getLoader(LOADER_ID);
        asyncLoader.forceLoad();
    }

    @Override
    public Loader<D> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, ">>>onCreateLoader");
        return new AsyncLoader<D>(this, this);
    }

    @Deprecated
    @Override
    public void onLoadFinished(Loader<D> loader, D data) {
        Log.d(TAG, ">>>onLoadFinished");
    }

    @Deprecated
    @Override
    public void onLoaderReset(Loader<D> loader) {
        Log.d(TAG, ">>>onLoaderReset");
    }

}
