package hk.xhy.android.commom.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import hk.xhy.android.commom.bind.Bind;


/**
 * Created by xuhaoyang on 2/24/16.
 */
public class BaseFragment extends Fragment {

    private Activity mActivity;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bind.inject(this, view);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }

    public Activity getmActivity() {
        return mActivity;
    }
}
