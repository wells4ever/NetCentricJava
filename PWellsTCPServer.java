import java.io.*;
import java.net.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PWellsTCPServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(9999)) {
            System.out.println("Waiting for client on port 9999...");

            while (true) {
                Socket connectionSocket = serverSocket.accept();
                System.out.println("Just connected to " + connectionSocket.getRemoteSocketAddress());

                // Receive file information
                DataInputStream in = new DataInputStream(connectionSocket.getInputStream());
                String fileName = in.readUTF();
                long fileSize = in.readLong(); // in bytes

                byte[] fileData = new byte[(int) fileSize];
                in.readFully(fileData); // Read the file data

                // Compute SHA256 hash
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = digest.digest(fileData);
                String hash = Base64.getEncoder().encodeToString(hashBytes); // Encode hash to Base64

                // Print file size in bits and hash code
                System.out.println("Received file size in bits = " + (fileSize * 8));
                System.out.println("Received file SHA256 hash: " + hash);

                // Send the hash back to the client
                DataOutputStream out = new DataOutputStream(connectionSocket.getOutputStream());
                out.writeUTF(hash);

                connectionSocket.close();
                System.out.println();
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
