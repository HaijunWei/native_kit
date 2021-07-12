package com.haijunwei.native_kit.tool

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel


/**
 *
 * @CreateDate:     2021/7/7 15:19
 * @Description:
 * @Author:         LOPER7
 * @Email:          loper7@163.com
 */

class ScreenBrightnessControl : MethodChannel.MethodCallHandler {
    private var channel: MethodChannel? = null
    private var context: Context? = null
    private var activityBinding: ActivityPluginBinding? = null
    private var currentBrightness = -1f

    companion object {
        val instance: ScreenBrightnessControl by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ScreenBrightnessControl()
        }
    }

    fun setActivityBinding(activityBinding: ActivityPluginBinding?) {
        this.activityBinding = activityBinding

        activityBinding?.activity?.contentResolver?.registerContentObserver(
                Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS), true,
                mBrightnessObserver)
    }

    fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.haijunwei.native_kit/screen_brightness_control")
        channel?.setMethodCallHandler(this)


    }


    fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
        activityBinding?.activity?.contentResolver?.unregisterContentObserver(
                mBrightnessObserver)
        activityBinding = null
        context = null

    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        when (call.method) {
            "getBrightness" -> {
                getBrightness(call, result)
            }
            "setBrightness" -> {
                setBrightness(call, result)
            }
            "record" -> {
                record(call, result)
            }
            "restore" -> {
                restore(call, result)
            }
            "setEnabledAutoKeep" -> {
                setEnabledAutoKeep(call, result)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    /**
     * 获取系统屏幕亮度
     */
    fun getBrightness(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        var systemBrightness = 0
        try {
            systemBrightness = Settings.System.getInt(context?.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        result.success(systemBrightness.toDouble() / 255.0)
    }

    /**
     * 设置当前屏幕亮度
     */
    fun setBrightness(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        var brightness: Double? = call.argument("brightness")
        if (brightness == null) {
            result.error("-1", "brightness cannot be empty", "brightness cannot be empty")
            return
        }

        val localWindow: Window? = activityBinding?.activity?.window
        val localLayoutParams: WindowManager.LayoutParams? = localWindow?.attributes
        localLayoutParams?.screenBrightness = brightness.toFloat()
        localWindow?.attributes = localLayoutParams

        result.success(null)
    }

    /**
     * 记录当前亮度
     */
    fun record(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        currentBrightness = -1f
        try {
            currentBrightness = Settings.System.getInt(context?.contentResolver, Settings.System.SCREEN_BRIGHTNESS).toFloat()
            currentBrightness /= 255.0f
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        result.success(null)
    }

    //还原当前亮度
    fun restore(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        val localWindow: Window? = activityBinding?.activity?.window
        val localLayoutParams: WindowManager.LayoutParams? = localWindow?.attributes
        localLayoutParams?.screenBrightness = 2f
        localWindow?.attributes = localLayoutParams
        result.success(null)
    }

    fun setEnabledAutoKeep(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        result.success(null)
    }

    /**
     * 监听屏幕亮度变化
     */
    private val mBrightnessObserver: ContentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            super.onChange(selfChange)
            Log.d("mBrightnessObserver","mBrightnessObserver->$selfChange")
            var systemBrightness = 0.0
            try {
                systemBrightness = Settings.System.getInt(context?.contentResolver, Settings.System.SCREEN_BRIGHTNESS).toDouble()
            } catch (e: Settings.SettingNotFoundException) {
                e.printStackTrace()
            }
            systemBrightness /= 255.0
            Log.d("mBrightnessObserver","mBrightnessObserver:systemBrightness->$systemBrightness")

            if(selfChange){
                val localWindow: Window? = activityBinding?.activity?.window
                val localLayoutParams: WindowManager.LayoutParams? = localWindow?.attributes
                localLayoutParams?.screenBrightness = 2f
                localWindow?.attributes = localLayoutParams
            }

            val map = hashMapOf<String, Any>()
            map["brightness"] = systemBrightness
            channel?.invokeMethod("brightnessDidChange", map)
        }
    }
}