/**
 * Copyright (C) 2015 Kimson Chuh
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
package hk.xhy.android.commom.bind;

import android.app.Activity;
import android.view.View;

import java.lang.reflect.Field;

/**
 * Created by xuhaoyang on 2/23/16.
 */
public class Bind {
    /**
     * 通过注解的方式初始化View
     *
     * @param activity
     */
    public static int inject(Activity activity) {
        return inject(activity, activity.getWindow().getDecorView());
    }

    /**
     * 通过注解的方式初始化View
     *
     * @param object
     * @param view
     * @return
     */
    public static int inject(Object object, View view) {
        int viewCount = 0;
        // 获取所有成员
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            // 设置成员可访问权限
            field.setAccessible(true);
            try {
                // 忽略不为null的成员
                if (field.get(object) != null) {
                    continue;
                }
                ViewById viewById = field.getAnnotation(ViewById.class);
                if (viewById != null) {
                    // 获取View的Id
                    int viewId = viewById.value();
                    // ViewId为0，通过变量名去获取viewId
                    if (viewId == 0) {
                        String fieldName = field.getName();
                        viewId = view.getResources().getIdentifier(fieldName, "id", view.getContext().getPackageName());
                        // 如果viewId还是为0，则继续执行下一个处理
                        if (viewId == 0) {
                            continue;
                        }
                    }
                    field.set(object, view.findViewById(viewId));
                    viewCount++;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // Log.e(TAG, "Field name : " + field.getName());
                e.printStackTrace();
            }
        }
        return viewCount;
    }


}
