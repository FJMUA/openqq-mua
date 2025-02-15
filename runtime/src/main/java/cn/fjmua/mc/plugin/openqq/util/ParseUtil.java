package cn.fjmua.mc.plugin.openqq.util;

import com.google.gson.Gson;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ParseUtil {

    public static <T> T toObject(Object obj, Class<T> clazz) {
        if (obj == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(obj), clazz);
    }

}
