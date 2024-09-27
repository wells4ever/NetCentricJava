import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PWellsTCPClient {
    public static void main(String[] args) {
        String serverIp = args[0];
        String filePath = args[1];

        if (args.length != 2) {
            System.out.println("Usage: java TCPClient <server_ip> <file_path>");
            return;
        }

        

        try (Socket socket = new Socket(serverIp, 9999)) {
            System.out.println("Connecting to " + serverIp + " on port 9999");
            System.out.println("Just connected to " + socket.getRemoteSocketAddress());

            // Read the file and compute its SHA256 hash
            byte[] fileData = Files.readAllBytes(Paths.get(filePath));
            long fileSize = fileData.length; // in bytes

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(fileData);
            String hash = Base64.getEncoder().encodeToString(hashBytes); // Encode hash to Base64

            // Send file name and size to the server
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(filePath);  // Send the file name
            out.writeLong(fileSize);  // Send the file size in bytes
            out.write(fileData);       // Send the actual file data

            // Start the timer
            long startTime = System.currentTimeMillis();

            // Receive the hash from the server
            DataInputStream in = new DataInputStream(socket.getInputStream());
            String serverHash = in.readUTF();

            // Stop the timer
            long endTime = System.currentTimeMillis();
            double timeTaken = endTime - startTime; // in milliseconds
            double timeInSeconds = timeTaken/1000;

            // Verify the hash
            if (serverHash.equals(hash)) {
                System.out.println("Successfully sent!");
            } else {
                System.out.println("Error!");
            }

            // Calculate throughput
            double throughput = (fileSize * 8.0) / (timeInSeconds/2);
            double throughputInMbps = throughput / 1000000; //Convert throughput from bps to Mbps
            System.out.printf("File name: %s%n", filePath);
            System.out.printf("SHA256 hash: %s%n", hash);
            System.out.printf("File size in bits = %d%n", fileSize * 8);
            System.out.printf("Time taken (approx. one way) = %.2f ms%n", timeTaken);
            System.out.printf("Throughput = %.2f Mbps%n", throughputInMbps);

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
