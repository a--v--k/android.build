package org.ak2.android.build

/**
 * Enumeration of the currently known SDK version codes.  These are the
 * values that can be found in [VERSION.SDK].  Version numbers
 * increment monotonically with each official platform release.
 */
enum class AndroidVersion(val code : Int) {
    /**
     * Magic version number for a current development build, which has
     * not yet turned into an official release.
     */
    CUR_DEVELOPMENT(10000),

    /**
     * October 2008: The original, first, version of Android.  Yay!
     */
    BASE(1), ANDROID_1_0(org.ak2.android.build.AndroidVersion.BASE.code),

    /**
     * February 2009: First Android update, officially called 1.1.
     */
    BASE_1_1(2), ANDROID_1_1(org.ak2.android.build.AndroidVersion.BASE_1_1.code),

    /**
     * May 2009: Android 1.5.
     */
    CUPCAKE(3), ANDROID_1_5(org.ak2.android.build.AndroidVersion.CUPCAKE.code),

    /**
     * September 2009: Android 1.6.
     *
     *
     * Applications targeting this or a later release will get these
     * new changes in behavior:
     *
     *  *  They must explicitly request the
     * [android.Manifest.permission.WRITE_EXTERNAL_STORAGE] permission to be
     * able to modify the contents of the SD card.  (Apps targeting
     * earlier versions will always request the permission.)
     *  *  They must explicitly request the
     * [android.Manifest.permission.READ_PHONE_STATE] permission to be
     * able to be able to retrieve phone state info.  (Apps targeting
     * earlier versions will always request the permission.)
     *  *  They are assumed to support different screen densities and
     * sizes.  (Apps targeting earlier versions are assumed to only support
     * medium density normal size screens unless otherwise indicated).
     * They can still explicitly specify screen support either way with the
     * supports-screens manifest tag.
     *  *  [android.widget.TabHost] will use the new dark tab
     * background design.
     *
     */
    DONUT(4), ANDROID_1_6(org.ak2.android.build.AndroidVersion.DONUT.code),

    /**
     * November 2009: Android 2.0
     *
     *
     * Applications targeting this or a later release will get these
     * new changes in behavior:
     *
     *  *  The [ Service.onStartCommand][android.app.Service.onStartCommand] function will return the new
     * [android.app.Service.START_STICKY] behavior instead of the
     * old compatibility [android.app.Service.START_STICKY_COMPATIBILITY].
     *  *  The [android.app.Activity] class will now execute back
     * key presses on the key up instead of key down, to be able to detect
     * canceled presses from virtual keys.
     *  *  The [android.widget.TabWidget] class will use a new color scheme
     * for tabs. In the new scheme, the foreground tab has a medium gray background
     * the background tabs have a dark gray background.
     *
     */
    ECLAIR(5), ANDROID_2_0(org.ak2.android.build.AndroidVersion.ECLAIR.code),

    /**
     * December 2009: Android 2.0.1
     */
    ECLAIR_0_1(6), ANDROID_2_0_1(org.ak2.android.build.AndroidVersion.ECLAIR_0_1.code),

    /**
     * January 2010: Android 2.1
     */
    ECLAIR_MR1(7), ANDROID_2_1(org.ak2.android.build.AndroidVersion.ECLAIR_MR1.code),

    /**
     * June 2010: Android 2.2
     */
    FROYO(8), ANDROID_2_2(org.ak2.android.build.AndroidVersion.FROYO.code),

    /**
     * November 2010: Android 2.3
     *
     *
     * Applications targeting this or a later release will get these
     * new changes in behavior:
     *
     *  *  The application's notification icons will be shown on the new
     * dark status bar background, so must be visible in this situation.
     *
     */
    GINGERBREAD(9), ANDROID_2_3(org.ak2.android.build.AndroidVersion.GINGERBREAD.code),

    /**
     * February 2011: Android 2.3.3.
     */
    GINGERBREAD_MR1(10), ANDROID_2_3_3(org.ak2.android.build.AndroidVersion.GINGERBREAD_MR1.code),

    /**
     * February 2011: Android 3.0.
     *
     *
     * Applications targeting this or a later release will get these
     * new changes in behavior:
     *
     *  *  The default theme for applications is now dark holographic:
     * [android.R.style.Theme_Holo].
     *  *  On large screen devices that do not have a physical menu
     * button, the soft (compatibility) menu is disabled.
     *  *  The activity lifecycle has changed slightly as per
     * [android.app.Activity].
     *  *  An application will crash if it does not call through
     * to the super implementation of its
     * [Activity.onPause()][android.app.Activity.onPause] method.
     *  *  When an application requires a permission to access one of
     * its components (activity, receiver, service, provider), this
     * permission is no longer enforced when the application wants to
     * access its own component.  This means it can require a permission
     * on a component that it does not itself hold and still access that
     * component.
     *  *  [ Context.getSharedPreferences()][android.content.Context.getSharedPreferences] will not automatically reload
     * the preferences if they have changed on storage, unless
     * [android.content.Context.MODE_MULTI_PROCESS] is used.
     *  *  [android.view.ViewGroup.setMotionEventSplittingEnabled]
     * will default to true.
     *  *  [android.view.WindowManager.LayoutParams.FLAG_SPLIT_TOUCH]
     * is enabled by default on windows.
     *  *  [ PopupWindow.isSplitTouchEnabled()][android.widget.PopupWindow.isSplitTouchEnabled] will return true by default.
     *  *  [android.widget.GridView] and [android.widget.ListView]
     * will use [View.setActivated][android.view.View.setActivated]
     * for selected items if they do not implement [android.widget.Checkable].
     *  *  [android.widget.Scroller] will be constructed with
     * "flywheel" behavior enabled by default.
     *
     */
    HONEYCOMB(11), ANDROID_3_0(org.ak2.android.build.AndroidVersion.HONEYCOMB.code),

    /**
     * May 2011: Android 3.1.
     */
    HONEYCOMB_MR1(12), ANDROID_3_1(org.ak2.android.build.AndroidVersion.HONEYCOMB_MR1.code),

    /**
     * June 2011: Android 3.2.
     *
     *
     * Update to Honeycomb MR1 to support 7 inch tablets, improve
     * screen compatibility mode, etc.
     *
     *
     * As of this version, applications that don't say whether they
     * support XLARGE screens will be assumed to do so only if they target
     * [.HONEYCOMB] or later; it had been [.GINGERBREAD] or
     * later.  Applications that don't support a screen size at least as
     * large as the current screen will provide the user with a UI to
     * switch them in to screen size compatibility mode.
     *
     *
     * This version introduces new screen size resource qualifiers
     * based on the screen size in dp: see
     * [android.content.res.Configuration.screenWidthDp],
     * [android.content.res.Configuration.screenHeightDp], and
     * [android.content.res.Configuration.smallestScreenWidthDp].
     * Supplying these in &lt;supports-screens&gt; as per
     * [android.content.pm.ApplicationInfo.requiresSmallestWidthDp],
     * [android.content.pm.ApplicationInfo.compatibleWidthLimitDp], and
     * [android.content.pm.ApplicationInfo.largestWidthLimitDp] is
     * preferred over the older screen size buckets and for older devices
     * the appropriate buckets will be inferred from them.
     *
     *
     * Applications targeting this or a later release will get these
     * new changes in behavior:
     *
     *  *
     *
     *New [android.content.pm.PackageManager.FEATURE_SCREEN_PORTRAIT]
     * and [android.content.pm.PackageManager.FEATURE_SCREEN_LANDSCAPE]
     * features were introduced in this release.  Applications that target
     * previous platform versions are assumed to require both portrait and
     * landscape support in the device; when targeting Honeycomb MR1 or
     * greater the application is responsible for specifying any specific
     * orientation it requires.
     *  *
     *
     *[android.os.AsyncTask] will use the serial executor
     * by default when calling [android.os.AsyncTask.execute].
     *  *
     *
     *[ ActivityInfo.configChanges][android.content.pm.ActivityInfo.configChanges] will have the
     * [android.content.pm.ActivityInfo.CONFIG_SCREEN_SIZE] and
     * [android.content.pm.ActivityInfo.CONFIG_SMALLEST_SCREEN_SIZE]
     * bits set; these need to be cleared for older applications because
     * some developers have done absolute comparisons against this value
     * instead of correctly masking the bits they are interested in.
     *
     */
    HONEYCOMB_MR2(13), ANDROID_3_2(org.ak2.android.build.AndroidVersion.HONEYCOMB_MR2.code),

    /**
     * October 2011: Android 4.0.
     *
     *
     * Applications targeting this or a later release will get these
     * new changes in behavior:
     *
     *  *  For devices without a dedicated menu key, the software compatibility
     * menu key will not be shown even on phones.  By targeting Ice Cream Sandwich
     * or later, your UI must always have its own menu UI affordance if needed,
     * on both tablets and phones.  The ActionBar will take care of this for you.
     *  *  2d drawing hardware acceleration is now turned on by default.
     * You can use
     * [configurators:hardwareAccelerated][android.R.attr.hardwareAccelerated]
     * to turn it off if needed, although this is strongly discouraged since
     * it will result in poor performance on larger screen devices.
     *  *  The default theme for applications is now the "device default" theme:
     * [android.R.style.Theme_DeviceDefault]. This may be the
     * holo dark theme or a different dark theme defined by the specific device.
     * The [android.R.style.Theme_Holo] family must not be modified
     * for a device to be considered compatible. Applications that explicitly
     * request a theme from the Holo family will be guaranteed that these themes
     * will not change character within the same platform version. Applications
     * that wish to blend in with the device should use a theme from the
     * [android.R.style.Theme_DeviceDefault] family.
     *  *  Managed cursors can now throw an exception if you directly close
     * the cursor yourself without stopping the management of it; previously failures
     * would be silently ignored.
     *  *  The fadingEdge attribute on views will be ignored (fading edges is no
     * longer a standard part of the UI).  A new requiresFadingEdge attribute allows
     * applications to still force fading edges on for special cases.
     *  *  [Context.bindService()][android.content.Context.bindService]
     * will not automatically add in [android.content.Context.BIND_WAIVE_PRIORITY].
     *  *  App Widgets will have standard padding automatically added around
     * them, rather than relying on the padding being baked into the widget itself.
     *  *  An exception will be thrown if you try to change the type of a
     * window after it has been added to the window manager.  Previously this
     * would result in random incorrect behavior.
     *  *  [android.view.animation.AnimationSet] will parse out
     * the duration, fillBefore, fillAfter, repeatMode, and startOffset
     * XML attributes that are defined.
     *  *  [ ActionBar.setHomeButtonEnabled()][android.app.ActionBar.setHomeButtonEnabled] is false by default.
     *
     */
    ICE_CREAM_SANDWICH(14), ANDROID_4_0(org.ak2.android.build.AndroidVersion.ICE_CREAM_SANDWICH.code),

    /**
     * December 2011: Android 4.0.3.
     */
    ICE_CREAM_SANDWICH_MR1(15), ANDROID_4_0_3(org.ak2.android.build.AndroidVersion.ICE_CREAM_SANDWICH_MR1.code),

    /**
     * June 2012: Android 4.1.
     *
     *
     * Applications targeting this or a later release will get these
     * new changes in behavior:
     *
     *  *  You must explicitly request the [android.Manifest.permission.READ_CALL_LOG]
     * and/or [android.Manifest.permission.WRITE_CALL_LOG] permissions;
     * access to the call log is no longer implicitly provided through
     * [android.Manifest.permission.READ_CONTACTS] and
     * [android.Manifest.permission.WRITE_CONTACTS].
     *  *  [android.widget.RemoteViews] will throw an exception if
     * setting an onClick handler for views being generated by a
     * [android.widget.RemoteViewsService] for a collection container;
     * previously this just resulted in a warning log message.
     *  *  New [android.app.ActionBar] policy for embedded tabs:
     * embedded tabs are now always stacked in the action bar when in portrait
     * mode, regardless of the size of the screen.
     *  *  [ WebSettings.setAllowFileAccessFromFileURLs][android.webkit.WebSettings.setAllowFileAccessFromFileURLs] and
     * [ WebSettings.setAllowUniversalAccessFromFileURLs][android.webkit.WebSettings.setAllowUniversalAccessFromFileURLs] default to false.
     *  *  Calls to [ PackageManager.setComponentEnabledSetting][android.content.pm.PackageManager.setComponentEnabledSetting] will now throw an
     * IllegalArgumentException if the given component class name does not
     * exist in the application's manifest.
     *  *  [ NfcAdapter.setNdefPushMessage][android.nfc.NfcAdapter.setNdefPushMessage],
     * [ NfcAdapter.setNdefPushMessageCallback][android.nfc.NfcAdapter.setNdefPushMessageCallback] and
     * [ NfcAdapter.setOnNdefPushCompleteCallback][android.nfc.NfcAdapter.setOnNdefPushCompleteCallback] will throw
     * IllegalStateException if called after the Activity has been destroyed.
     *  *  Accessibility services must require the new
     * [android.Manifest.permission.BIND_ACCESSIBILITY_SERVICE] permission or
     * they will not be available for use.
     *  *  [ AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS][android.accessibilityservice.AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS] must be set
     * for unimportant views to be included in queries.
     *
     */
    JELLY_BEAN(16), ANDROID_4_1(org.ak2.android.build.AndroidVersion.JELLY_BEAN.code),

    /**
     * November 2012: Android 4.2, Moar jelly beans!
     *
     *
     * Applications targeting this or a later release will get these
     * new changes in behavior:
     *
     *  * Content Providers: The default value of `configurators:exported` is now
     * `false`. See
     * [
 * the configurators:exported section]({@docRoot}guide/topics/manifest/provider-element.html#exported) in the provider documentation for more details.
     *  * [View.getLayoutDirection()][android.view.View.getLayoutDirection]
     * can return different values than [android.view.View.LAYOUT_DIRECTION_LTR]
     * based on the locale etc.
     *  *  [ WebView.addJavascriptInterface][android.webkit.WebView.addJavascriptInterface]
     *  requires explicit annotations on methods for them to be accessible from Javascript.
     *
     */
    JELLY_BEAN_MR1(17), ANDROID_4_2(org.ak2.android.build.AndroidVersion.JELLY_BEAN_MR1.code),

    /**
     * July 2013: Android 4.3, the revenge of the beans.
     */
    JELLY_BEAN_MR2(18), ANDROID_4_3(org.ak2.android.build.AndroidVersion.JELLY_BEAN_MR2.code),

    /**
     * October 2013: Android 4.4, KitKat, another tasty treat.
     *
     *
     * Applications targeting this or a later release will get these
     * new changes in behavior:
     *
     *  *  The default result of
     * [ PreferenceActivity.isValueFragment][android.preference.PreferenceActivity.isValidFragment]
     * becomes false instead of true.
     *  *  In [android.webkit.WebView], apps targeting earlier versions will have
     * JS URLs evaluated directly and any result of the evaluation will not replace
     * the current page content.  Apps targetting KITKAT or later that load a JS URL will
     * have the result of that URL replace the content of the current page
     *  *  [AlarmManager.set][android.app.AlarmManager.set] becomes interpreted as
     * an inexact value, to give the system more flexibility in scheduling alarms.
     *  *  [ Context.getSharedPreferences][android.content.Context.getSharedPreferences]
     *  no longer allows a null name.
     *  *  [android.widget.RelativeLayout] changes to compute wrapped content
     * margins correctly.
     *  *  [android.app.ActionBar]'s window content overlay is allowed to be
     * drawn.
     *  * The [android.Manifest.permission.READ_EXTERNAL_STORAGE]
     * permission is now always enforced.
     *  * Access to package-specific external storage directories belonging
     * to the calling app no longer requires the
     * [android.Manifest.permission.READ_EXTERNAL_STORAGE] or
     * [android.Manifest.permission.WRITE_EXTERNAL_STORAGE]
     * permissions.
     *
     */
    KITKAT(19), ANDROID_4_4(org.ak2.android.build.AndroidVersion.KITKAT.code),

    /**
     * June 2014: Android 4.4W. KitKat for watches, snacks on the run.
     *
     *
     * Applications targeting this or a later release will get these
     * new changes in behavior:
     *
     *  * [android.app.AlertDialog] might not have a default background if the theme does
     * not specify one.
     *
     */
    KITKAT_WATCH(20), ANDROID_4_4_W(org.ak2.android.build.AndroidVersion.KITKAT_WATCH.code),

    /**
     * Temporary until we completely switch to [.LOLLIPOP].
     * @hide
     */
    L(21),

    /**
     * November 2014: Android 5.0 Lollipop.  A flat one with beautiful shadows.  But still tasty.
     *
     *
     * Applications targeting this or a later release will get these
     * new changes in behavior:
     *
     *  *  [Context.bindService][android.content.Context.bindService] now
     * requires an explicit Intent, and will throw an exception if given an implicit
     * Intent.
     *  *  [Notification.Builder][android.app.Notification.Builder] will
     * not have the colors of their various notification elements adjusted to better
     * match the new material design look.
     *  *  [android.os.Message] will validate that a message is not currently
     * in use when it is recycled.
     *  *  Hardware accelerated drawing in windows will be enabled automatically
     * in most places.
     *  *  [android.widget.Spinner] throws an exception if attaching an
     * adapter with more than one item type.
     *  *  If the app is a launcher, the launcher will be available to the user
     * even when they are using corporate profiles (which requires that the app
     * use [android.content.pm.LauncherApps] to correctly populate its
     * apps UI).
     *  *  Calling [Service.stopForeground][android.app.Service.stopForeground]
     * with removeNotification false will modify the still posted notification so that
     * it is no longer forced to be ongoing.
     *  *  A [android.service.dreams.DreamService] must require the
     * [android.Manifest.permission.BIND_DREAM_SERVICE] permission to be usable.
     *
     */
    LOLLIPOP(21), ANDROID_5_0(org.ak2.android.build.AndroidVersion.LOLLIPOP.code),

    /**
     * March 2015: Android 5.1 Lollipop with an extra sugar coating on the outside!
     */
    LOLLIPOP_MR1(22), ANDROID_5_1(org.ak2.android.build.AndroidVersion.LOLLIPOP_MR1.code),

    /**
     * M is for Marshmallow! Android 6.0
     *
     *
     * Applications targeting this or a later release will get these
     * new changes in behavior:
     *
     *  *  Runtime permissions.  Dangerous permissions are no longer granted at
     * install time, but must be requested by the application at runtime through
     * [android.app.Activity.requestPermissions].
     *  *  Bluetooth and Wi-Fi scanning now requires holding the location permission.
     *  *  [AlarmManager.setTimeZone][android.app.AlarmManager.setTimeZone] will fail if
     * the given timezone is non-Olson.
     *  *  Activity transitions will only return shared
     * elements mapped in the returned view hierarchy back to the calling activity.
     *  *  [android.view.View] allows a number of behaviors that may break
     * existing apps: Canvas throws an exception if restore() is called too many times,
     * widgets may return a hint size when returning UNSPECIFIED measure specs, and it
     * will respect the attributes [android.R.attr.foreground],
     * [android.R.attr.foregroundGravity], [android.R.attr.foregroundTint], and
     * [android.R.attr.foregroundTintMode].
     *  *  [MotionEvent.getButtonState][android.view.MotionEvent.getButtonState]
     * will no longer report [android.view.MotionEvent.BUTTON_PRIMARY]
     * and [android.view.MotionEvent.BUTTON_SECONDARY] as synonyms for
     * [android.view.MotionEvent.BUTTON_STYLUS_PRIMARY] and
     * [android.view.MotionEvent.BUTTON_STYLUS_SECONDARY].
     *  *  [android.widget.ScrollView] now respects the layout param margins
     * when measuring.
     *
     */
    M(23), ANDROID_6_0(org.ak2.android.build.AndroidVersion.M.code),

    /**
     * N is for Nougat. Android 7.0
     *
     *
     * Applications targeting this or a later release will get these
     * new changes in behavior:
     *
     *  *  [ DownloadManager.Request.setAllowedNetworkTypes][android.app.DownloadManager.Request.setAllowedNetworkTypes]
     * will disable "allow over metered" when specifying only
     * [android.app.DownloadManager.Request.NETWORK_WIFI].
     *  *  [android.app.DownloadManager] no longer allows access to raw
     * file paths.
     *  *  [ Notification.Builder.setShowWhen][android.app.Notification.Builder.setShowWhen]
     * must be called explicitly to have the time shown, and various other changes in
     * [Notification.Builder][android.app.Notification.Builder] to how notifications
     * are shown.
     *  * [android.content.Context.MODE_WORLD_READABLE] and
     * [android.content.Context.MODE_WORLD_WRITEABLE] are no longer supported.
     *  * [android.os.FileUriExposedException] will be thrown to applications.
     *  * Applications will see global drag and drops as per
     * [android.view.View.DRAG_FLAG_GLOBAL].
     *  * [WebView.evaluateJavascript][android.webkit.WebView.evaluateJavascript]
     * will not persist state from an empty WebView.
     *  * [android.animation.AnimatorSet] will not ignore calls to end() before
     * start().
     *  * [ AlarmManager.cancel][android.app.AlarmManager.cancel] will throw a NullPointerException
     *  if given a null operation.
     *  * [android.app.FragmentManager] will ensure fragments have been created
     * before being placed on the back stack.
     *  * [android.app.FragmentManager] restores fragments in
     * [Fragment.onCreate][android.app.Fragment.onCreate] rather than after the
     * method returns.
     *  * [android.R.attr.resizeableActivity] defaults to true.
     *  * [android.graphics.drawable.AnimatedVectorDrawable] throws exceptions when
     * opening invalid VectorDrawable animations.
     *  * [android.view.ViewGroup.MarginLayoutParams] will no longer be dropped
     * when converting between some types of layout params (such as
     * [LinearLayout.LayoutParams][android.widget.LinearLayout.LayoutParams] to
     * [RelativeLayout.LayoutParams][android.widget.RelativeLayout.LayoutParams]).
     *  * Your application processes will not be killed when the device density changes.
     *  * Drag and drop. After a view receives the
     * [android.view.DragEvent.ACTION_DRAG_ENTERED] event, when the drag shadow moves into
     * a descendant view that can accept the data, the view receives the
     * [android.view.DragEvent.ACTION_DRAG_EXITED] event and won’t receive
     * [android.view.DragEvent.ACTION_DRAG_LOCATION] and
     * [android.view.DragEvent.ACTION_DROP] events while the drag shadow is within that
     * descendant view, even if the descendant view returns `false` from its handler
     * for these events.
     *
     */
    N(24), ANDROID_7_0(org.ak2.android.build.AndroidVersion.N.code),

    /**
     * N MR1: Nougat++. Android 7.1
     */
    N_MR1(25), ANDROID_7_1(org.ak2.android.build.AndroidVersion.N_MR1.code),

    /**
     * O. Android 8.0
     *
     *
     * Applications targeting this or a later release will get these
     * new changes in behavior:
     *
     *  * [Background execution limits]({@docRoot}about/versions/oreo/background.html)
     * are applied to the application.
     *  * The behavior of AccountManager's
     * [android.accounts.AccountManager.getAccountsByType],
     * [android.accounts.AccountManager.getAccountsByTypeAndFeatures], and
     * [android.accounts.AccountManager.hasFeatures] has changed as documented there.
     *  * [android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE_PRE_26]
     * is now returned as
     * [android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_PERCEPTIBLE].
     *  * The [android.app.NotificationManager] now requires the use of notification
     * channels.
     *  * Changes to the strict mode that are set in
     * [Application.onCreate] will no longer be clobbered after
     * that function returns.
     *  * A shared library apk with native code will have that native code included in
     * the library path of its clients.
     *  * [Context.getSharedPreferences][android.content.Context.getSharedPreferences]
     * in credential encrypted storage will throw an exception before the user is unlocked.
     *  * Attempting to retrieve a [Context.FINGERPRINT_SERVICE] on a device that
     * does not support that feature will now throw a runtime exception.
     *  * [android.app.Fragment] will stop any active view animations when
     * the fragment is stopped.
     *  * Some compatibility code in Resources that attempts to use the default Theme
     * the app may be using will be turned off, requiring the app to explicitly request
     * resources with the right theme.
     *  * [ContentResolver.notifyChange][android.content.ContentResolver.notifyChange] and
     * [ ContentResolver.registerContentObserver][android.content.ContentResolver.registerContentObserver]
     * will throw a SecurityException if the caller does not have permission to access
     * the provider (or the provider doesn't exit); otherwise the call will be silently
     * ignored.
     *  * [ CameraDevice.createCaptureRequest][android.hardware.camera2.CameraDevice.createCaptureRequest]
     *  will enable [android.hardware.camera2.CaptureRequest.CONTROL_ENABLE_ZSL] by default for
     * still image capture.
     *  * WallpaperManager's [android.app.WallpaperManager.getWallpaperFile],
     * [android.app.WallpaperManager.getDrawable],
     * [android.app.WallpaperManager.getFastDrawable],
     * [android.app.WallpaperManager.peekDrawable], and
     * [android.app.WallpaperManager.peekFastDrawable] will throw an exception
     * if you can not access the wallpaper.
     *  * The behavior of
     * [UsbDeviceConnection.requestWait][android.hardware.usb.UsbDeviceConnection.requestWait]
     * is modified as per the documentation there.
     *  * [StrictMode.VmPolicy.Builder.detectAll]
     * will also enable [StrictMode.VmPolicy.Builder.detectContentUriWithoutPermission]
     * and [StrictMode.VmPolicy.Builder.detectUntaggedSockets].
     *  * [StrictMode.ThreadPolicy.Builder.detectAll]
     * will also enable [StrictMode.ThreadPolicy.Builder.detectUnbufferedIo].
     *  * [android.provider.DocumentsContract]'s various methods will throw failure
     * exceptions back to the caller instead of returning null.
     *  * [View.hasFocusable] now includes auto-focusable views.
     *  * [android.view.SurfaceView] will no longer always change the underlying
     * Surface object when something about it changes; apps need to look at the current
     * state of the object to determine which things they are interested in have changed.
     *  * [android.view.WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY] must be
     * used for overlay windows, other system overlay window types are not allowed.
     *  * [ ViewTreeObserver.addOnDrawListener][android.view.ViewTreeObserver.addOnDrawListener]
     *  will throw an exception if called from within onDraw.
     *  * [Canvas.setBitmap][android.graphics.Canvas.setBitmap] will no longer preserve
     * the current matrix and clip stack of the canvas.
     *  * [ListPopupWindow.setHeight][android.widget.ListPopupWindow.setHeight]
     * will throw an exception if a negative height is supplied.
     *  * [android.widget.TextView] will use internationalized input for numbers,
     * dates, and times.
     *  * [android.widget.Toast] must be used for showing toast windows; the toast
     * window type can not be directly used.
     *  * [WifiManager.getConnectionInfo][android.net.wifi.WifiManager.getConnectionInfo]
     * requires that the caller hold the location permission to return BSSID/SSID
     *  * [WifiP2pManager.requestPeers][android.net.wifi.p2p.WifiP2pManager.requestPeers]
     * requires the caller hold the location permission.
     *  * [android.R.attr.maxAspectRatio] defaults to 0, meaning there is no restriction
     * on the app's maximum aspect ratio (so it can be stretched to fill larger screens).
     *  * [android.R.attr.focusable] defaults to a new state (`auto`) where it will
     * inherit the value of [android.R.attr.clickable] unless explicitly overridden.
     *  * A default theme-appropriate focus-state highlight will be supplied to all Views
     * which don't provide a focus-state drawable themselves. This can be disabled by setting
     * [android.R.attr.defaultFocusHighlightEnabled] to false.
     *
     */
    O(26), ANDROID_8_0(org.ak2.android.build.AndroidVersion.O.code),

    /**
     * O MR1. Android 8.1
     *
     *
     * Applications targeting this or a later release will get these
     * new changes in behavior:
     *
     *  * Apps exporting and linking to apk shared libraries must explicitly
     * enumerate all signing certificates in a consistent order.
     *  * [android.R.attr.screenOrientation] can not be used to request a fixed
     * orientation if the associated activity is not fullscreen and opaque.
     *
     */
    O_MR1(27), ANDROID_8_1(org.ak2.android.build.AndroidVersion.O_MR1.code),

    /**
     * P. Android 9.0
     *
     *
     * Applications targeting this or a later release will get these
     * new changes in behavior:
     *
     *  * [Service.startForeground][android.app.Service.startForeground] requires
     * that apps hold the permission
     * [android.Manifest.permission.FOREGROUND_SERVICE].
     *  * [android.widget.LinearLayout] will always remeasure weighted children,
     * even if there is no excess space.
     *
     */
    P(28), ANDROID_9_0(org.ak2.android.build.AndroidVersion.P.code),

    Q(29), ANDROID_10_0(org.ak2.android.build.AndroidVersion.Q.code),
}