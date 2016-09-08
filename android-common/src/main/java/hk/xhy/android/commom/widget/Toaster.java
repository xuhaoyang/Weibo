package hk.xhy.android.commom.widget;


import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.text.MessageFormat;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.LENGTH_SHORT;

/**
 * Helper to show {@link Toast} notifications
 */
public class Toaster {


    private static Toast mToast;

    private static void showToast(Context context, String text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    private static void showToast(Context context, final int resId, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, resId, duration);
        } else {
            mToast.setText(resId);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    private static void show(final Activity activity, final int resId,
                             final int duration) {
        if (activity == null)
            return;

        final Context context = activity.getApplication();
        activity.runOnUiThread(new Runnable() {

            public void run() {
                showToast(context, resId, duration);
            }
        });
    }

    private static void show(final Activity activity, final String message,
                             final int duration) {
        if (activity == null)
            return;
        if (TextUtils.isEmpty(message))
            return;

        final Context context = activity.getApplication();
        activity.runOnUiThread(new Runnable() {

            public void run() {
                showToast(context, message, duration);
            }
        });
    }

    private static void show(final Context context, final String message, final int duration) {

        if (TextUtils.isEmpty(message))
            return;
        if (context == null)
            return;

        showToast(context, message, duration);
    }

    private static void show(final Context context, final int resId, final int duration) {


        if (context == null)
            return;

        showToast(context, resId, duration);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
     *
     * @param activity
     * @param resId
     */
    public static void showLong(final Activity activity, int resId) {
        show(activity, resId, LENGTH_LONG);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
     *
     * @param context
     * @param resid
     */
    public static void showLong(final Context context, int resid) {
        show(context, resid, LENGTH_LONG);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
     *
     * @param context
     * @param message
     */
    public static void showLong(final Context context, final String message) {
        show(context, message, LENGTH_LONG);
    }


    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
     *
     * @param activity
     * @param resId
     */
    public static void showShort(final Activity activity, final int resId) {
        show(activity, resId, LENGTH_SHORT);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
     *
     * @param context
     * @param message
     */
    public static void showShort(final Context context, final String message) {
        show(context, message, LENGTH_SHORT);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
     *
     * @param context
     * @param resId
     */
    public static void showShort(final Context context, final int resId) {
        show(context, resId, LENGTH_SHORT);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
     *
     * @param activity
     * @param message
     */
    public static void showLong(final Activity activity, final String message) {
        show(activity, message, LENGTH_LONG);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
     *
     * @param activity
     * @param message
     */
    public static void showShort(final Activity activity, final String message) {
        show(activity, message, LENGTH_SHORT);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
     *
     * @param activity
     * @param message
     * @param args
     */
    public static void showLong(final Activity activity, final String message,
                                final Object... args) {
        String formatted = MessageFormat.format(message, args);
        show(activity, formatted, LENGTH_LONG);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
     *
     * @param activity
     * @param message
     * @param args
     */
    public static void showShort(final Activity activity, final String message,
                                 final Object... args) {
        String formatted = MessageFormat.format(message, args);
        show(activity, formatted, LENGTH_SHORT);
    }

    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_LONG} duration
     *
     * @param activity
     * @param resId
     * @param args
     */
    public static void showLong(final Activity activity, final int resId,
                                final Object... args) {
        if (activity == null)
            return;

        String message = activity.getString(resId);
        showLong(activity, message, args);
    }


    /**
     * Show message in {@link Toast} with {@link Toast#LENGTH_SHORT} duration
     *
     * @param activity
     * @param resId
     * @param args
     */
    public static void showShort(final Activity activity, final int resId,
                                 final Object... args) {
        if (activity == null)
            return;

        String message = activity.getString(resId);
        showShort(activity, message, args);
    }
}