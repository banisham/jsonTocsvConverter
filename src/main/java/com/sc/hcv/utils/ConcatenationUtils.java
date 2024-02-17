package com.sc.hcv.utils;

import java.util.List;

public class ConcatenationUtils {

    // Method to concatenate IP addresses and hostnames from a list of SourceServer objects
    public static String concatenateAddresses(List<SourceServer> sourceServers) {
        StringBuilder result = new StringBuilder();

        for (SourceServer server : sourceServers) {
            // Append IP address
            String ipAddress = server.getIpAddress();
            if (ipAddress != null && ! ipAddress.isBlank()) {
                result.append(ipAddress).append(",");
            }

            // Append hostname
            String hostname = server.getHostName();
            if (hostname != null && !hostname.isBlank()) {
                result.append(hostname).append(",");
            }
        }

        // Remove the trailing comma, if any
        if (result.length() > 0) {
            result.setLength(result.length() - 1);
        }

        return result.toString();
    }
}

