/* 
* RK: This utility Uses Haversine formula as its base. 
* Calculate distance between two points in latitude and longitude,also considering the height differences. 
* (If you are not interested in height difference pass 0.0) 
* lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters 
* el2 End altitude in meters 
*/ 
public static double calculateDistance(double lat1, double lat2, double lon1, double lon2, 
double el1, double el2) { 

final double EARTH_RADIUS = 6371.00; // Radius in Kilometers default For miles, divide km by 1.609344 

double latDistance = deg2rad(lat2 - lat1); 
double lonDistance = deg2rad(lon2 - lon1); 

double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) 
+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) 
* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2); 

double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)); 

double distance = EARTH_RADIUS * c * 1000; // convert to meters 
double height = el1 - el2; 
distance = Math.pow(distance, 2) + Math.pow(height, 2); 
return Math.sqrt(distance); 
} 

private static double deg2rad(double deg) { 
return (deg * Math.PI / 180.0); 
} 


}
