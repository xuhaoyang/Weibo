package hk.xhy.android.commom.ui;

import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import hk.xhy.android.commom.bind.Bind;
import hk.xhy.android.commom.utils.ActivityUtils;


/**
 * Created by xuhaoyang on 2/24/16.
 */
public class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        Bind.inject(this);
        // 显示返回按钮并隐藏图标
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ActivityUtils.finishActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
