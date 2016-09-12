package hk.xhy.android.commom.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Created by xuhaoyang on 16/9/8.
 */
public class GsonUtil {

    private final static Gson gson = new GsonBuilder()
            .serializeNulls()
            .create();


    public static <T> T parseJson(String json, Class<T> clazz) {

        try {
            return gson.fromJson(json, clazz);
        } catch (Exception e) {
            return null;
        }

    }

    public static <T> T parseJson(String json, Type typeOfT) {

        try {
            return gson.fromJson(json, typeOfT);
        } catch (Exception e) {
            return null;
        }

    }

    public static <T> List<T> getBeanList(String jsonData) throws Exception {
        return gson.fromJson(jsonData, new TypeToken<List<T>>() {
        }.getType());
    }

    public static List<Map<String, Object>> getBeanMapList(String jsonData) throws Exception {
        return new Gson().fromJson(jsonData, new TypeToken<List<Map<String, Object>>>() {
        }.getType());
    }

    public static String toJson(Object obj) {
        return obj == null ? null : gson.toJson(obj);
    }


}
