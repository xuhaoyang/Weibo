package hk.xhy.android.commom;

import hk.xhy.android.commom.utils.Utils;

/**
 * Created by xuhaoyang on 16/9/7.
 */
public class Application  extends android.app.Application{

    @Override
    public void onCreate() {
        super.onCreate();
        XHYInit.initialize(getApplicationContext());
    }
}
