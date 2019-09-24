import 'dart:async';

import 'package:flutter/services.dart';

class StepDetails {
  final String title;
  final String steps;
  StepDetails(this.title, this.steps);

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'title': this.title,
      'steps': this.steps
    };
  }
}

class FlutterStepsCounter {
  static const EventChannel _eventChannel = const EventChannel("steps.eventChannel");
  static const MethodChannel _methodChannel = const MethodChannel("steps.methodChannel");

  Stream<int> _stream;

  Stream<int> get stepCountStream {
    if (_stream == null) {
      _stream = _eventChannel.receiveBroadcastStream().map((stepsCount) => stepsCount);
    }
    return _stream;
  }

  static void setStepsDetails(StepDetails details) {
    _methodChannel.invokeMethod('setStepsDetails', details.toMap());
  }

}
