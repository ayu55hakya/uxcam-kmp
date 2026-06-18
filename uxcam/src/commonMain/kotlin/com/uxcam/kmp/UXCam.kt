package com.uxcam.kmp

/**
 * Common, platform-neutral facade over the native UXCam SDKs.
 *
 * Consumers call these from shared (commonMain) code; the compiler binds each
 * call to the platform `actual` (Android → com.uxcam.UXCam, iOS → UXCam.framework).
 * This replaces Flutter's MethodChannel + string dispatch: no serialization,
 * resolved at compile time.
 *
 * No Context is required to start: the native SDKs capture their own platform
 * context at process startup, so consumers just call `startWithConfiguration`.
 */
expect object UXCam {
    fun startWithConfiguration(config: UXConfig)
    fun stopSessionAndUploadData()
    fun logEvent(eventName: String)

    /** Log an event with properties. A null map exercises the native no-properties path. */
    fun logEvent(eventName: String, properties: Map<String, Any?>?)

    /**
     * Log an event whose properties are supplied as a JSON string. On Android this is
     * parsed into an org.json.JSONObject and forwarded to the native JSONObject overload
     * (which has no cross-platform type, hence a String here). A null string exercises
     * the native null-JSON path; "{}" is an empty object.
     */
    fun logEventWithJson(eventName: String, json: String?)

    fun tagScreenName(screenName: String)
    fun setUserIdentity(userIdentity: String)
    fun setUserProperty(propertyName: String, value: String)
    fun occludeSensitiveScreen(hide: Boolean)
    fun isRecording(): Boolean
}
