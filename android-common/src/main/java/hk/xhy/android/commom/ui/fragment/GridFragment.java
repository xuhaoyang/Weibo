package hk.xhy.android.commom.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import hk.xhy.android.commom.R;

/**
 * Created by xuhaoyang on 5/26/16.
 */
public abstract class GridFragment<VH extends RecyclerView.ViewHolder, Item, Result> extends RecyclerFragment<VH, Item, Result> {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayoutManager(new GridLayoutManager(getActivity(), getSpanCount()));
    }

    protected int getSpanCount() {
        return 2;
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.grid;
    }

    @Override
    public void onBindViewHolder(final VH holder, final int position) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onListItemClick(getRecyclerView(), holder.itemView, position, getItemId(position));
            }
        });
    }

    protected void onListItemClick(RecyclerView rv, View v, int position, long id) {
    }
}