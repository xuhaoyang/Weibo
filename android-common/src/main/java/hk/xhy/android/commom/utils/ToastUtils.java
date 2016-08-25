package hk.xhy.android.commom.utils;

import android.content.Context;
import android.widget.Toast;


/**
 * Created by xuhaoyang on 16/5/12.
 */
public class ToastUtils {

    private static Toast mToast;

    public static void showToast(Context context, String text, int duration) {
        if(mToast == null) {
            mToast = Toast.makeText(context, text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }
        mToast.show();
    }
}
