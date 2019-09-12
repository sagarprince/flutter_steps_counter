import 'dart:async';

import 'package:flutter/services.dart';

class FlutterStepsCounter {
  static const MethodChannel _methodchannel =
  const MethodChannel('pedometer.methodChannel');

  static const EventChannel _eventChannel =
  const EventChannel("pedometer.eventChannel");

  Stream<int> _pedometerStream;

  static Future<bool> get isSensorPresent async {
    return await _methodchannel.invokeMethod('isSensorPresent');
  }

  Stream<int> get stepCountStream {
    if (_pedometerStream == null) {
      _pedometerStream =
          _eventChannel.receiveBroadcastStream().map((stepCount) => stepCount);
    }
    return _pedometerStream;
  }

}
