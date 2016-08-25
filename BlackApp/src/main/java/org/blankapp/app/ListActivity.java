/**
 * Copyright (C) 2015 JianyingLi <lijy91@foxmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.blankapp.app;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.blankapp.R;

public abstract class ListActivity<VH extends RecyclerView.ViewHolder, Item, Result> extends RecyclerActivity<VH, Item, Result> {

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        setLayoutManager(new LinearLayoutManager(this));
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
