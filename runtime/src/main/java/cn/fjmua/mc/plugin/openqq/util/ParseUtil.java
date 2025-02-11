package cn.fjmua.mc.plugin.openqq.util;

import com.google.gson.Gson;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class ParseUtil {

    public static <T> T toObject(Map<?, ?> map, Class<T> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(map), clazz);
    }

}
