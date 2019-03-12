package com.gojek.parking.service;

import com.gojek.parking.models.Car;
import com.gojek.parking.models.Parking;
import com.gojek.parking.models.ParkingSlot;
import com.gojek.parking.models.Ticket;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface ParkingService {

    void addParkingSlots(int n);
    ParkingSlot fetchNearestAvailableSlot();
    ArrayList<Parking> fetchParkings();
    Ticket allocateParking(final Car car, final ParkingSlot slot);
    boolean releaseParking(int slotNo);
    List<Car> fetchCarsByColour(final String colour);
    ParkingSlot fetchParkingSlot(final Car car);
    List<ParkingSlot> fetchParkingsByColour(final String colour);
    List<ParkingSlot> fetchAllSlots();
}
