package com.prince.flutter_steps_counter;

import android.content.Context;
import android.content.Intent;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import java.util.Map;

/**
 * SensorsPlugin
 */
public class FlutterStepsCounterPlugin implements EventChannel.StreamHandler, MethodCallHandler, StepListener {

  private final Registrar registrar;
  private static Context context;
  private static final String STEP_COUNT_EVENT_CHANNEL_NAME = "steps.eventChannel";
  private static final String STEP_COUNT_METHOD_CHANNEL_NAME = "steps.methodChannel";

  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
    final EventChannel eventChannel = new EventChannel(registrar.messenger(), STEP_COUNT_EVENT_CHANNEL_NAME);
    eventChannel.setStreamHandler(new FlutterStepsCounterPlugin(registrar));

    final MethodChannel methodChannel = new MethodChannel(registrar.messenger(), STEP_COUNT_METHOD_CHANNEL_NAME);
    methodChannel.setMethodCallHandler(new FlutterStepsCounterPlugin(registrar));
  }

  private static SensorEventListener sensorEventListener;
  private static SensorManager sensorManager;
  private static Sensor sensor;

  private StepDetector stepDetector;

  private static Intent notificationIntentService;

  private long stepsCount = 0;
  private long lastStepsCount = 0;
  boolean isActivity = true;

  private FlutterStepsCounterPlugin(Registrar registrar) {
    this.registrar = registrar;
    this.context = registrar.context();
    sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
    sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    notificationIntentService = new Intent(context, StepService.class);
  }

  @Override
  public void onListen(Object arguments, EventChannel.EventSink events) {
    if (sensor != null) {
      sensorEventListener = createSensorEventListener(events);
      stepDetector = new StepDetector(this);
      sensorManager.registerListener(sensorEventListener, sensor, sensorManager.SENSOR_DELAY_GAME);
    }
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("setStepsDetails")) {
      Map<String, Object> arguments = call.arguments();
      String title = (String) arguments.get("title");
      String steps = (String) arguments.get("steps");
      notificationIntentService.putExtra("title", title);
      notificationIntentService.putExtra("content", steps);
      context.startService(notificationIntentService);
      result.success(true);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onCancel(Object arguments) {
    onDestroy();
  }

  public static void onDestroy() {
    if (sensor != null) {
      sensorManager.unregisterListener(sensorEventListener);
      context.stopService(notificationIntentService);
    }
  }

  @Override
  public void step(long num) {
    stepsCount += num;
    if (stepsCount - lastStepsCount > 10) {
      lastStepsCount = stepsCount;
    }
    notificationIntentService.putExtra("content", Long.toString(stepsCount));
    context.startService(notificationIntentService);
  }

  SensorEventListener createSensorEventListener(final EventChannel.EventSink events) {
    return new SensorEventListener() {
      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {
      }

      @Override
      public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
          stepDetector.updateModel(isActivity);
          stepDetector.updateStep(event.values[0], event.values[1], event.values[2]);
          events.success(stepsCount);
        }
      }
    };
  }
}