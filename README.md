# Huawei Analytics Kit and Crash Service Solar2d Plugin

This plugin was created based on Huawei Analytics and Crash Service. Please [Analytics](https://developer.huawei.com/consumer/en/hms/huawei-analyticskit/), [Crash Service](https://developer.huawei.com/consumer/en/agconnect/crash/) for detailed information. 

In order to use this plugin, you must first create an account from developer.huawei.com. And after logging in with your account, you must create a project in the huawei console in order to use HMS kits.

## Project Setup

To use the plugin please add following to `build.settings`

```lua
{
    plugins = {
        ["plugin.huaweiAnalyticsKit"] = {
            publisherId = "com.solar2d",
        },
    },
}
```

And then you have to create keystore for your app. And you must generate sha-256 bit fingerprint from this keystore using the command here. You have to define this fingerprint to your project on the huawei console.

And you must add the keystore you created while building your project.
Also you need to give the package-name of the project you created on Huawei Console.
And you need to put `agconnect-services.json` file into `main.lua` directory.

After all the configuration processes, you must define the plugin in main.lua.

```lua
local huaweiAnalytics = require "plugin.huaweiAnalyticsKit"

local function listener(event)
    print(event)
end

huaweiAnalytics.init(listener) -- sets listener and inits plugin
```

We should call all methods through huaweiAnalytics object.

## Methods in the Plugin
# Analytics Kit
## HiAnalyticsInstance
### setAnalyticsEnabled
Sets whether to enable event tracking. If event tracking is disabled, no data is recorded or analyzed.

```lua
huaweiAnalytics.HiAnalyticsInstance("setAnalyticsEnabled", {enabled=true})
```

### setUserId
Sets a user ID. When this method is called, a new session will be generated if the old value of id is not empty and is different from the new value. If you do not want to use id to identify a user (for example, when a user signs out), you must set id to null when calling setUserId.

```lua
huaweiAnalytics.HiAnalyticsInstance("setUserId", {id=""})
```
### setUserProfile
Sets user attributes. The values of user attributes remain unchanged throughout the app lifecycle and during each session. A maximum of 25 user attributes are supported. If the name of an attribute set later is the same as that of an existing attribute, the value of the existing attribute is updated.


```lua
huaweiAnalytics.HiAnalyticsInstance("setUserProfile", {name="", value=""})
```

### setPushToken
Sets the push token. After obtaining a push token through Push Kit, call this method to save the push token so that you can use the audience defined by Analytics Kit to create HCM notification tasks.


```lua
huaweiAnalytics.HiAnalyticsInstance("setPushToken", {token=""})
```


### setMinActivitySessions
Sets the minimum interval for starting a new session. A new session will be generated when an app is switched back to the foreground after it runs in the background for the specified minimum interval. By default, the minimum interval is 30,000 milliseconds (that is, 30 seconds).


```lua
huaweiAnalytics.HiAnalyticsInstance("setAnalyticsEnabled", {milliseconds=123})
```


### setSessionDuration
Sets the session timeout interval. A new session will be generated when an app is running in the foreground but the interval between two adjacent events exceeds the specified timeout interval. By default, the timeout interval is 1,800,000 milliseconds (that is, 30 minutes).


```lua
huaweiAnalytics.HiAnalyticsInstance("setSessionDuration", {milliseconds=123})
```

### onEvent
Records an event.

```lua
huaweiAnalytics.HiAnalyticsInstance("onEvent", {key=value, key=value})
```


### clearCachedData
Clears all collected data cached locally, including cached data that failed to be sent.


```lua
huaweiAnalytics.HiAnalyticsInstance("clearCachedData")
```

### getAAID
Obtains the AAID from AppGallery Connect.

```lua
huaweiAnalytics.HiAnalyticsInstance("getAAID")
```

### getUserProfiles
Obtains the automatically collected or custom user attributes.

```lua
huaweiAnalytics.HiAnalyticsInstance("getUserProfiles", {preDefined=true})
```


### pageStart
Defines a page entry event. This method applies only to non-activity pages because automatic collection is supported for activity pages. If it is called for an activity page, statistics on page entry and exit events will be inaccurate.
After this method is called, the pageEnd method must be called.

```lua
huaweiAnalytics.HiAnalyticsInstance("pageStart", {pageName="", pageClassOverride=""})
```

### pageEnd
Defines a page exit event. This method applies only to non-activity pages because automatic collection is supported for activity pages. If it is called for an activity page, statistics on page entry and exit events will be inaccurate. Before this method is called, the pageStart method must be called.

```lua
huaweiAnalytics.HiAnalyticsInstance("pageEnd", {pageName=""})
```

### setRestrictionEnabled
Sets whether to disable data analysis. The default value is false, which indicates that data analysis is enabled.

```lua
huaweiAnalytics.HiAnalyticsInstance("setRestrictionEnabled", {isEnabled=true})
```


### isRestrictionEnabled
Checks whether data analysis is disabled.

```lua
huaweiAnalytics.HiAnalyticsInstance("isRestrictionEnabled")
```

## HiAnalyticsTools

### enableLog
Enables the log function.
```lua
huaweiAnalytics.HiAnalyticsTools("enableLog")
```
Enables the debug log function and sets the minimum log level.
Log levels:
* Log.DEBUG(3)
* Log.INFO(4)
* Log.WARN(5)
* Log.ERROR(6)

```lua
huaweiAnalytics.HiAnalyticsTools("enableLog", {level=3})
```

# Crash Service

### enableCrashCollection
Enables or disables Crash. This service is enabled by default, indicating that the Crash service collects and reports crash information. If you do not need this service, you can disable it.

```lua
huaweiAnalytics.CrashService("enableCrashCollection", {enabled=true})
```

### testIt
Creates a crash for testing. This method is used only for you to test the crash implementation. Do not use this method in officially released apps.

```lua
huaweiAnalytics.CrashService("testIt")
```
### setUserId
Sets a custom user ID.

```lua
huaweiAnalytics.CrashService("setUserId", {userId=""})
```

### setCustomKey
Sets a custom key-value pair.

```lua
huaweiAnalytics.CrashService("setCustomKey", {key="", value=""})
```
### log
Records a custom log.

```lua
huaweiAnalytics.CrashService("log", {message="", level=""})
```

## References
Analytics Kit [Check](https://developer.huawei.com/consumer/en/hms/huawei-analyticskit/)
Crash Service [Check](https://developer.huawei.com/consumer/en/agconnect/crash/)

## License
MIT
