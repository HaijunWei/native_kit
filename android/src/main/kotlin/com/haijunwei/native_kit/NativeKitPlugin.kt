package com.haijunwei.native_kit

import androidx.annotation.NonNull
import com.haijunwei.native_kit.tool.AppInstaller
import com.haijunwei.native_kit.tool.VolumeControl

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** NativeKitPlugin */
class NativeKitPlugin: FlutterPlugin {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "native_kit")
    channel.setMethodCallHandler { call, result ->

    }

    AppInstaller.instance.onAttachedToEngine(flutterPluginBinding)
    VolumeControl.instance.onAttachedToEngine(flutterPluginBinding)
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
    AppInstaller.instance.onDetachedFromEngine(binding)
    VolumeControl.instance.onDetachedFromEngine(binding)
  }
}
