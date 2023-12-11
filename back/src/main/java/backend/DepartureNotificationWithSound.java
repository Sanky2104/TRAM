package backend;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.awt.Toolkit;

public class DepartureNotificationWithSound {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        PriorityQueue<Notification> notificationQueue = new PriorityQueue<>();

        System.out.print("Enter the departure time (24hr format): ");
        String departureTimeStr = scanner.nextLine();

        try {
            Date departureTime = dateFormat.parse(departureTimeStr);

            // Calculate the time to notify (5 minutes before departure)
            Date notificationTime = new Date(departureTime.getTime() - 5 * 60 * 1000);

            // Create a notification object and add it to the priority queue
            Notification notification = new Notification(notificationTime, "5 minutes left for the vehicle to leave.");
            notificationQueue.add(notification);

            // Simulate triggering notifications based on priority
            while (!notificationQueue.isEmpty()) {
                Notification nextNotification = notificationQueue.poll();
                Date currentTime = new Date();
                if (nextNotification.getNotificationTime().after(currentTime)) {
                    // Wait until it's time to trigger the notification
                    long delay = nextNotification.getNotificationTime().getTime() - currentTime.getTime();
                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                playNotificationSound();
                System.out.println(nextNotification.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Invalid time format. Please use HH:mm format.");
        }

        scanner.close();
    }

    private static void playNotificationSound() {
        Toolkit.getDefaultToolkit().beep();
    }
}

class Notification implements Comparable<Notification> {
    private Date notificationTime;
    private String message;

    public Notification(Date notificationTime, String message) {
        this.notificationTime = notificationTime;
        this.message = message;
    }

    public Date getNotificationTime() {
        return notificationTime;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public int compareTo(Notification other) {
        return this.notificationTime.compareTo(other.notificationTime);
    }
}
