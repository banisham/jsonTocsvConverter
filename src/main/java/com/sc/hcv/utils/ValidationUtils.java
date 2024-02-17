package com.sc.hcv.utils;

import java.util.List;

public class ValidationUtils {

    // Method to validate a list of SourceServer objects
    public static void validateSourceServers(List<SourceServer> sourceServers) {
        for (SourceServer server : sourceServers) {
            // Validate IP address
            String ipAddress = server.getIpAddress();
            if (!isValidIpAddress(ipAddress)) {
                System.out.println("Invalid IP address format for server: " + server);
            }

            // Validate hostname
            String hostname = server.getHostName();
            if (!isValidHostname(hostname)) {
                System.out.println("Invalid hostname format for server: " + server);
            }
        }
    }

    // Method to validate IP address format
    private static boolean isValidIpAddress(String ipAddress) {
        // Add your IP address validation logic here
        // For simplicity, assuming IPv4 format
        // You can use regular expressions or InetAddress class for more robust validation
        return ipAddress.matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
    }

    // Method to validate hostname format
    private static boolean isValidHostname(String hostname) {
        // Add your hostname validation logic here
        // For simplicity, assuming valid FQDN format with alphanumeric characters, dashes, and dots
        // You can use regular expressions or InetAddress class for more robust validation
        return hostname.matches("[a-zA-Z0-9.-]+") && hostname.matches("[^.].*[^.]");
    }

}
