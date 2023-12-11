package backend;

import java.time.LocalDate;
import java.time.LocalTime;
import java.sql.*;

public class VehicleInfo {
    static Connection connection;
    // static PreparedStatement p ;
    // static ResultSet rs;
    public static void main(String[] args) {
        try {
            String url = "jdbc:mysql://root@localhost:3306/vehicleinfo";
            String username = "root";
            String password = "Bobo2104@";
            connection = DriverManager.getConnection(url, username, password);
        }catch(Exception e){e.printStackTrace();}
    }

    private String vehicleNumber;
    private LocalDate departureDate;
    private LocalTime departureTime;
    private String location;

    public VehicleInfo(String vehicleNumber, LocalDate departureDate, LocalTime departureTime, String location) {
        this.vehicleNumber = vehicleNumber;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.location = location;
    }

    public String getVehicleNumber() throws SQLException {
        if (connection == null) {
            throw new SQLException("Connection is not initialized. Call initializeConnection method first.");
        }
        String vehicle_no = "SELECT vehicleNumber FROM vehicles";
        // PreparedStatement p = connection.prepareStatement(vehicle_no);
        // rs = p.executeQuery();
        // if (rs.next()) {
        //     String vehicleNumber = rs.getString("vehicleNumber");
        //     return vehicleNumber;
        // } else {
        //     // Handle the case when no rows are returned
        //     return null; // or throw an exception, depending on your requirements
        // }
        try(PreparedStatement p = connection.prepareStatement(vehicle_no)){
            p.setString(1, location);
            ResultSet resultSet = p.executeQuery();

            if (resultSet.next()) {
                    // String vehicleNumber = resultSet.getString("vehicleNumber");
                    return resultSet.getString("vehicleNumber");
            } else {
                return "null";
            }
        }
    }
    

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public String getLocation() {
        return location;
    }
}