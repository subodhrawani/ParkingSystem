package com.gojek.parking.handlers;

import com.gojek.parking.models.Parking;
import com.gojek.parking.models.ParkingSlot;
import com.gojek.parking.models.Car;
import com.gojek.parking.models.Ticket;

import java.sql.Timestamp;
import java.util.*;


public class EntryHandler {

    private ArrayList<ParkingSlot> parkingSlots;
    private HashMap<String, Set<Car>> carsColourMap = new HashMap<String, Set<Car>>();
    private HashMap<String, Set<ParkingSlot>> parkingColourMap = new HashMap<String, Set<ParkingSlot>>();
    private HashMap<Integer, Parking> parkingMap = new HashMap<Integer, Parking>();

    public EntryHandler(ArrayList<ParkingSlot> parkings, HashMap<String, Set<Car>> carsColourMap,
                 HashMap<String, Set<ParkingSlot>> parkingColourMap, HashMap<Integer, Parking> parkingTicketMap){
        this.parkingSlots = parkings;
        this.carsColourMap = carsColourMap;
        this.parkingColourMap = parkingColourMap;
        this.parkingMap = parkingTicketMap;
    }

    /**
     *
     * @return
     */
    public ParkingSlot fetchNearestAvailableSlot() {
        for (ParkingSlot slot : parkingSlots){
            if (slot.isAvailable())
                return slot;
        }
        return null;
    }

    /**
     *
     * @param car
     * @param slot
     * @return
     */
    public Ticket allocateParking(final Car car, final ParkingSlot slot) {
        if (slot == null || car == null)
            return null;
        try {
            synchronized (slot){
                while (!slot.isAvailable()){
                    slot.wait();
                }
                slot.setIsAvailable(false);
                updateMaps(car, slot);
                Ticket ticket = createTicket(car, slot);
                slot.notifyAll();
                return ticket;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     *
     * @param car
     * @param slot
     */
    private void updateMaps(final Car car, final  ParkingSlot slot){
        Parking parking = new Parking();
        parking.setCar(car);
        parking.setSlot(slot);
        parkingMap.put(slot.getSlotNumber(), parking);
        String colour = car.getColour();
        if (carsColourMap.get(colour) == null){
            Set<Car> carSet = new HashSet<Car>();
            carSet.add(car);
            carsColourMap.put(colour, carSet);
        } else carsColourMap.get(colour).add(car);

        if (parkingColourMap.get(car.getColour()) == null){
            Set<ParkingSlot> parkingSet  = new HashSet<ParkingSlot>();
            parkingSet.add(slot);
            parkingColourMap.put(colour, parkingSet);
        } else parkingColourMap.get(colour).add(slot);

    }

    /**
     *
     * @param car
     * @param slot
     * @return
     */
    private Ticket createTicket(final Car car, final ParkingSlot slot) {
        Ticket ticket = new Ticket();
        ticket.setTicketId(UUID.randomUUID().toString().replaceAll("-", ""));
        Parking parking = new Parking();
        parking.setCar(car);
        parking.setSlot(slot);
        ticket.setParking(parking);
        ticket.setBookingTime(new Timestamp(new Date().getTime()));
        return ticket;
    }
}
