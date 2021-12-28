//
//  ClipboardExtension.swift
//  native_kit
//
//  Created by haijun on 2021/12/28.
//

import Foundation
import Flutter
import UIKit

class ClipboardExtension: NSObject, FlutterPlugin {
    var channel: FlutterMethodChannel?
    
    static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "com.haijunwei.native_kit/clipboard_extension", binaryMessenger: registrar.messenger())
        let instance = ClipboardExtension()
        instance.channel = channel
        registrar.addMethodCallDelegate(instance, channel: channel)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if call.method == "getChangeCount" {
            getChangeCount(result: result)
        }
    }
    
    func getChangeCount(result: @escaping FlutterResult) {
        result(UIPasteboard.general.changeCount)
    }
    
}
