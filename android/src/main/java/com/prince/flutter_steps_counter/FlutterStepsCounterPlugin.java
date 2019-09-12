package com.prince.flutter_steps_counter;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * SensorsPlugin
 */
public class FlutterStepsCounterPlugin implements MethodCallHandler, EventChannel.StreamHandler {
  private static final String STEP_COUNT_METHOD_CHANNEL_NAME =
          "pedometer.methodChannel";

  private static final String STEP_COUNT_EVENT_CHANNEL_NAME =
          "pedometer.eventChannel";

  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), STEP_COUNT_METHOD_CHANNEL_NAME);
    channel.setMethodCallHandler(new FlutterStepsCounterPlugin(registrar.context(), Sensor.TYPE_STEP_COUNTER));

    final EventChannel eventChannel =
            new EventChannel(registrar.messenger(), STEP_COUNT_EVENT_CHANNEL_NAME);
    eventChannel.setStreamHandler(
            new FlutterStepsCounterPlugin(registrar.context(), Sensor.TYPE_STEP_COUNTER));
  }

  private SensorEventListener sensorEventListener;
  private final SensorManager sensorManager;
  private final Sensor sensor;

  @TargetApi(Build.VERSION_CODES.CUPCAKE)
  private FlutterStepsCounterPlugin(Context context, int sensorType) {
    sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
    sensor = sensorManager.getDefaultSensor(sensorType);
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("isSensorPresent")) {
      if (sensor != null) {
        result.success(true);
      } else {
        result.success(false);
      }
    } else {
      result.notImplemented();
    }
  }

  @TargetApi(Build.VERSION_CODES.CUPCAKE)
  @Override
  public void onListen(Object arguments, EventChannel.EventSink events) {
    if (sensor != null) {
      sensorEventListener = createSensorEventListener(events);
      sensorManager.registerListener(sensorEventListener, sensor, sensorManager.SENSOR_DELAY_FASTEST);
    }
  }

  @TargetApi(Build.VERSION_CODES.CUPCAKE)
  @Override
  public void onCancel(Object arguments) {
    if (sensor != null) {
      sensorManager.unregisterListener(sensorEventListener);
    }
  }

  SensorEventListener createSensorEventListener(final EventChannel.EventSink events) {
    return new SensorEventListener() {
      @Override
      public void onAccuracyChanged(Sensor sensor, int accuracy) {
      }

      @TargetApi(Build.VERSION_CODES.CUPCAKE)
      @Override
      public void onSensorChanged(SensorEvent event) {
        int stepCount = (int) event.values[0];
        events.success(stepCount);
      }
    };
  }
}