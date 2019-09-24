package com.prince.flutter_steps_counter_example;

import android.os.Bundle;
import android.util.Log;

import io.flutter.app.FlutterActivity;
import io.flutter.plugins.GeneratedPluginRegistrant;

import com.prince.flutter_steps_counter.FlutterStepsCounterPlugin;

public class MainActivity extends FlutterActivity {
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    GeneratedPluginRegistrant.registerWith(this);
  }

  @Override
  protected void onDestroy() {
    Log.d("STEPS", "DESTROY");
    FlutterStepsCounterPlugin.onDestroy();
    super.onDestroy();
  }
}
