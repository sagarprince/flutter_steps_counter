import 'dart:async';

import 'package:flutter/services.dart';

class FlutterStepsCounter {
  static const MethodChannel _methodChannel =
  const MethodChannel('steps.methodChannel');

  static const EventChannel _eventChannel =
  const EventChannel("steps.eventChannel");

  Stream<int> _stream;

  static Future<bool> get isSensorPresent async {
    return await _methodChannel.invokeMethod('isSensorPresent');
  }

  Stream<int> get stepCountStream {
    if (_stream == null) {
      _stream = _eventChannel.receiveBroadcastStream().map((stepsCount) => stepsCount);
    }
    return _stream;
  }

}
