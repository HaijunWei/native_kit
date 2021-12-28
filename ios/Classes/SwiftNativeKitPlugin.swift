import Flutter
import UIKit

public class SwiftNativeKitPlugin: NSObject, FlutterPlugin {
  public static func register(with registrar: FlutterPluginRegistrar) {
    VolumeControl.register(with: registrar)
    ScreenBrightnessControl.register(with: registrar)
    ClipboardExtension.register(with: registrar)
  }
}
