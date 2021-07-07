package com.haijunwei.native_kit.tool

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.util.Log
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlin.math.roundToInt


class VolumeControl : MethodCallHandler {
    private lateinit var channel: MethodChannel
    private lateinit var context: Context
    private val audioManager by lazy { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    private var hideUI: Boolean = false

    companion object {
        val instance: VolumeControl by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            VolumeControl()
        }
    }

    fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        this.context = flutterPluginBinding.applicationContext

        // 注册音量监听
        val volumeReceiver = VolumeReceiver()
        val filter = IntentFilter()
        filter.addAction("android.media.VOLUME_CHANGED_ACTION")
        context.registerReceiver(volumeReceiver, filter)

        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.haijunwei.native_kit/volume_control")
        channel.setMethodCallHandler(this)
    }

    fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "getVolume" -> {
                getStreamVolume(call, result)
            }
            "setVolume" -> {
                setStreamVolume(call, result)
            }
            "hideUI" -> {
                hideUI(call, result)
            }
            else -> {
                result.notImplemented()
            }
        }
    }


    /**
     * 获取系统当前音量值（最大值1）
     */
    fun getStreamVolume(@NonNull call: MethodCall, @NonNull result: Result) {
        val max: Double = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toDouble()
        val current: Double = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toDouble()
        result.success(current / max)
    }

    /**
     * 设置音量
     */
    fun setStreamVolume(@NonNull call: MethodCall, @NonNull result: Result) {
        val volume: Double? = call.argument("volume")
        if (volume == null) {
            result.error("-1", "Volume cannot be empty", "Volume cannot be empty")
            return
        }
        val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (max * volume).roundToInt(), if (hideUI) AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE else AudioManager.FLAG_SHOW_UI)
        result.success(null)
    }

    /**
     * 隐藏UI
     */
    fun hideUI(call: MethodCall, result: Result) {
        hideUI = call.argument<Boolean>("hide")!!
        result.success(null)
    }


    /**
     * 音量监听
     */
    private inner class VolumeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "android.media.VOLUME_CHANGED_ACTION") {
                val max: Int = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                val current: Int = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                var map = hashMapOf<String, Any>()
                map["volume"] = current.toDouble() / max.toDouble()
                channel.invokeMethod("volumeDidChange", current.toDouble() / max.toDouble())
            }
        }
    }

}
