package com.arsenal.framework.model.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @author Gordon.Gan
 */
public class NetworkUtils {
    private static final Logger log= LoggerFactory.getLogger(NetworkUtils.class);

    public static final String ipAddress = getIpAddress();

    private static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (inetAddress instanceof Inet4Address && !inetAddress.isLoopbackAddress()
                            && !inetAddress.isLinkLocalAddress() && !inetAddress.isAnyLocalAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error while getting IP address.", e);
        }
        return "127.0.0.1";
    }

    /**
     * Get the hostname of the current machine.
     *
     * @return The hostname.
     */
    public static final String hostname = getHostname();

    private static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.warn("Get hostname failed.", e);
        }
        return "localhost";
    }

    /**
     * Get a free port number.
     *
     * @return The free port number.
     */
    public static int getFreePort() {
        try {
            ServerSocket serverSocket = new ServerSocket(0);
            int localPort = serverSocket.getLocalPort();
            serverSocket.close();
            return localPort;
        } catch (Exception e) {
            log.warn("Get free port failed.", e);
        }
        return 0;
    }

    /**
     * Extension function to parse socket address from a string.
     *
     * @return The InetSocketAddress object.
     */
    public InetSocketAddress parseSocketAddress(String addressString) {
        String[] items = addressString.split(":");
        assert (items.length == 2);

        String host = items[0];
        int port = Integer.parseInt(items[1]);
        return new InetSocketAddress(host, port);
    }
}
