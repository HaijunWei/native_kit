import Flutter
import UIKit
import AVKit
import MediaPlayer

public class SwiftNativeKitPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    VolumeControl.register(with: registrar)
  }
}


public class VolumeControl: NSObject, FlutterPlugin {
    let volumeView = MPVolumeView()
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
        NotificationCenter.default.addObserver(self, selector: #selector(volumeDidChange), name: NSNotification.Name(rawValue: "AVSystemController_SystemVolumeDidChangeNotification"), object: nil)
        UIApplication.shared.beginReceivingRemoteControlEvents();
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
        var slider: UISlider?
        for item in volumeView.subviews {
            if item is UISlider {
                slider = (item as! UISlider)
                break
            }
        }
        if slider == nil {
            result(FlutterError(code: "-1", message: "未获取到系统音量条", details: "未获取到系统音量条"))
            return
        }
        slider!.setValue((Float)(volume), animated: false)
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
    }
    
    @objc func volumeDidChange(notification: NSNotification) {
        let volume = notification.userInfo!["AVSystemController_AudioVolumeNotificationParameter"] as! Float
        channel?.invokeMethod("volumeDidChange", arguments: ["volume": volume])
    }
}
