package currdig.utils;

import blockchain.utils.SecurityUtils;
import currdig.core.User;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

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
}
