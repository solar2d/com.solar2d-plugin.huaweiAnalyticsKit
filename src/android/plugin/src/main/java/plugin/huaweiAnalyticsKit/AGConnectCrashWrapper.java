package plugin.huaweiAnalyticsKit;

import android.util.Log;

import com.ansca.corona.CoronaEnvironment;
import com.huawei.agconnect.crash.AGConnectCrash;
import com.naef.jnlua.LuaState;
import com.naef.jnlua.LuaType;

class AGConnectCrashWrapper {

    static int enableCrashCollection(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        if (L.type(2) == LuaType.TABLE || L.tableSize(2) != 0) {
            L.getField(2, "enable");
            if (L.isBoolean(-1)) {
                AGConnectCrash.getInstance().enableCrashCollection(L.toBoolean(-1));
            } else {
                Log.e(Constants.TAG, "enableCrashCollection {enable=Boolean} expected");
            }
        } else {
            Log.e(Constants.TAG, "enableCrashCollection {enable=Boolean} expected");
        }
        return 0;
    }

    static int testIt(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        AGConnectCrash.getInstance().testIt(CoronaEnvironment.getApplicationContext());
        return 0;
    }

    static int setUserId(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        if (L.type(2) == LuaType.TABLE || L.tableSize(2) != 0) {
            L.getField(2, "userId");
            if (L.isString(-1)) {
                AGConnectCrash.getInstance().setUserId(L.toString(-1));
            } else {
                Log.e(Constants.TAG, "setUserId {userId=String} expected");
            }
        } else {
            Log.e(Constants.TAG, "setUserId {userId=String} expected");
        }
        return 0;
    }

    static int setCustomKey(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        String key;
        if (L.type(2) == LuaType.TABLE || L.tableSize(2) != 0) {
            L.getField(2, "key");
            if (L.isString(-1)) {
                key = L.toString(-1);
                L.pop(1);
            } else {
                Log.e(Constants.TAG, "setCustomKey {key=String, value={String, Boolean, Double, Number}} expected");
                return 0;
            }

            L.getField(2, "value");
            if (!L.isNil(-1)) {
                AGConnectCrash.getInstance().setCustomKey(key, L.toString(-1));
            } else {
                Log.e(Constants.TAG, "setCustomKey {key=String, value={String, Boolean, Double, Number}} expected");
            }
        } else {
            Log.e(Constants.TAG, "setCustomKey {key=String, value={String, Boolean, Double, Number}} expected");
        }
        return 0;

    }

    static int log(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        String message;
        if (L.type(2) == LuaType.TABLE || L.tableSize(2) != 0) {
            L.getField(2, "message");
            if (L.isString(-1)) {
                message = L.toString(-1);
                L.pop(1);
            } else {
                Log.e(Constants.TAG, "setAnalyticsEnabled {Boolean} expected");
                return 0;
            }

            L.getField(2, "level");
            if (L.isNumber(-1)) {
                AGConnectCrash.getInstance().log(L.toInteger(-1), message);
            } else {
                AGConnectCrash.getInstance().log(message);
            }
        } else {
            Log.e(Constants.TAG, "setAnalyticsEnabled {enabled=Boolean} expected");
        }
        return 0;
    }

}
