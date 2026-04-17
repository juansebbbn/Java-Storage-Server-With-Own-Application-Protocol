package cliente;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JCloudClient {
    private final String host;
    private final int port;

    public JCloudClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void uploadFile(long userId, String filePathString) {
        Path path = Paths.get(filePathString);
        
        if (!Files.exists(path)) {
            System.err.println("Error: El archivo local no existe.");
            return;
        }

        try (Socket socket = new Socket(host, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             FileInputStream fis = new FileInputStream(path.toFile())) {

            out.writeByte(1); 

            out.writeLong(userId);
            
            long fileSize = Files.size(path);
            out.writeLong(fileSize);
 
            String fileName = path.getFileName().toString();
            out.writeUTF(fileName);

            System.out.println("Subiendo: " + fileName + " (" + fileSize + " bytes)...");
                
            byte[] buffer = new byte[8192]; 
            int bytesRead;
        
            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);     
            }

            out.flush(); 
            System.out.println("¡Subida completada con éxito!");

        } catch (IOException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        JCloudClient client = new JCloudClient("localhost", 8083);
        client.uploadFile(77, "text.txt");
    }
}
