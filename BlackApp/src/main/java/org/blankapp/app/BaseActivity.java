/**
 * Copyright (C) 2015 JianyingLi <lijy91@foxmail.com>
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.blankapp.app;

import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import org.blankapp.util.ViewUtils;

import hk.xhy.android.commom.utils.Logger;
import hk.xhy.android.commom.utils.ToastUtils;


public class BaseActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ViewUtils.inject(this);
    }


    public void showLog(String msg) {
        Logger.show(TAG, msg);
    }

    public void showToast(String msg) {
        ToastUtils.showToast(this, msg, Toast.LENGTH_SHORT);
    }

}
