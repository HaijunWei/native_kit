package com.haijunwei.native_kit.tool

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.NonNull
import androidx.core.content.FileProvider
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.io.File


class AppInstaller : MethodCallHandler {
    private var channel: MethodChannel? = null
    private var context: Context? = null

    companion object {
        val instance: AppInstaller by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            AppInstaller()
        }
    }

    fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        this.context = flutterPluginBinding.applicationContext

        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.haijunwei.native_kit/app_installer")
        channel?.setMethodCallHandler(this)
    }


    fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel?.setMethodCallHandler(null)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "install" -> {
                installApk(call, result)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    /**
     * 安装 apk 文件
     *
     */
    fun installApk(@NonNull call: MethodCall, @NonNull result: Result) {
        val filePath: String? = call.argument("path")
        if (filePath == null) {
            result.error("-1", "Permission denied", "android.permission.REQUEST_INSTALL_PACKAGES is Permission denied")
            return
        }

        context?.apply {
            val apkFile = File(filePath)
            val installApkIntent = Intent()
            installApkIntent.action = Intent.ACTION_VIEW
            installApkIntent.addCategory(Intent.CATEGORY_DEFAULT)
            installApkIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                installApkIntent.setDataAndType(
                        FileProvider.getUriForFile(context!!, "${packageName}.native_kit_file_provider", apkFile),
                        "application/vnd.android.package-archive")
                installApkIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } else {
                installApkIntent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
            }

            if (packageManager.queryIntentActivities(installApkIntent, 0).size > 0) {
                startActivity(installApkIntent)
            }
        }
        result.success(null)
    }

}
