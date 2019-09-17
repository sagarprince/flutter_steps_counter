import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter_steps_counter/flutter_steps_counter.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _stepCountValue = 'Unknown';
  StreamSubscription<int> _subscription;

  @override
  void initState() {
    super.initState();
    setUpPedometer();
  }

  void setUpPedometer() async {
    FlutterStepsCounter stepsCounter = new FlutterStepsCounter();

    _subscription = stepsCounter.stepCountStream.listen(_onData,
        onError: _onError, onDone: _onDone, cancelOnError: true);
  }

  void _onData(int stepCountValue) async {
    setState(() => _stepCountValue = "$stepCountValue");
  }

  void _onDone() => print("Finished Steps Counter Tracking");

  void _onError(error) => print("Flutter Steps Counter Error: $error");

  void _onCancel() => _subscription.cancel();

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Steps Counter'),
        ),
        body: Center(
          child: Text('Steps : $_stepCountValue\n'),
        ),
      ),
    );
  }

  @override
  void dispose() {
    _onCancel();
    super.dispose();
  }
}
