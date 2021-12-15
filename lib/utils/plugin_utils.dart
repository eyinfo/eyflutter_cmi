import 'package:eyflutter_core/storage/memory_utils.dart';

class PluginUtils {
  factory PluginUtils() => _getInstance();

  static PluginUtils get instance => _getInstance();
  static PluginUtils _instance;

  static PluginUtils _getInstance() {
    _instance ??= new PluginUtils._internal();
    return _instance;
  }

  PluginUtils._internal();

  String _runModuleKey = "2bc4f54923c0408e";

  /// 设置当前运行模块
  /// [isPlugin] true-插件运行；false-主工程运行；
  void setRunModule({bool isPlugin = false}) {
    MemoryUtils.instance.set(_runModuleKey, isPlugin);
  }

  /// 获取当前运行模块
  /// [return] true-插件运行；false-主工程运行；
  bool get isRunPlugin => MemoryUtils.instance.getBool(_runModuleKey);
}
