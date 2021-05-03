local huaweiAnalytics = require "plugin.huaweiAnalyticsKit"
local widget = require( "widget" )

local function listener( event )
    print( event )
end

huaweiAnalytics.init( listener )

local setAnalyticsEnabled = widget.newButton(
    {
        left = 55,
        top = 135,
        id = "setAnalyticsEnabled",
        label = "setAnalyticsEnabled",
        onPress = function(event)
            huaweiAnalytics.HiAnalyticsInstance("setAnalyticsEnabled", {enabled=true})
        end,
        width = 210,
        height = 30
    }
)
