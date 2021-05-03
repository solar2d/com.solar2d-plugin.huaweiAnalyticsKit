local Library = require "CoronaLibrary"

local lib = Library:new{ name='plugin.huaweiAnalyticsKit', publisherId='com.solar2d' }

local placeholder = function()
	print( "WARNING: The '" .. lib.name .. "' library is not available on this platform." )
end


lib.init = placeholder
lib.HiAnalyticsInstance = placeholder
lib.HiAnalyticsTools = placeholder
lib.CrashService = placeholder

-- Return an instance
return lib