import com.gojek.parking.ParkingManager;
import com.gojek.parking.models.Car;
import com.gojek.parking.models.ParkingSlot;
import com.gojek.parking.models.Ticket;
import com.gojek.parking.service.ParkingService;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Random;

public class ParkingTest {

    private static ParkingService parkingService;

    @BeforeClass
    public static void initialise(){
        parkingService = ParkingManager.getService();
    }

    @Test
    public void testCreateParking(){
        int  n = new Random().nextInt(50) + 1;
        int currentSize = parkingService.fetchAllSlots().size() + n;
        parkingService.addParkingSlots(n);
        Assert.assertTrue(parkingService.fetchAllSlots().size() == currentSize);
    }

    @Test
    public void testAllocateParking(){
        int  n = new Random().nextInt(50) + 1;
        parkingService.addParkingSlots(n);
        String registerationNo = "KA-01-HH-9999";
        String colour = "White";
        Car car = new Car();
        car.setRegisterationNo(registerationNo);
        car.setColour(colour);
        ParkingSlot slot = parkingService.fetchNearestAvailableSlot();
        Ticket ticket = parkingService.allocateParking(car, slot);
        Assert.assertTrue(ticket != null);
        Assert.assertTrue(ticket.getParking().getSlot().getSlotNumber() == slot.getSlotNumber());
        Assert.assertTrue(ticket.getParking().getCar().getRegisterationNo().equals(registerationNo));
        Assert.assertTrue(ticket.getParking().getCar().getColour().equals(colour));
    }

    @Test
    public void tesReleaseParking(){
        int  n = new Random().nextInt(50) + 1;
        parkingService.addParkingSlots(n);
        String registerationNo = "KA-01-HH-9999";
        String colour = "White";
        Car car = new Car();
        car.setRegisterationNo(registerationNo);
        car.setColour(colour);
        ParkingSlot slot = parkingService.fetchNearestAvailableSlot();
        parkingService.allocateParking(car, slot);
        Assert.assertTrue(parkingService.releaseParking(slot.getSlotNumber()));
    }

    @Test
    public void testForParkedSlotWithColour(){

        int  n = new Random().nextInt(50) + 1;
        parkingService.addParkingSlots(n);
        String registerationNo = "KA-01-HH-9999";
        String colour = "White";
        Car car = new Car();
        car.setRegisterationNo(registerationNo);
        car.setColour(colour);
        ParkingSlot slot = parkingService.fetchNearestAvailableSlot();
        parkingService.allocateParking(car, slot);

        List<Car> cars = parkingService.fetchCarsByColour(colour);
        Assert.assertEquals(cars.get(0).getColour(), colour);
    }

    @Test
    public void testForParkedSlotWithRegisterationNo(){

        int  n = new Random().nextInt(50) + 1;
        parkingService.addParkingSlots(n);
        String registerationNo = "KA-01-HH-9999";
        String colour = "White";
        Car car = new Car();
        car.setRegisterationNo(registerationNo);
        car.setColour(colour);
        ParkingSlot slot = parkingService.fetchNearestAvailableSlot();
        parkingService.allocateParking(car, slot);

        List<ParkingSlot> slots = parkingService.fetchParkingsByColour(colour);
        for (ParkingSlot parkingSlot : slots){
            if (parkingSlot.getSlotNumber() == slot.getSlotNumber())
                return;
        }

        //didn't find the allocated slot
        Assert.assertTrue(false);
    }

    @AfterClass
    public static void clearParkings(){
        List<ParkingSlot> slots = parkingService.fetchAllSlots();
        for (ParkingSlot slot : slots){
            parkingService.releaseParking(slot.getSlotNumber());
        }

    }
}
