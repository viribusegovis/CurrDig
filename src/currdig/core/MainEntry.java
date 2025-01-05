package currdig.core;

import currdig.gui.Client;
import currdig.gui.Landing;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import p2p.NodeP2PGui;

/**
 * MainEntry class serves as the entry point for the application. It takes a
 * command-line argument to determine which GUI to launch.
 *
 * Usage: java -jar YourProject.jar [p2p|admin|student]
 */
public class MainEntry {

    /**
     * The main method that launches the appropriate GUI based on the argument
     * provided.
     *
     * @param args Command-line arguments where the first argument specifies the
     * GUI to launch.
     * @throws NotBoundException If the RMI registry does not contain a binding
     * for the specified name.
     * @throws MalformedURLException If the provided URL is malformed.
     */
    public static void main(String[] args) throws NotBoundException, MalformedURLException {
        // Check if at least one argument is provided
        if (args.length < 1) {
            System.out.println("Please provide the GUI name as an argument.");
            System.out.println("Usage: java -jar YourProject.jar [p2p|admin|student]");
            return;
        }

        // Retrieve the first argument to determine which GUI to launch
        String option = args[0];

        // Decide which GUI to open based on the provided argument
        switch (option.toLowerCase()) {
            case "p2p":
                // Launch the P2P GUI
                new NodeP2PGui().setVisible(true);
                break;
            case "admin":
                // Launch the Admin GUI (Landing)
                new Landing().setVisible(true);
                break;
            case "student":
                // Launch the Student GUI (Client)
                new Client().setVisible(true);
                break;
            default:
                // Handle unknown GUI options
                System.out.println("Unknown GUI: " + option);
                System.out.println("Usage: java -jar YourProject.jar [p2p|admin|student]");
                break;
        }
    }
}
