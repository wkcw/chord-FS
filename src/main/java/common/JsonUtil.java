package common;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;

public class JsonUtil {


    public static String serilizable(HashMap<String, String> objMap) {
        Gson gson = new Gson();
        String jsonString = gson.toJson(objMap);

        return jsonString;
    }

    public static HashMap<String, String> deserilizable(String jsonStr) {
        Type type = new TypeToken<HashMap<String, String>>(){}.getType();
        Gson gson = new Gson();

        HashMap<String, String> map = gson.fromJson(jsonStr, type);

        return map;
    }

}
