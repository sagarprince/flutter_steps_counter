package com.prince.flutter_steps_counter;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
//import android.util.Log;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * SensorsPlugin
 */
public class FlutterStepsCounterPlugin implements EventChannel.StreamHandler, StepListener {

  private static final String STEP_COUNT_EVENT_CHANNEL_NAME =
          "steps.eventChannel";

//  final public String TAG = "FLUTTER_STEP_PLUGIN";

  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
    final EventChannel eventChannel =
            new EventChannel(registrar.messenger(), STEP_COUNT_EVENT_CHANNEL_NAME);
    eventChannel.setStreamHandler(
            new FlutterStepsCounterPlugin(registrar.context()));
  }

  private SensorEventListener sensorEventListener;
  private final SensorManager sensorManager;
  private final Sensor sensor;

  private StepDetector stepDetector;
  private long stepsCount = 0;
  private long lastStepsCount = 0;
  boolean isActivity = true;

  private FlutterStepsCounterPlugin(Context context) {
    sensorManager = (SensorManager) context.getSystemService(context.SENSOR_SERVICE);
    sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
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
  public void onCancel(Object arguments) {
    if (sensor != null) {
      sensorManager.unregisterListener(sensorEventListener);
    }
  }

  @Override
  public void step(long num) {
    stepsCount += num;
    if (stepsCount - lastStepsCount > 10) {
      lastStepsCount = stepsCount;
    }
//    Log.d(TAG, "STEP COUNT : " + stepsCount);
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