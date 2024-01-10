package com.haijunwei.native_kit.tool

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.io.Console
import java.lang.reflect.Field


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
    private var maximumBrightness = 0f

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
        maximumBrightness = getScreenMaximumBrightness(flutterPluginBinding.applicationContext)
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
        val localWindow: Window? = activityBinding?.activity?.window
        val localLayoutParams: WindowManager.LayoutParams? = localWindow?.attributes
        var systemBrightness = localLayoutParams?.screenBrightness ?: 0f
        if (systemBrightness < 0) {
            try {
                systemBrightness = Settings.System.getInt(context?.contentResolver, Settings.System.SCREEN_BRIGHTNESS) / maximumBrightness
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        result.success(systemBrightness)
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
        result.success(null)
    }

    //还原当前亮度
    fun restore(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        val localWindow: Window? = activityBinding?.activity?.window
        val localLayoutParams: WindowManager.LayoutParams? = localWindow?.attributes
        localLayoutParams?.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
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
            var systemBrightness = 0.0
            try {
                systemBrightness = Settings.System.getInt(context?.contentResolver, Settings.System.SCREEN_BRIGHTNESS).toDouble()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            systemBrightness /= maximumBrightness

            if(selfChange){
                val localWindow: Window? = activityBinding?.activity?.window
                val localLayoutParams: WindowManager.LayoutParams? = localWindow?.attributes
                localLayoutParams?.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
                localWindow?.attributes = localLayoutParams
            }

            val map = hashMapOf<String, Any>()
            map["brightness"] = systemBrightness
            channel?.invokeMethod("brightnessDidChange", map)
        }
    }

    private fun getScreenMaximumBrightness(context: Context): Float {
        try {
            val powerManager: PowerManager =
                context.getSystemService(Context.POWER_SERVICE) as PowerManager?
                    ?: throw ClassNotFoundException()
            val fields: Array<Field> = powerManager.javaClass.declaredFields
            for (field in fields) {
                if (field.name.equals("BRIGHTNESS_ON")) {
                    field.isAccessible = true
                    return (field[powerManager] as Int).toFloat()
                }
            }

            return 255.0f
        } catch (e: Exception) {
            return 255.0f
        }
    }
}