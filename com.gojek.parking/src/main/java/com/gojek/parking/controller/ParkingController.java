package com.gojek.parking.controller;

import com.gojek.parking.ParkingManager;
import com.gojek.parking.commons.Constants;
import com.gojek.parking.models.Car;
import com.gojek.parking.models.Parking;
import com.gojek.parking.models.ParkingSlot;
import com.gojek.parking.models.Ticket;
import com.gojek.parking.service.ParkingService;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

public class ParkingController {

    private static ParkingService parkingService;

    public static void main(String args[]){
        try {
            parkingService = ParkingManager.getService();

            if (args.length == 2) {
                String inputFileName = args[0];
                String outputFileName = args[1];
                File inputFile = new File(inputFileName);
                File outputFile = new File(outputFileName);
                if (!outputFile.exists())
                    outputFile.createNewFile();
                if (inputFile.exists()) {
                    readFromFile(inputFile, outputFile);
                }
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String sCurrentLine = "";
                while (!"EXIT".equalsIgnoreCase(sCurrentLine = br.readLine())){
                    System.out.println(performAction(sCurrentLine));
                }
            }

        } catch (Exception e) {
            System.out.println("Exception while processing input" + e.getMessage());
        }
    }

    private static void readFromFile(File inputFile, File outputFile){
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile.getAbsoluteFile()));
            String sCurrentLine = "";
            while ((sCurrentLine = br.readLine()) != null){
                String output = performAction(sCurrentLine);
                bw.write(output + "\n");
                System.out.println(output);
            }
            bw.close();
        } catch (IOException e) {
            System.out.println("Exception while reading file " + e.getMessage());
        }

    }

    private static String performAction(String query) {
        String queries[] = query.split(" ");
        if (Constants.CREATE_PARKING.equals(queries[0])){
            return createParkings(Integer.parseInt(queries[1]));
        } else if (Constants.ALLOCATE_PARKING.equals(queries[0])){
            return allocateParking(queries[1], queries[2]);
        } else if (Constants.CARS_COLOUR_QUERY.equals(queries[0])){
            return displayCarsByColour(queries[1]);
        } else if (Constants.PARKING_COLOUR_QUERY.equals(queries[0])){
            return displayParkingByColour(queries[1]);
        } else if (Constants.PARKING_SLOT_QUERY.equals(queries[0])){
            return displayParkingSlot(queries[1]);
        } else if (Constants.PARKING_STATUS.equals(queries[0])){
            return displayParkingStatus();
        } else if (Constants.RELEASE_PARKING.equals(queries[0])){
            return releaseParking(Integer.parseInt(queries[1]));
        } else return "";
    }

    private static String createParkings(int n){
        try {
            parkingService.addParkingSlots(n);
            return "Created a parking lot with " + n +  " slots";
        } catch (Exception e) {
            return "Exception while creating parkings " + e.getMessage();
        }
    }

    private static String allocateParking(String registerNo, String colour){
        try {
            Car car = new Car();
            car.setRegisterationNo(registerNo);
            car.setColour(colour);
            Ticket ticket = parkingService.allocateParking(car, parkingService.fetchNearestAvailableSlot());
            if (ticket != null){
                return "Allocated slot number: " + (ticket.getParking().getSlot().getSlotNumber() + 1);
            } else {
                return "Sorry, parking lot is full";
            }
        } catch (Exception e) {
            return "Exception while allocating parking " + e.getMessage();
        }
    }

    private static String releaseParking(int slotNo){

        try {
            parkingService.releaseParking(slotNo - 1);
            return "Slot number " + slotNo + " is free";
        } catch (Exception e) {
            return "Exception while releasing parking " + e.getMessage();
        }
    }

    private static String displayParkingStatus(){
        ArrayList<Parking> parkings = parkingService.fetchParkings();
        String output = "Slot No.\t" + "Registration No\t" + "Colour";
        output += "\n";
        for (Parking parking : parkings){
            ParkingSlot slot = parking.getSlot();
            Car car = parking.getCar();
            output += (slot.getSlotNumber() + 1) + "\t" + car.getRegisterationNo() + "\t" + car.getColour();
            output += "\n";
        }
        return output;
    }

    private static String displayCarsByColour(String colour){
        List<Car> carSet = parkingService.fetchCarsByColour(colour);
        String cars = "";
        Iterator<Car> iterator = carSet.iterator();
        while (iterator.hasNext()){
            cars += iterator.next().getRegisterationNo() + ",";
        }
        return cars.substring(0, cars.length() - 1);
    }

    private static String displayParkingByColour(String colour){
        List<ParkingSlot> parkingSet = parkingService.fetchParkingsByColour(colour);
        String parkingList = "";
        Iterator<ParkingSlot> iterator = parkingSet.iterator();
        while (iterator.hasNext()){
            parkingList += iterator.next().getSlotNumber() + 1 + ",";
        }
        return parkingList.substring(0, parkingList.length() - 1);
    }

    private static String displayParkingSlot(String registerationNo){
        Car car = new Car();
        car.setRegisterationNo(registerationNo);
        ParkingSlot slot = parkingService.fetchParkingSlot(car);
        if (slot == null)
            return "Not found";
        else return slot.getSlotNumber() + 1 + "";
    }
}
