#import "EyflutterCmiPlugin.h"
#if __has_include(<eyflutter_cmi/eyflutter_cmi-Swift.h>)
#import <eyflutter_cmi/eyflutter_cmi-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "eyflutter_cmi-Swift.h"
#endif

@implementation EyflutterCmiPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftEyflutterCmiPlugin registerWithRegistrar:registrar];
}
@end
