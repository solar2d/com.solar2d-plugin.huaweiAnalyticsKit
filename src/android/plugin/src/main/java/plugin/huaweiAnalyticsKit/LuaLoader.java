//
//  LuaLoader.java
//  TemplateApp
//
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

// This corresponds to the name of the Lua library,
// e.g. [Lua] require "plugin.library"
package plugin.huaweiAnalyticsKit;

import android.os.Bundle;
import android.util.Log;

import com.ansca.corona.CoronaEnvironment;
import com.ansca.corona.CoronaLua;
import com.ansca.corona.CoronaRuntime;
import com.ansca.corona.CoronaRuntimeListener;
import com.ansca.corona.CoronaRuntimeTask;
import com.ansca.corona.CoronaRuntimeTaskDispatcher;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.analytics.HiAnalytics;
import com.huawei.hms.analytics.HiAnalyticsInstance;
import com.huawei.hms.analytics.HiAnalyticsTools;
import com.huawei.hms.analytics.type.HAEventType;
import com.huawei.hms.analytics.type.HAParamType;
import com.huawei.hms.common.ApiException;
import com.naef.jnlua.JavaFunction;
import com.naef.jnlua.LuaState;
import com.naef.jnlua.LuaType;
import com.naef.jnlua.NamedJavaFunction;

import static plugin.huaweiAnalyticsKit.Utils.mapToJsonArray;

@SuppressWarnings("WeakerAccess")
public class LuaLoader implements JavaFunction, CoronaRuntimeListener {
    private int fListener;

    private static final String EVENT_NAME = "Huawei Analytics";
    public static HiAnalyticsInstance instance;

    public static CoronaRuntimeTaskDispatcher fDispatcher = null;

    private void initHiAnalyticsInstance() {
        if (instance == null) {
            CoronaEnvironment.getCoronaActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        HiAnalyticsTools.enableLog();
                        instance = HiAnalytics.getInstance(CoronaEnvironment.getCoronaActivity());
                        Log.i(Constants.TAG, "initHiAnalyticsInstance");
                    } catch (Exception e) {
                        Log.i(Constants.TAG, "initHiAnalyticsInstance Error => " + e.getMessage());
                    }
                }
            });
        }
    }

    @SuppressWarnings("unused")
    public LuaLoader() {
        fListener = CoronaLua.REFNIL;
        CoronaEnvironment.addRuntimeListener(this);
    }

    @Override
    public int invoke(LuaState L) {
        NamedJavaFunction[] luaFunctions = new NamedJavaFunction[]{
                new init(),
                new hiAnalyticsInstance(),
                new hiAnalyticsTools(),
                new crashService()
        };
        String libName = L.toString(1);
        L.register(libName, luaFunctions);

        return 1;
    }

    private class init implements NamedJavaFunction {

        @Override
        public String getName() {
            return "init";
        }

        @Override
        public int invoke(LuaState L) {
            int listenerIndex = 1;

            if (CoronaLua.isListener(L, listenerIndex, EVENT_NAME)) {
                fListener = CoronaLua.newRef(L, listenerIndex);
            }

            AGConnectServicesConfig config = AGConnectServicesConfig.fromContext(CoronaEnvironment.getApplicationContext());
            config.overlayWith(new HmsLazyInputStream(CoronaEnvironment.getApplicationContext()).get(CoronaEnvironment.getApplicationContext()));

            fDispatcher = new CoronaRuntimeTaskDispatcher(L);
            return 0;
        }
    }

    private class hiAnalyticsInstance implements NamedJavaFunction {

        @Override
        public String getName() {
            return Constants.HiAnalyticsInstance;
        }

        @Override
        public int invoke(LuaState L) {
            switch (L.toString(1)) {
                case Constants.setAnalyticsEnabled:
                    return setAnalyticsEnabled(L);
                case Constants.setUserId:
                    return setUserId(L);
                case Constants.setUserProfile:
                    return setUserProfile(L);
                case Constants.setPushToken:
                    return setPushToken(L);
                case Constants.setMinActivitySessions:
                    return setMinActivitySessions(L);
                case Constants.setSessionDuration:
                    return setSessionDuration(L);
                case Constants.onEvent:
                    return onEvent(L);
                case Constants.clearCachedData:
                    return clearCachedData(L);
                case Constants.getAAID:
                    return getAAID(L);
                case Constants.getUserProfiles:
                    return getUserProfiles(L);
                case Constants.pageStart:
                    return pageStart(L);
                case Constants.pageEnd:
                    return pageEnd(L);
                case Constants.setReportPolicies:
                    return setReportPolicies(L);
                case Constants.setRestrictionEnabled:
                    return setRestrictionEnabled(L);
                case Constants.isRestrictionEnabled:
                    return isRestrictionEnabled(L);
                default:
                    return 0;
            }
        }
    }

    private class hiAnalyticsTools implements NamedJavaFunction {

        @Override
        public String getName() {
            return Constants.HiAnalyticsTools;
        }

        @Override
        public int invoke(LuaState L) {
            if (L.toString(1).equals(Constants.enableLog)) {
                return enableLog(L);
            } else {
                return 0;
            }
        }
    }

    private static class crashService implements NamedJavaFunction {

        @Override
        public String getName() {
            return Constants.CrashService;
        }

        @Override
        public int invoke(LuaState L) {
            switch (L.toString(1)) {
                case Constants.enableCrashCollection:
                    return AGConnectCrashWrapper.enableCrashCollection(L);
                case Constants.testIt:
                    return AGConnectCrashWrapper.testIt(L);
                case Constants.setUserId:
                    return AGConnectCrashWrapper.setUserId(L);
                case Constants.setCustomKey:
                    return AGConnectCrashWrapper.setCustomKey(L);
                case Constants.log:
                    return AGConnectCrashWrapper.log(L);
                default:
                    return 0;
            }
        }
    }

    private int setAnalyticsEnabled(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        initHiAnalyticsInstance();

        if (L.type(2) == LuaType.TABLE || L.tableSize(2) != 0) {
            L.getField(2, "enabled");
            if (L.isBoolean(-1)) {
                instance.setAnalyticsEnabled(L.toBoolean(-1));
            } else {
                Log.e(Constants.TAG, "setAnalyticsEnabled {Boolean} expected");
            }
        } else {
            Log.e(Constants.TAG, "setAnalyticsEnabled {enabled=Boolean} expected");
        }
        return 0;
    }

    private int setUserId(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        initHiAnalyticsInstance();

        if (L.type(2) == LuaType.TABLE || L.tableSize(2) == 0) {
            L.getField(2, "id");
            if (L.isString(-1)) {
                instance.setUserId(L.toString(-1));
            } else {
                Log.e(Constants.TAG, "setUserId {id=String} expected");
            }
        } else {
            Log.e(Constants.TAG, "setUserId {id=String} expected");
        }
        return 0;
    }

    private int setUserProfile(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        initHiAnalyticsInstance();
        String name, value;

        if (L.type(2) == LuaType.TABLE || L.tableSize(2) != 0) {
            L.getField(2, "name");
            if (L.isString(-1)) {
                name = L.toString(-1);
                L.pop(1);
            } else {
                Log.e(Constants.TAG, "setUserProfile {name=String, value=String} expected");
                return 0;
            }

            L.getField(2, "value");
            if (L.isString(-1)) {
                value = L.toString(-1);
                L.pop(1);
            } else {
                Log.e(Constants.TAG, "setUserProfile {name=String, value=String} expected");
                return 0;
            }
        } else {
            Log.e(Constants.TAG, "setUserProfile {name=String, value=String} expected");
            return 0;
        }

        instance.setUserProfile(name, value);
        return 0;
    }

    private int setPushToken(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        initHiAnalyticsInstance();

        if (L.type(2) == LuaType.TABLE || L.tableSize(2) != 0) {
            L.getField(2, "token");
            if (L.isString(-1)) {
                instance.setUserId(L.toString(-1));
            } else {
                Log.e(Constants.TAG, "setPushToken {token=String} expected");
            }
        } else {
            Log.e(Constants.TAG, "setPushToken {token=String} expected");
        }
        return 0;
    }

    private int setMinActivitySessions(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        initHiAnalyticsInstance();

        if (L.type(2) == LuaType.TABLE || L.tableSize(2) != 0) {
            L.getField(2, "milliseconds");
            if (L.isNumber(-1)) {
                instance.setMinActivitySessions(L.toInteger(-1));
            } else {
                Log.e(Constants.TAG, "setMinActivitySessions {milliseconds=Number} expected");
            }
        } else {
            Log.e(Constants.TAG, "setMinActivitySessions {milliseconds=Number} expected");
        }
        return 0;
    }

    private int setSessionDuration(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        initHiAnalyticsInstance();

        if (L.type(2) == LuaType.TABLE || L.tableSize(2) != 0) {
            L.getField(2, "milliseconds");
            if (L.isNumber(-1)) {
                instance.setSessionDuration(L.toInteger(-1));
            } else {
                Log.e(Constants.TAG, "setSessionDuration {milliseconds=Number} expected");
            }
        } else {
            Log.e(Constants.TAG, "setSessionDuration {milliseconds=Number} expected");
        }
        return 0;
    }

    //TODO:
    private int onEvent(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        initHiAnalyticsInstance();

        try {
            Bundle bundle_pre = new Bundle();
            bundle_pre.putString(HAParamType.PRODUCTID, "item_ID");
            bundle_pre.putString(HAParamType.PRODUCTNAME, "name");
            bundle_pre.putString(HAParamType.CATEGORY, "category");
            bundle_pre.putLong(HAParamType.QUANTITY, 100L);
            bundle_pre.putDouble(HAParamType.PRICE, 10.01);
            bundle_pre.putDouble(HAParamType.REVENUE, 10);
            bundle_pre.putString(HAParamType.CURRNAME, "currency");
            bundle_pre.putString(HAParamType.PLACEID, "location_ID");
            instance.onEvent(HAEventType.ADDPRODUCT2WISHLIST, bundle_pre);

        } catch (Exception e) {
            Log.i(Constants.TAG, "" + e.getMessage());
        }

        return 0;
    }

    private int clearCachedData(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        initHiAnalyticsInstance();

        try {
            instance.clearCachedData();
        } catch (Exception e) {
            Log.e(Constants.TAG, "clearCachedData error => " + e.getMessage());
        }
        return 0;
    }

    private int getAAID(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        initHiAnalyticsInstance();

        int listenerIndex = 2;
        final int listener = CoronaLua.isListener(L, listenerIndex, Constants.eventName) ? CoronaLua.newRef(L, listenerIndex) : CoronaLua.REFNIL;

        instance.getAAID().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String data) {
                sendDispatcher(listener, false, data, Constants.getAAID, Constants.HiAnalyticsInstance);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                if (e instanceof ApiException) {
                    String result = "rtnCode:" + ((ApiException) e).getStatusCode();
                    sendDispatcher(listener, true, result, Constants.getAAID, Constants.HiAnalyticsInstance);
                }
            }
        });

        return 0;
    }

    private int getUserProfiles(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        initHiAnalyticsInstance();

        if (L.type(2) == LuaType.TABLE || L.tableSize(2) != 0) {
            L.getField(2, "preDefined");
            if (L.isBoolean(-1)) {
                try {
                    L.pushString(mapToJsonArray(instance.getUserProfiles(L.toBoolean(-1))).toString());
                    return 1;
                } catch (Exception e) {
                    Log.e(Constants.TAG, "getUserProfiles Error => " + e.getMessage());
                }
            } else {
                Log.e(Constants.TAG, "getUserProfiles {preDefined=Boolean} expected");
            }
        } else {
            Log.e(Constants.TAG, "getUserProfiles {preDefined=Boolean} expected");
        }
        return 0;
    }

    private int pageStart(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        initHiAnalyticsInstance();

        String pageName, pageClassOverride;

        if (L.type(2) == LuaType.TABLE || L.tableSize(2) != 0) {
            L.getField(2, "pageName");
            if (L.isString(-1)) {
                pageName = L.toString(-1);
                L.pop(1);
            } else {
                Log.e(Constants.TAG, "pageStart {pageName=String, pageClassOverride=String} expected");
                return 0;
            }

            L.getField(2, "pageClassOverride");
            if (L.isString(-1)) {
                pageClassOverride = L.toString(-1);
                L.pop(1);
            } else {
                Log.e(Constants.TAG, "pageStart {pageName=String, pageClassOverride=String} expected");
                return 0;
            }
        } else {
            Log.e(Constants.TAG, "pageStart {pageName=String, pageClassOverride=String} expected");
            return 0;
        }

        try {
            instance.pageStart(pageName, pageClassOverride);
        } catch (Exception e) {
            Log.e(Constants.TAG, "pageStart error =>" + e.getMessage());
        }

        return 0;
    }

    private int pageEnd(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        initHiAnalyticsInstance();

        String pageName;

        if (L.type(2) == LuaType.TABLE || L.tableSize(2) != 0) {
            L.getField(2, "pageName");
            if (L.isString(-1)) {
                pageName = L.toString(-1);
                L.pop(1);
            } else {
                Log.e(Constants.TAG, "pageEnd {pageName=String} expected");
                return 0;
            }
        } else {
            Log.e(Constants.TAG, "pageEnd {pageName=String} expected");
            return 0;
        }

        try {
            instance.pageEnd(pageName);
        } catch (Exception e) {
            Log.e(Constants.TAG, "pageEnd error =>" + e.getMessage());
        }

        return 0;
    }

    //TODO
    private int setReportPolicies(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        initHiAnalyticsInstance();


        return 0;
    }

    private int setRestrictionEnabled(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        initHiAnalyticsInstance();

        if (L.type(2) == LuaType.TABLE || L.tableSize(2) != 0) {
            L.getField(2, "isEnabled");
            if (L.isBoolean(-1)) {
                try {
                    instance.setRestrictionEnabled(L.toBoolean(-1));
                } catch (Exception e) {
                    Log.e(Constants.TAG, "setRestrictionEnabled error =>" + e.getMessage());
                }
                return 0;
            } else {
                Log.e(Constants.TAG, "setRestrictionEnabled {isEnabled=Boolean} expected");
                return 0;
            }
        } else {
            Log.e(Constants.TAG, "setRestrictionEnabled {isEnabled=Boolean} expected");
            return 0;
        }
    }

    private int isRestrictionEnabled(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        initHiAnalyticsInstance();

        try {
            L.pushBoolean(instance.isRestrictionEnabled());
            return 1;
        } catch (Exception e) {
            Log.e(Constants.TAG, "isRestrictionEnabled error =>" + e.getMessage());
            return 0;
        }
    }

    private int enableLog(LuaState L) {
        if (CoronaEnvironment.getCoronaActivity() == null) {
            return 0;
        }

        if (L.type(2) == LuaType.TABLE || L.tableSize(2) != 0) {
            L.getField(2, "level");
            if (L.isNumber(-1)) {
                HiAnalyticsTools.enableLog(L.toInteger(-1));
            } else {
                Log.e(Constants.TAG, "enableLog {level=Number} expected");
            }
        } else {
            HiAnalyticsTools.enableLog();
        }
        return 0;
    }

    public void sendDispatcher(final int listener, final boolean isError, final String message, final String type, final String provider) {
        fDispatcher.send(new CoronaRuntimeTask() {
            @Override
            public void executeUsing(CoronaRuntime coronaRuntime) {
                if (listener != CoronaLua.REFNIL) {
                    LuaState L = coronaRuntime.getLuaState();
                    try {
                        CoronaLua.newEvent(L, Constants.TAG);

                        L.pushString(message);
                        L.setField(-2, "message");

                        L.pushBoolean(isError);
                        L.setField(-2, "isError");

                        L.pushString(type);
                        L.setField(-2, "type");

                        L.pushString(provider);
                        L.setField(-2, "provider");

                        CoronaLua.dispatchEvent(L, listener, 0);

                    } catch (Exception ex) {
                        Log.i(Constants.TAG, "Corona Error:", ex);
                    } finally {
                        CoronaLua.deleteRef(L, listener);
                    }
                }
            }
        });
    }

    @Override
    public void onLoaded(CoronaRuntime runtime) {
    }

    @Override
    public void onStarted(CoronaRuntime runtime) {
    }

    @Override
    public void onSuspended(CoronaRuntime runtime) {
    }

    @Override
    public void onResumed(CoronaRuntime runtime) {
    }

    @Override
    public void onExiting(CoronaRuntime runtime) {
        CoronaLua.deleteRef(runtime.getLuaState(), fListener);
        fListener = CoronaLua.REFNIL;
    }

}
