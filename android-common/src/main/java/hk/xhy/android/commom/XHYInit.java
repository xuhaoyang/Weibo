package hk.xhy.android.commom;

import android.content.Context;

import hk.xhy.android.commom.utils.PreferenceUtils;

/**
 * Created by xuhaoyang on 16/9/7.
 */
public class XHYInit {

    public static final boolean DEBUG = true;

    public static void initialize(Context context){
        PreferenceUtils.initialize(context);
    }
}
