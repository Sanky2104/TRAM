package backend;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map;

public class VehicleDataStorage {
    public static void main(String[] args) {
        Connection connection = null;
        try {
            // Replace these placeholders with your MySQL connection details
            String url = "jdbc:mysql://root@localhost:3306/vehicleinfo";
            String username = "root";
            String password = "Bobo2104@";

            connection = DriverManager.getConnection(url, username, password);
            initializeDatabase(connection);

            Map<LocalTime, VehicleInfo> vehicleData = new TreeMap<>();
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("Enter vehicle number (or type 'exit' to quit):");
                String vehicleNumber = scanner.nextLine();

                if (vehicleNumber.equalsIgnoreCase("exit")) {
                    break; // Exit the loop if the user enters 'exit'
                }

                // Fetch current date
                LocalDate currentDate = LocalDate.now();

                System.out.println("Enter departure time (in 24-hour format, e.g., 14:30):");
                String departureTimeStr = scanner.nextLine();

                System.out.println("Enter vehicle location:");
                String location = scanner.nextLine();

                VehicleInfo info = createVehicleInfo(vehicleNumber, currentDate, departureTimeStr, location);
                if (info != null) {
                    vehicleData.put(info.getDepartureTime(), info);
                    saveVehicleToDatabase(connection, info);
                    System.out.println("Vehicle data added for " + vehicleNumber);
                } else {
                    System.out.println("Invalid time format. Please enter a valid time (e.g., 14:30).");
                }
            }

            System.out.println("Vehicle data entered and sorted (by departure time):");
            for (Map.Entry<LocalTime, VehicleInfo> entry : vehicleData.entrySet()) {
                System.out.println("Departure Time: " + entry.getKey());
                System.out.println("Departure Date: " + entry.getValue().getDepartureDate());
                System.out.println("Vehicle Number: " + entry.getValue().getVehicleNumber());
                System.out.println("Location: " + entry.getValue().getLocation());
            }

            scanner.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static VehicleInfo createVehicleInfo(String vehicleNumber, LocalDate currentDate, String departureTimeStr,
            String location) {
        try {
            LocalTime departureTime = LocalTime.parse(departureTimeStr);
            return new VehicleInfo(vehicleNumber, currentDate, departureTime, location);
        } catch (Exception e) {
            return null;
        }
    }

    public static void initializeDatabase(Connection connection) throws SQLException {
        // Create a table to store vehicle data if it doesn't exist
        Statement statement = connection.createStatement();
        String createTableQuery = "CREATE TABLE IF NOT EXISTS vehicles (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "vehicleNumber VARCHAR(255), " +
                "departureDate DATE, " +
                "departureTime TIME, " +
                "location VARCHAR(255));";
        statement.executeUpdate(createTableQuery);
    }

    private static void saveVehicleToDatabase(Connection connection, VehicleInfo vehicle) throws SQLException {
        // Insert a vehicle record into the database
        String insertQuery = "INSERT INTO vehicles (vehicleNumber, departureDate, departureTime, location) VALUES (?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
        preparedStatement.setString(1, vehicle.getVehicleNumber());
        preparedStatement.setDate(2, Date.valueOf(vehicle.getDepartureDate()));
        preparedStatement.setTime(3, Time.valueOf(vehicle.getDepartureTime()));
        preparedStatement.setString(4, vehicle.getLocation());
        preparedStatement.executeUpdate();
    }
}