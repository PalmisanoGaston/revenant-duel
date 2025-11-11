package red;

import java.net.InetAddress;

public class NetworkTest {
    public static void main(String[] args) {
        try {
            // Test if you can resolve localhost
            InetAddress localhost = InetAddress.getByName("localhost");
            System.out.println("Localhost IP: " + localhost.getHostAddress());
            
            // Test if you can get your actual IP
            InetAddress actualIP = InetAddress.getLocalHost();
            System.out.println("Actual IP: " + actualIP.getHostAddress());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}