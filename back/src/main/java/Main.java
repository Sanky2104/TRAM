// import backend.DepartureNotificationWithSound;
import java.sql.SQLException;

import backend.*;
public class Main {
    public static void main(String[] args) {
        // DepartureNotificationWithSound obj = new DepartureNotificationWithSound();
        
        VehicleInfo obj = new VehicleInfo(null, null, null, null);
        try {
            System.out.println(obj.getVehicleNumber());
        } catch (SQLException e) {}
    }
}
