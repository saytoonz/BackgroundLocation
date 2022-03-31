import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter/scheduler.dart';
import 'package:flutter/services.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MyHomePage(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({Key? key}) : super(key: key);

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  static const methodChannel =
      MethodChannel("com.sayt.background_location/method");

  _invokeMethodChannel() async {
    try {
      dynamic invoked = await methodChannel.invokeMethod("requestPermissions");
      print(invoked);
    } on PlatformException catch (e) {
      print(e);
    }
  }

  _startLocationService() async {
    try {
      dynamic invoked = await methodChannel.invokeMethod(
        "startLocationService",
        {"postUrl": "http://127.0.0.1:8000/api/save-location/12"},
      );
      print(invoked);
    } on PlatformException catch (e) {
      print(e);
    }
  }

  _stopLocationService() async {
    try {
      dynamic invoked = await methodChannel.invokeMethod("stopLocationService");
      print(invoked);
    } on PlatformException catch (e) {
      print(e);
    }
  }

  @override
  void initState() {
    super.initState();
    SchedulerBinding.instance?.addPostFrameCallback((_) {
      _invokeMethodChannel();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Row(),
          ElevatedButton(
            onPressed: () {
              _startLocationService();
            },
            child: const Text("Start Service"),
          ),
          const SizedBox(
            height: 10,
          ),
          ElevatedButton(
            onPressed: () {
              _stopLocationService();
            },
            child: const Text("Stop Service"),
          ),
        ],
      ),
    );
  }
}
