package com.haijunwei.native_kit.tool

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

    companion object{
        val instance: ScreenBrightnessControl by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ScreenBrightnessControl()
        }
    }

    fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.haijunwei.native_kit/screen_brightness_control")
        channel?.setMethodCallHandler(this)
    }


    fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: MethodChannel.Result) {
        when (call.method) {
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

}