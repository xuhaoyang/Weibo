package hk.xhy.android.commom.utils;

import android.content.Context;
import android.nfc.Tag;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import hk.xhy.android.commom.widget.Toaster;

/**
 * Created by xuhaoyang on 16/9/8.
 */
public class ErrorUtils {

    private final static String TAG = ErrorUtils.class.getSimpleName();

    /**
     * @param context
     * @param exception
     */
    public static void show(Context context, Exception exception) {
        if (exception == null) {
            return;
        }
        String messages = exception.getLocalizedMessage();
        Logger.show(TAG + ":" + context.getPackageName(), messages);
        show(context, messages);
    }

    /**
     * @param context
     * @param t
     */
    public static void show(Context context, Throwable t) {
        if (t == null) {
            return;
        }
        t.printStackTrace();
        String messages = t.getLocalizedMessage();
        show(context, messages);
    }

    /**
     * NetWork Connect Error
     *
     * @param context
     * @param messages
     */
    public static void show(Context context, String messages) {
        if (context == null) {
            return;
        }
        if (TextUtils.isEmpty(messages)) {
            return;
        }

        Toaster.showShort(context, messages);

    }
}
