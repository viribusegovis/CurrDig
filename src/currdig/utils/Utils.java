package currdig.utils;

import blockchain.utils.SecurityUtils;
import currdig.core.User;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

public class Utils {

    private static final String USER_DATA_PATH = "UsersData";

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

                    users.add(new User(username, pub));
                }
            }
        }

        return users;
    }

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
}
