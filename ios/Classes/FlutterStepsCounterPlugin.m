#import "FlutterStepsCounterPlugin.h"
#import <CoreMotion/CoreMotion.h>

@implementation FlutterStepsCounterPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
    FLTPedometerStreamHandler* streamHandler = [[FLTPedometerStreamHandler alloc] init];
    FlutterEventChannel* eventChannel =
    [FlutterEventChannel eventChannelWithName:@"pedometer.eventChannel"
                              binaryMessenger:[registrar messenger]];
    [eventChannel setStreamHandler:streamHandler];
}

@end

CMPedometer* _pedometer;

void _initPedometerManager() {
    if (!_pedometer) {
        _pedometer = [[CMPedometer alloc]init];
    }
}

@implementation FLTPedometerStreamHandler

- (FlutterError*)onListenWithArguments:(id)arguments eventSink:(FlutterEventSink)eventSink {
    _initPedometerManager();
    NSDate *now = [NSDate date];
    NSCalendar *gregorian = [[NSCalendar alloc]initWithCalendarIdentifier:NSCalendarIdentifierGregorian];
    NSDateComponents *components = [gregorian components:(NSCalendarUnitYear | NSCalendarUnitMonth | NSCalendarUnitDay) fromDate:now];
    NSDate *from = [gregorian dateFromComponents:components];
    [_pedometer startPedometerUpdatesFromDate:from withHandler:^(CMPedometerData * _Nullable pedometerData, NSError * _Nullable error) {
        NSLog( @"From Date: '%@'", from );
        NSNumber *steps = 0;
        if ([CMPedometer isStepCountingAvailable]) {
            steps = pedometerData.numberOfSteps;
            eventSink(steps);
        }
        return;
    }];
    
    return nil;
}

- (FlutterError*)onCancelWithArguments:(id)arguments {
    [_pedometer stopPedometerUpdates];
    return nil;
}

@end
