package currdig.utils;

import blockchain.utils.SecurityUtils;
import currdig.core.User;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

/**
 * Utility class containing methods for loading users, fetching network time,
 * and discovering P2P servers on the network.
 */
public class Utils {

    private static final String USER_DATA_PATH = "UsersData";

    /**
     * Loads all users from the filesystem by reading their public keys.
     *
     * @return A list of User objects.
     * @throws IOException If there's an issue reading user directories or
     * files.
     * @throws Exception If there's an issue converting keys.
     */
    public static List<User> loadUsers() throws IOException, Exception {
        List<User> users = new ArrayList<>();
        Path rootPath = Paths.get(USER_DATA_PATH);

        try (var paths = Files.walk(rootPath, 1)) {
            List<Path> userDirectories = paths
                    .filter(path -> Files.isDirectory(path) && !path.equals(rootPath))
                    .collect(Collectors.toList());

            for (Path userDir : userDirectories) {
                String username = userDir.getFileName().toString();
                Path publicKeyPath = userDir.resolve("public.key");

                if (Files.exists(publicKeyPath)) {
                    byte[] publicKeyBytes = Files.readAllBytes(publicKeyPath);
                    PublicKey pub = SecurityUtils.getPublicKey(publicKeyBytes);
                    users.add(new User(username, pub));  // Add user to list
                }
            }
        }

        return users;
    }

    /**
     * Fetches the current network time using an NTP server. Falls back to
     * system time if network request fails.
     *
     * @return The current time as a LocalDateTime object.
     */
    public static LocalDateTime fetchNetworkTime() {
        String ntpServer = "time.google.com"; // Public NTP server
        try {
            NTPUDPClient client = new NTPUDPClient();
            client.setDefaultTimeout(10000); // Set a timeout of 10 seconds
            InetAddress hostAddr = InetAddress.getByName(ntpServer);
            TimeInfo timeInfo = client.getTime(hostAddr);
            long ntpTime = timeInfo.getMessage().getTransmitTimeStamp().getTime(); // Time in milliseconds
            return Instant.ofEpochMilli(ntpTime).atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (IOException ex) {
            // Fallback to system time in case of failure
            return LocalDateTime.now();
        }
    }

    /**
     * Discovers P2P servers by sending broadcast messages on a specified port
     * and listening for responses.
     *
     * @return A list of unique server addresses discovered on the network.
     */
    public static List<String> discoverServers() {
        Set<String> uniqueServers = new HashSet<>();  // Use a Set to ensure unique servers
        int[] portsToCheck = {12345}; // List of ports to check
        DatagramSocket socket = null;

        try {
            socket = new DatagramSocket();  // Create a single socket to send requests
            socket.setSoTimeout(5000); // Timeout after 5 seconds
            InetAddress broadcastAddress = InetAddress.getByName("255.255.255.255");

            // Loop through each port to send discovery requests
            for (int port : portsToCheck) {
                try {
                    // Send broadcast request to discover peers on this port
                    String discoveryMessage = "DISCOVER_P2P_NODE";
                    DatagramPacket requestPacket = new DatagramPacket(discoveryMessage.getBytes(), discoveryMessage.length(), broadcastAddress, port);
                    socket.send(requestPacket);
                    System.out.println("Broadcasting discovery message on port " + port + "...");

                    // Listen for responses on the current port
                    long endTime = System.currentTimeMillis() + 5000; // Wait for 5 seconds for responses
                    while (System.currentTimeMillis() < endTime) {
                        byte[] buffer = new byte[256];
                        DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                        try {
                            socket.receive(responsePacket);
                            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
                            if (response.startsWith("P2P Node:")) {
                                uniqueServers.add(response.substring(10).trim());  // Add to the Set (duplicates are automatically removed)
                            }
                        } catch (SocketTimeoutException e) {
                            break; // No more responses, exit loop
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Error while trying to discover servers on port " + port + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            // Handle outer exceptions, e.g., socket creation failure
            System.out.println("Error creating socket for server discovery: " + e.getMessage());
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();  // Ensure the socket is closed after operation
            }
        }

        // Convert the Set of unique servers back to a List and return it
        return new ArrayList<>(uniqueServers);
    }
}
