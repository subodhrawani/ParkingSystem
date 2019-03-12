package com.gojek.parking;


import com.gojek.parking.service.ParkingService;
import com.gojek.parking.service.impl.ParkingServiceImpl;

public class ParkingManager {

    public static ParkingService parkingService;

    public static ParkingService getService(){
        if (parkingService == null){
            synchronized(ParkingManager.class){
                if (parkingService == null)
                    parkingService = new ParkingServiceImpl();
            }
        }
        return parkingService;
    }
}
