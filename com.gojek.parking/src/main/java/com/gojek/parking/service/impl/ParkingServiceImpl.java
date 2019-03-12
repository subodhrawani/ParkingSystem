package com.gojek.parking.service.impl;

import com.gojek.parking.handlers.EntryHandler;
import com.gojek.parking.handlers.ExitHandler;
import com.gojek.parking.models.Car;
import com.gojek.parking.models.Parking;
import com.gojek.parking.models.ParkingSlot;
import com.gojek.parking.models.Ticket;
import com.gojek.parking.service.ParkingService;

import java.security.Key;
import java.util.*;

public class ParkingServiceImpl implements ParkingService {

    private ArrayList<ParkingSlot> parkingSlots = new ArrayList<ParkingSlot>();
    private HashMap<String, Set<Car>> carsColourMap = new HashMap<String, Set<Car>>();
    private HashMap<String, Set<ParkingSlot>> parkingColourMap = new HashMap<String, Set<ParkingSlot>>();
    private HashMap<Integer, Parking> parkingMap = new HashMap<Integer, Parking>();

    private EntryHandler entryHandler;
    private ExitHandler exitHandler;

    public ParkingServiceImpl(){
        entryHandler = new EntryHandler(parkingSlots, carsColourMap, parkingColourMap, parkingMap);
        exitHandler = new ExitHandler(parkingSlots, carsColourMap, parkingColourMap, parkingMap);

    }

    /**
     *
     * @param n
     */
    public void addParkingSlots(int n) {
        int size = parkingSlots.size();
        for (int i = 0; i < n; i++){
            ParkingSlot slot = new ParkingSlot();
            slot.setIsAvailable(true);
            slot.setSlotNumber(size + i);
            parkingSlots.add(slot);
        }
    }

    /***
     *
     * @return
     */
    public ParkingSlot fetchNearestAvailableSlot() {
        return entryHandler.fetchNearestAvailableSlot();
    }

    /**
     *
     * @param car
     * @param slot
     * @return
     */
    public Ticket allocateParking(Car car, ParkingSlot slot) {
        return entryHandler.allocateParking(car, slot);
    }

    /**
     *
     * @param slotNo
     * @return
     */
    public boolean releaseParking(int slotNo) {
        return exitHandler.releaseParking(parkingMap.get(slotNo));
    }

    /**
     *
     * @param colour
     * @return
     */
    public List<Car> fetchCarsByColour(String colour) {
        Set<Car> set = carsColourMap.get(colour);
        List<Car> sortedList = new ArrayList<Car>(set);
        Collections.sort(sortedList, new Comparator<Car>() {
            public int compare(Car o1, Car o2) {
                return o1.getRegisterationNo().compareTo(o2.getRegisterationNo());
            }
        });
        return sortedList;
    }

    /**
     *
     * @param car
     * @return
     */
    public ParkingSlot fetchParkingSlot(Car car) {
        for (Integer key : parkingMap.keySet()){
            Parking parking = parkingMap.get(key);
            if (parking.getCar().getRegisterationNo().equalsIgnoreCase(car.getRegisterationNo()))
                return parking.getSlot();
        }
        return null;
    }

    /**
     *
     * @param colour
     * @return
     */
    public List<ParkingSlot> fetchParkingsByColour(String colour) {
        Set<ParkingSlot> set = parkingColourMap.get(colour);
        List<ParkingSlot> sortedList = new ArrayList<ParkingSlot>(set);
        Collections.sort(sortedList, new Comparator<ParkingSlot>() {
            public int compare(ParkingSlot o1, ParkingSlot o2) {
                return o1.getSlotNumber() - o2.getSlotNumber();
            }
        });
        return sortedList;
    }

    /**
     *
     * @return
     */
    public List<ParkingSlot> fetchAllSlots() {
        return parkingSlots;
    }

    /**
     *
     * @return
     */
    public ArrayList<Parking> fetchParkings(){
        ArrayList<Parking> parkings = new ArrayList<Parking>();
        for (Integer key : parkingMap.keySet()){
            parkings.add(parkingMap.get(key));
        }
        return parkings;
    }
}
