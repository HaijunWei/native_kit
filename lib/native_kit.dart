import 'dart:async';
import 'dart:io';

import 'package:flutter/services.dart';

class AppInstaller {
  static const MethodChannel _channel =
      const MethodChannel('com.haijunwei.native_kit/app_installer');

  static Future<void> install(String path) async {
    await _channel.invokeMethod('install', {'path': path});
  }
}

class VolumeControl {
  static const MethodChannel _channel =
      const MethodChannel('com.haijunwei.native_kit/volume_control');

  static StreamController<double> _streamController =
      StreamController.broadcast();

  /// 音量监听流
  static Stream<double> get stream {
    _channel.setMethodCallHandler(_methodCallHandler);
    return _streamController.stream;
  }

  static Future<dynamic> _methodCallHandler(call) async {
    if (call.method == 'volumeDidChange') {
      double volume = call.arguments['volume'];
      _streamController.add(volume);
    }
  }

  /// 获取系统当前音量，取值 0 - 1
  static Future<double> get volume async {
    final double? volume = await _channel.invokeMethod('getVolume');
    return volume ?? 0;
  }

  /// 设置系统音量
  /// `volume`取值 0 - 1
  static Future<void> setVolume(double volume) async {
    await _channel.invokeMethod('setVolume', {'volume': volume});
  }

  /// 设置音量时隐藏/显示系统音量UI
  static Future<void> hideUI(bool hide) async {
    await _channel.invokeMethod('hideUI', {'hide': hide});
  }
}

class ScreenBrightnessControl {
  static const MethodChannel _channel =
      const MethodChannel('com.haijunwei.native_kit/screen_brightness_control');

  static StreamController<double> _streamController =
      StreamController.broadcast();

  /// 屏幕亮度监听流
  static Stream<double> get stream {
    _channel.setMethodCallHandler(_methodCallHandler);
    return _streamController.stream;
  }

  static Future<dynamic> _methodCallHandler(call) async {
    if (call.method == 'brightnessDidChange') {
      double brightness = call.arguments['brightness'];
      _streamController.add(brightness);
    }
  }

  /// 获取屏幕亮度，取值 0 - 1
  static Future<double> get brightness async {
    final double? brightness = await _channel.invokeMethod('getBrightness');
    return brightness ?? 0;
  }

  /// 设置亮度，取值 0 - 1
  static Future<void> setBrightness(double brightness) async {
    await _channel.invokeMethod('setBrightness', {'brightness': brightness});
  }

  /// 记录当前亮度，在安卓上无效
  static Future<void> record() async {
    await _channel.invokeMethod('record');
  }

  /// 还原上次记录的亮度，还原上次记录的亮度，安卓还原到系统亮度
  static Future<void> restore() async {
    await _channel.invokeMethod('restore');
  }

  /// 是否启用自动管理亮度，App进入后台时还原亮度，App进入前台时保持亮度
  static Future<void> setEnabledAutoKeep(bool enabled) async {
    await _channel.invokeMethod('setEnabledAutoKeep', {'enabled': enabled});
  }
}

class ClipboardExtension {
  static const MethodChannel _channel =
      const MethodChannel('com.haijunwei.native_kit/clipboard_extension');

  static Future<int> get changeCount async {
    int? count;
    if (Platform.isIOS) {
      count = await _channel.invokeMethod('getChangeCount');
    }
    return count ?? 0;
  }
}
