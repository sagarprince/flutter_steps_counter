# flutter_steps_counter

This plugin allows for conitnuous step count using the built-in pedometer sensor API's of iOS and Android devices.

## Getting Started
To use this plugin, add `flutter_steps_counter` as a [dependency in your pubspec.yaml file](https://flutter.io/platform-plugins/).

## Configuring XCode for usage on iOS

It seems that users of this plug-in will have to manually open XCode and configure a few settings manually, mostly pertaining to privacy settings and permissions due to the application collecting the user's movement data.

For usage on Android it seems there are no problems with permissions.

## Common iOS Flutter Errors

### Fix: Enable @objc inference
![7jcq5](https://user-images.githubusercontent.com/9467047/43827445-21326694-9afa-11e8-8e0c-60e829eb4c79.png)

## Errors only visible when running through XCode
![screen shot 2018-08-07 at 16 04 31](https://user-images.githubusercontent.com/9467047/43827142-6e0b8f00-9af9-11e8-80b6-f01b5db33713.png)

### Fix: Configure plist and capabilities
#### Step 1: Open XCode
Open the XCode project located at `<your_project>/iOS/Runner.xcodeproj`

#### Step 2: Set Capabilities
![screen shot 2018-08-08 at 10 09 11](https://user-images.githubusercontent.com/9467047/43827207-902101f6-9af9-11e8-8341-d399ece490f6.png)

#### Step 3: Configure your plist
![screen shot 2018-08-08 at 11 07 21](https://user-images.githubusercontent.com/9467047/43827874-3bd9a970-9afb-11e8-80bb-c9ec25b026c3.png)

