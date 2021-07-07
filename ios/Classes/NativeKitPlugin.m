#import "NativeKitPlugin.h"
#if __has_include(<native_kit/native_kit-Swift.h>)
#import <native_kit/native_kit-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "native_kit-Swift.h"
#endif

@implementation NativeKitPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftNativeKitPlugin registerWithRegistrar:registrar];
}
@end
