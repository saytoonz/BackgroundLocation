import UIKit
import Flutter
import CoreLocation

@UIApplicationMain
@objc class AppDelegate: FlutterAppDelegate, CLLocationManagerDelegate {
    private var locationManager: CLLocationManager?
    private var postUrl : String?

  override func application(
    _ application: UIApplication,
    didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?
  ) -> Bool {
      
      let controller : FlutterViewController = window?.rootViewController as! FlutterViewController
      let methodChannel = FlutterMethodChannel(name: "com.sayt.background_location/method", binaryMessenger: controller.binaryMessenger)
      
      methodChannel.setMethodCallHandler({
          (call: FlutterMethodCall, result: @escaping FlutterResult) -> Void in
          
          
          switch call.method {
          case  "startLocationService":
              self.startLocationService()
              guard let args = call.arguments as? [String : Any] else {return}
              self.postUrl = (args["postUrl"] as! String)
              result("startLocationService")
              break
              
          case  "stopLocationService":
              self.stopLocationService()
              result("stopLocationService")
              break
              
          default:
              result(FlutterMethodNotImplemented)
          }
      })
      
      
      
      GeneratedPluginRegistrant.register(with: self)
    return super.application(application, didFinishLaunchingWithOptions: launchOptions)
  }
    
    func stopLocationService() {
        locationManager?.stopUpdatingLocation()
        locationManager?.stopUpdatingHeading()
        locationManager?.allowsBackgroundLocationUpdates = false
    }
    
    func startLocationService()  {
           locationManager = CLLocationManager()
           locationManager?.requestAlwaysAuthorization()
           locationManager?.startUpdatingLocation()
           locationManager?.startUpdatingHeading()
           locationManager?.delegate = self
           locationManager?.allowsBackgroundLocationUpdates = true
           
       }
       
       func locationManager(_ manager:CLLocationManager, didUpdateLocations locations: [CLLocation] ) {
           if let location = locations.last{
               if(postUrl == nil) {return}
               
                    
               // Create a URLRequest for an API endpoint
               let url = URL(string: postUrl!)!
               var request = URLRequest(url: url)
               
               // Configure request authentication
               request.setValue(
                   "application/json",
                   forHTTPHeaderField: "Content-Type"
               )
               
               // Serialize HTTP Body data as JSON
               let body: [String : AnyHashable] = [
                    "lat": location.coordinate.latitude,
                    "lng": location.coordinate.longitude,
                    "heading": location.course,
               ]
               let bodyData = try? JSONSerialization.data(
                   withJSONObject: body,
                   options: .fragmentsAllowed
               )

               // Change the URLRequest to a POST request
               request.httpMethod = "POST"
               request.httpBody = bodyData
               
               // Create the HTTP request
               let session = URLSession.shared
               let task = session.dataTask(with: request) { (data, _, error) in
                   guard let data = data, error == nil else {
                       return
                   }
                   
                   do{
                       let response = try JSONSerialization.jsonObject(with: data, options: .allowFragments)
                       print(response)
                   }catch{
                       print(error)
                   }

               }

               task.resume()
               
           }
       }
}
