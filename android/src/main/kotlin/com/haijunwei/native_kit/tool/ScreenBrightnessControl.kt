package com.haijunwei.native_kit.tool

import android.content.Context
import android.provider.Settings
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
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
    private lateinit var context: Context

    companion object{
        val instance: ScreenBrightnessControl by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ScreenBrightnessControl()
        }
    }

    fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        context  = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.haijunwei.native_kit/screen_brightness_control")
        channel?.setMethodCallHandler(this)
    }


    fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        when (call.method) {
            "getBrightness"->{
                getBrightness(call,result)
            }
            "setBrightness" -> {

            }
            "record" -> {

            }
            "restore" -> {

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
            systemBrightness = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        result.success(systemBrightness.toDouble()/255.0)
    }

}