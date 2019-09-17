import 'dart:async';

import 'package:flutter/services.dart';

class FlutterStepsCounter {
  static const EventChannel _eventChannel =
  const EventChannel("steps.eventChannel");

  Stream<int> _stream;

  Stream<int> get stepCountStream {
    if (_stream == null) {
      _stream = _eventChannel.receiveBroadcastStream().map((stepsCount) => stepsCount);
    }
    return _stream;
  }

}
