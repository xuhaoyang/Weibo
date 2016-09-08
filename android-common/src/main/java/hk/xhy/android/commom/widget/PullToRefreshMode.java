package hk.xhy.android.commom.widget;

/**
 * Created by xuhaoyang on 7/20/16.
 */
public enum PullToRefreshMode {
    /**
     * Disable all Pull-to-Refresh gesture and Refreshing handling
     */
    DISABLED,

    /**
     * Only allow the user to Pull from the start of the Refreshable View to
     * refresh. The start is either the Top or Left, depending on the
     * scrolling direction.
     */
    PULL_FROM_START,

    /**
     * Only allow the user to Pull from the end of the Refreshable View to
     * refresh. The start is either the Bottom or Right, depending on the
     * scrolling direction.
     */
    PULL_FROM_END,

    /**
     * Allow the user to both Pull from the start, from the end to refresh.
     */
    BOTH;
}
