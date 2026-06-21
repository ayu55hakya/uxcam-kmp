import Foundation
import UIKit
import UXCamKMP

/// Thin Swift-friendly facade over the generated `UXCamKMP` framework (the `:uxcam`
/// Kotlin Multiplatform wrapper).
///
/// It smooths over Kotlin/Native Obj-C interop quirks so the SwiftUI screens stay
/// readable:
///  - the `UXCam.shared` object accessor (Kotlin `object` singleton),
///  - the mangled overload selectors for same-named/different-typed setters
///    (`value:` Bool, `value_:` Float, `value__:` Int32, `value___:` String).
///
/// NOTE: the iOS side of the wrapper is currently a non-throwing **stub** (see
/// `uxcam/src/iosMain/.../UXCam.ios.kt`). Calls log on the Kotlin side and return
/// defaults until the native iOS UXCam SDK is wired in.
enum UX {
    private static var sdk: UXCam { UXCam.shared }

    // MARK: Lifecycle & session
    static func start(appKey: String) {
        sdk.startWithConfiguration(config: UXConfig(
            appKey: appKey,
            enableAutomaticScreenNameTagging: true,
            enableMultiSessionRecord: true,
            enableCrashHandling: true,
            enableIntegrationLogging: true,
            occludeAllTextFields: false
        ))
    }
    static func addVerificationListener(onSuccess: @escaping () -> Void,
                                        onFailure: @escaping (String) -> Void) {
        sdk.addVerificationListener(onSuccess: onSuccess, onFailure: onFailure)
    }
    static func startNewSession() { sdk.startNewSession() }
    static func stopSessionAndUploadData() { sdk.stopSessionAndUploadData() }
    static func stopSessionAndUploadData(onStopped: @escaping () -> Void) {
        sdk.stopSessionAndUploadData(onSessionStopped: onStopped)
    }
    static func cancelCurrentSession() { sdk.cancelCurrentSession() }

    // MARK: Events
    static func logEvent(_ name: String) { sdk.logEvent(eventName: name) }
    static func logEvent(_ name: String, properties: [String: Any]?) {
        sdk.logEvent(eventName: name, properties: properties)
    }
    static func logEventWithJson(_ name: String, json: String?) {
        sdk.logEventWithJson(eventName: name, json: json)
    }

    // MARK: Bug & exception reporting
    static func reportBugEvent(_ name: String) { sdk.reportBugEvent(eventName: name) }
    static func reportBugEvent(_ name: String, properties: [String: Any]?) {
        sdk.reportBugEvent(eventName: name, properties: properties)
    }
    static func reportBugEventWithJson(_ name: String, json: String?) {
        sdk.reportBugEventWithJson(eventName: name, json: json)
    }
    static func reportException(_ message: String, properties: [String: Any]? = nil) {
        let throwable = KotlinThrowable(message: message)
        if let properties {
            sdk.reportExceptionEvent(throwable: throwable, properties: properties)
        } else {
            sdk.reportExceptionEvent(throwable: throwable)
        }
    }

    // MARK: User identity & properties
    static func setUserIdentity(_ id: String) { sdk.setUserIdentity(userIdentity: id) }
    static func setUserProperty(_ name: String, _ value: String) { sdk.setUserProperty(propertyName: name, value___: value) }
    static func setUserProperty(_ name: String, _ value: Int32) { sdk.setUserProperty(propertyName: name, value__: value) }
    static func setUserProperty(_ name: String, _ value: Float) { sdk.setUserProperty(propertyName: name, value_: value) }
    static func setUserProperty(_ name: String, _ value: Bool) { sdk.setUserProperty(propertyName: name, value: value) }
    static func setPushNotificationToken(_ token: String) { sdk.setPushNotificationToken(token: token) }

    // MARK: Session properties
    static func setSessionProperty(_ name: String, _ value: String) { sdk.setSessionProperty(propertyName: name, value___: value) }
    static func setSessionProperty(_ name: String, _ value: Int32) { sdk.setSessionProperty(propertyName: name, value__: value) }
    static func setSessionProperty(_ name: String, _ value: Float) { sdk.setSessionProperty(propertyName: name, value_: value) }
    static func setSessionProperty(_ name: String, _ value: Bool) { sdk.setSessionProperty(propertyName: name, value: value) }
    static func markSessionAsFavorite() { sdk.markSessionAsFavorite() }

    // MARK: Screen tagging & ignore lists
    static func tagScreen(_ name: String) { sdk.tagScreenName(screenName: name) }
    static func setAutomaticScreenNameTagging(_ enable: Bool) { sdk.setAutomaticScreenNameTagging(enable: enable) }
    static func setImprovedScreenCaptureEnabled(_ enable: Bool) { sdk.setImprovedScreenCaptureEnabled(enable: enable) }
    static func addScreenNameToIgnore(_ name: String) { sdk.addScreenNameToIgnore(screenName: name) }
    static func addScreenNamesToIgnore(_ names: [String]) { sdk.addScreenNamesToIgnore(screenNames: names) }
    static func removeScreenNameToIgnore(_ name: String) { sdk.removeScreenNameToIgnore(screenName: name) }
    static func removeScreenNamesToIgnore(_ names: [String]) { sdk.removeScreenNamesToIgnore(screenNames: names) }
    static func removeAllScreenNamesToIgnore() { sdk.removeAllScreenNamesToIgnore() }
    static func screenNamesBeingIgnored() -> [String] { sdk.screenNamesBeingIgnored() }

    // MARK: Occlusion (screen-level)
    static func occludeSensitiveScreen(_ hide: Bool) { sdk.occludeSensitiveScreen(hide: hide) }
    static func occludeSensitiveScreen(_ hide: Bool, withoutGesture: Bool) { sdk.occludeSensitiveScreen(hide: hide, withoutGesture: withoutGesture) }
    static func occludeAllTextFields(_ occludeAll: Bool) { sdk.occludeAllTextFields(occludeAll: occludeAll) }
    static func applyOverlayOcclusion(withoutGesture: Bool = false) { sdk.applyOverlayOcclusion(withoutGesture: withoutGesture) }
    static func applyBlurOcclusion(blurRadius: Int32 = 15, withoutGesture: Bool = false) { sdk.applyBlurOcclusion(blurRadius: blurRadius, withoutGesture: withoutGesture) }
    static func removeOcclusion() { sdk.removeOcclusion() }

    // MARK: Per-view occlusion (iOS-only; operates on a specific UIView instance)
    static func occludeSensitiveView(_ view: UIView) { sdk.occludeSensitiveView(view: view) }
    static func occludeSensitiveViewWithoutGesture(_ view: UIView) { sdk.occludeSensitiveViewWithoutGesture(view: view) }
    static func unOccludeSensitiveView(_ view: UIView) { sdk.unOccludeSensitiveView(view: view) }

    // MARK: Recording control
    static func pauseScreenRecording() { sdk.pauseScreenRecording() }
    static func resumeScreenRecording() { sdk.resumeScreenRecording() }
    static func isRecording() -> Bool { sdk.isRecording() }
    static func allowShortBreakForAnotherApp() { sdk.allowShortBreakForAnotherApp() }
    static func allowShortBreakForAnotherApp(continueSession: Bool) { sdk.allowShortBreakForAnotherApp(continueSession: continueSession) }
    static func allowShortBreakForAnotherApp(millis: Int32) { sdk.allowShortBreakForAnotherApp(millis: millis) }
    static func resumeShortBreakForAnotherApp() { sdk.resumeShortBreakForAnotherApp() }

    // MARK: Opt in / out
    static func optInOverall() { sdk.optInOverall() }
    static func optOutOverall() { sdk.optOutOverall() }
    static func optInOverallStatus() -> Bool { sdk.optInOverallStatus() }
    static func optIntoVideoRecording() { sdk.optIntoVideoRecording() }
    static func optOutOfVideoRecording() { sdk.optOutOfVideoRecording() }
    static func optInVideoRecordingStatus() -> Bool { sdk.optInVideoRecordingStatus() }

    // MARK: Multi-session
    static func getMultiSessionRecord() -> Bool { sdk.getMultiSessionRecord() }
    static func setMultiSessionRecord(_ enable: Bool) { sdk.setMultiSessionRecord(enable: enable) }

    // MARK: Crash handling
    static func disableCrashHandling(_ disabled: Bool) { sdk.disableCrashHandling(disabled: disabled) }

    // MARK: URLs, uploads & status
    static func urlForCurrentSession() -> String? { sdk.urlForCurrentSession() }
    static func urlForCurrentUser() -> String? { sdk.urlForCurrentUser() }
    static func deletePendingUploads() { sdk.deletePendingUploads() }
    static func pendingSessionCount() -> Int { Int(sdk.pendingSessionCount()) }
    static func pendingUploads(onResult: @escaping (Int) -> Void) {
        sdk.pendingUploads(onResult: { boxed in onResult(Int(truncating: boxed)) })
    }
    static func sdkVersionInfo() -> String { sdk.getSdkVersionInfo() }
}
