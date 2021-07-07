package com.haijunwei.native_kit.tool

import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

class VolumeControl : MethodCallHandler {
    private lateinit var channel: MethodChannel

    companion object {
        val instance: VolumeControl by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            VolumeControl()
        }
    }

    fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.haijunwei.native_kit/volume_control")
        channel.setMethodCallHandler(this)
    }

    fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "getVolume" -> {
                //获取系统当前音量，取值 0 - 1
                result.success(0.55)
            }
            "setVolume" -> {
                //设置系统音量
            }
            "hideUI" -> {
                //设置音量时隐藏/显示系统音量UI
            }
            else -> {
                result.notImplemented()
            }
        }
    }


}
