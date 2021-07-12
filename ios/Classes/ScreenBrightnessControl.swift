//
//  ScreenBrightnessControl.swift
//  native_kit
//
//  Created by Haijun on 2021/7/7.
//

import Foundation
import Flutter
import UIKit

class ScreenBrightnessControl: NSObject, FlutterPlugin, FlutterApplicationLifeCycleDelegate {
    var channel: FlutterMethodChannel?
    var recordBrightness: CGFloat?
    var changedBrightness: CGFloat?
    var enabledAutoKeep: Bool = false
    
    static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "com.haijunwei.native_kit/screen_brightness_control", binaryMessenger: registrar.messenger())
        let instance = ScreenBrightnessControl()
        instance.channel = channel
        registrar.addMethodCallDelegate(instance, channel: channel)
        registrar.addApplicationDelegate(instance)
    }
    
    override init() {
        super.init()
        NotificationCenter.default.addObserver(self, selector: #selector(brightnessDidChange(notification:)), name: UIScreen.brightnessDidChangeNotification, object: nil)
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if call.method == "setBrightness" {
            setBrightness(call, result: result)
        } else if call.method == "getBrightness" {
            getBrightness(result: result)
        } else if call.method == "record" {
            record(result: result)
        } else if call.method == "restore" {
            restore(result: result)
        } else if call.method == "setEnabledAutoKeep" {
            setEnabledAutoKeep(call, result: result)
        }
    }
    
    func getBrightness(result: @escaping FlutterResult) {
        result(UIScreen.main.brightness)
    }
    
    func setBrightness(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        let arguments = call.arguments as? [String : Any]
        let brightness = arguments?["brightness"] as? Double ?? 0
        UIScreen.main.brightness = CGFloat(brightness)
        result(nil)
    }
    
    func record(result: @escaping FlutterResult) {
        recordBrightness = UIScreen.main.brightness
        result(nil)
    }
    
    func restore(result: @escaping FlutterResult) {
        if let brightness = recordBrightness {
            UIScreen.main.brightness = brightness
        }
        result(nil)
    }
    
    func setEnabledAutoKeep(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        enabledAutoKeep = (call.arguments as? [String : Any])?["enabled"] as? Bool ?? false
        result(nil)
    }
    
    func onApplicationResume() {
        if !enabledAutoKeep { return }
        if let brightness = changedBrightness {
            UIScreen.main.brightness = brightness
        }
        changedBrightness = nil
    }
    
    func onApplicationPause() {
        if !enabledAutoKeep { return }
        if (changedBrightness == nil) {
            changedBrightness = UIScreen.main.brightness
        }
        if let brightness = recordBrightness {
            UIScreen.main.brightness = brightness
        }
    }
    
    @objc func brightnessDidChange(notification: NSNotification) {
        let brightness = UIScreen.main.brightness
        channel?.invokeMethod("brightnessDidChange", arguments: ["brightness": brightness])
    }
    
    // MARK: -
    
    
    func applicationDidEnterBackground(_ application: UIApplication) {
        onApplicationPause()
    }
    
    func applicationWillResignActive(_ application: UIApplication) {
        onApplicationPause()
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        onApplicationPause()
    }
    
    func applicationDidBecomeActive(_ application: UIApplication) {
        onApplicationResume()
    }
    
    func applicationWillEnterForeground(_ application: UIApplication) {
        onApplicationResume()
    }
}
