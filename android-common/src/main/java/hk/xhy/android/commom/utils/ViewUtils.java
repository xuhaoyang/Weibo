package hk.xhy.android.commom.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by xuhaoyang on 2017/1/10.
 */

public class ViewUtils {

    private ViewUtils() {
        throw new UnsupportedOperationException("u can't fuck me...");
    }

    public static View inflate(Context context, int layoutId) {
        if (layoutId <= 0) {
            return null;
        }
        return LayoutInflater.from(context).inflate(layoutId, null);
    }

    public static View inflate(ViewGroup parent, int layoutId) {
        if (layoutId <= 0) {
            return null;
        }
        return LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
    }
}
