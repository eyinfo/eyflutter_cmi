import 'package:eyflutter_cmi/utils/plugin_utils.dart';
import 'package:eyflutter_core/eyflutter_core.dart';
import 'package:flutter/material.dart';
import 'package:flutter_localizations/flutter_localizations.dart';

mixin OnLauncherListener {
  /// 构建启动路由
  MaterialPageRoute generateRoute(RouteSettings settings);

  /// 未知路由配置
  MaterialPageRoute onUnknownRoute(RouteSettings settings);

  /// 应该本地语言回调
  Locale localeResCall(Locale deviceLocale, Iterable<Locale> supportedLocales);
}

class LauncherWidget extends MaterialApp {
  final bool isPlugin;
  final OnLauncherListener launcherListener;

  LauncherWidget({this.isPlugin = false, this.launcherListener})
      : super(
          theme: ThemeData(
            primaryColor: Colors.white,
          ),
          debugShowCheckedModeBanner: false,
          routes: RouteUtils.instance.routeWidgets,
          onGenerateRoute: (settings) => launcherListener?.generateRoute(settings),
          navigatorObservers: [
            CloudRouteObserver.instance,
          ],
          onUnknownRoute: (settings) => launcherListener?.onUnknownRoute(settings),
          localeResolutionCallback: (deviceLocale, supportedLocals) {
            return launcherListener?.localeResCall(deviceLocale, supportedLocals);
          },
          localizationsDelegates: [
            LangLocalizationsDelegate(),
            GlobalMaterialLocalizations.delegate,
            GlobalWidgetsLocalizations.delegate,
            GlobalCupertinoLocalizations.delegate,
          ],
          supportedLocales: LangManager.instance.supportedLocale,
        ) {
    PluginUtils.instance.setRunModule(isPlugin: isPlugin);
  }
}
