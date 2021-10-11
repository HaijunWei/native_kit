//
//  VolumeControl.swift
//  native_kit
//
//  Created by Haijun on 2021/7/7.
//

import Foundation
import Flutter
import AVKit
import MediaPlayer

public class VolumeControl: NSObject, FlutterPlugin {
    let volumeView = MPVolumeView()
    var volumeSlider: UISlider?
    var channel: FlutterMethodChannel?
    private var isHideUI = false
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "com.haijunwei.native_kit/volume_control", binaryMessenger: registrar.messenger())
        let instance = VolumeControl()
        instance.channel = channel
        registrar.addMethodCallDelegate(instance, channel: channel)
    }
    
    override init() {
        super.init()
        UIApplication.shared.beginReceivingRemoteControlEvents();
        for item in volumeView.subviews {
            if item is UISlider {
                volumeSlider = (item as! UISlider)
                break
            }
        }
        volumeSlider?.addTarget(self, action: #selector(volumeDidChange), for: .valueChanged)
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if call.method == "getVolume" {
            getVolume(result: result)
        } else if call.method == "setVolume" {
            setVolume(call, result: result)
        } else if call.method == "hideUI" {
            hideUI(call, result: result)
        }
    }
    
    func getVolume(result: @escaping FlutterResult) {
        do {
            try AVAudioSession.sharedInstance().setActive(true)
            result(AVAudioSession.sharedInstance().outputVolume)
        } catch let error as NSError {
            result(FlutterError(code: String(error.code), message: "\(error.localizedDescription)", details: "\(error.localizedDescription)"))
        }
    }
    
    func setVolume(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        let arguments = call.arguments as? [String : Any]
        let volume = arguments?["volume"] as? Double ?? 0
        volumeSlider?.setValue((Float)(volume), animated: false)
        volumeDidChange()
        result(nil)
    }
    
    func hideUI(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        let arguments = call.arguments as? [String : Any]
        let hide = arguments?["hide"] as? Bool ?? false
        if isHideUI == hide { return }
        if hide {
            volumeView.frame = CGRect(x: -1000, y: -1000, width: 1, height: 1)
            volumeView.showsRouteButton = false
            UIApplication.shared.delegate!.window!?.rootViewController!.view.addSubview(volumeView)
        } else {
            volumeView.removeFromSuperview()
        }
        isHideUI = hide
        result(nil)
    }
    
    @objc func volumeDidChange() {
        let volume = volumeSlider?.value ?? 0
        channel?.invokeMethod("volumeDidChange", arguments: ["volume": volume])
    }
}
