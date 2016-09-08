package hk.xhy.android.commom.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import hk.xhy.android.commom.R;

/**
 * Created by xuhaoyang on 5/26/16.
 */
public abstract class ListFragment<VH extends RecyclerView.ViewHolder, Item, Result> extends RecyclerFragment<VH, Item, Result> {

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected int getRecyclerViewId() {
        return R.id.list;
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
