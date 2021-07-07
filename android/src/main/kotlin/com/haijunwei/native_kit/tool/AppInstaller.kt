package com.haijunwei.native_kit.tool

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

class AppInstaller : MethodCallHandler {
    private var channel: MethodChannel? = null

    companion object{
        val instance: AppInstaller by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            AppInstaller()
        }
    }

    fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.haijunwei.native_kit/app_installer")
        channel?.setMethodCallHandler(this)
    }


    fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "install" -> {
                //获取系统当前音量，取值 0 - 1
            }
            else -> {
                result.notImplemented()
            }
        }
    }

}
