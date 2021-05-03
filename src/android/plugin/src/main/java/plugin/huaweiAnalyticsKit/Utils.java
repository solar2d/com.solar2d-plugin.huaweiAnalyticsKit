package plugin.huaweiAnalyticsKit;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

class Utils {

    static JSONArray mapToJsonArray(Map<String, String> data) throws JSONException {
        JSONArray array = new JSONArray();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            JSONObject object = new JSONObject();
            object.put(entry.getKey(), entry.getValue());
            array.put(object);
        }
        return array;
    }

}
