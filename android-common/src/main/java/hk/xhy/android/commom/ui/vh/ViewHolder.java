package hk.xhy.android.commom.ui.vh;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import hk.xhy.android.commom.bind.Bind;

/**
 * Created by xuhaoyang on 16/9/8.
 */
public class ViewHolder extends RecyclerView.ViewHolder {

    public ViewHolder(View itemView) {
        super(itemView);
        Bind.inject(this, itemView);
    }
}
