package connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import security.SecurityService;
import storage.StorageService;

public class JCloudServer {
    private final int port;
    private SecurityService securityService;
    private StorageService storageService;

    public JCloudServer(int port, SecurityService securityService, StorageService storageService) {
        this.port = port;
        this.securityService = securityService;
        this.storageService = storageService;
    }

    public void start() {

        try (var executor = Executors.newVirtualThreadPerTaskExecutor();
                ServerSocket serverSocket = new ServerSocket(port)) {

            System.out.println("Servidor escuchando en puerto " + port);

            while (true) {
                Socket client = serverSocket.accept();

                executor.submit(() -> handleClient(client));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handleClient(Socket client) {

        try (client;

                var in = new DataInputStream(client.getInputStream());
                var out = new DataOutputStream(client.getOutputStream())) {

            byte operation = in.readByte();
            long userId = in.readLong();
            long payloadSize = in.readLong();

            System.out.printf("Log: Usuario %d solicita Operación %d (%d bytes)%n",
                    userId, operation, payloadSize);

            switch (operation) {
                case ProtocolConstants.OP_UPLOAD -> {
                    handleUpload(in, userId, payloadSize);

                    out.writeByte(0x00);
                }
                case ProtocolConstants.OP_DOWNLOAD -> {
                    handleDownload(in, out, userId);
                }
                default -> {
                    System.err.println("Operación no reconocida: " + operation);
                    out.writeByte(0x01);
                }
            }

        } catch (IOException e) {
            System.err.println("Error de comunicación con el cliente: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error interno en el procesamiento: " + e.getMessage());
        }

    }

    private void handleUpload(DataInputStream socketIn, long userId, long fileSize) throws Exception {

        String fileName = socketIn.readUTF();

        Path userPath = storageService.getPathForUserFile(userId, fileName);

        byte[] iv = securityService.generateIV();

        Cipher encryptor = securityService.getCipher(Cipher.ENCRYPT_MODE, iv);

        try (
                FileOutputStream fileOut = new FileOutputStream(userPath.toFile());
                CipherOutputStream cipherOut = new CipherOutputStream(fileOut, encryptor);
            ) 
            {

            fileOut.write(iv);

            byte[] buffer = new byte[8192];
            int bytesRead;
            int totalRead = 0;
            long remaining = fileSize;

            while (remaining > 0
                    && (bytesRead = socketIn.read(buffer, 0, (int) Math.min(buffer.length, remaining))) != -1) {

                cipherOut.write(buffer, 0, bytesRead);
                remaining -= bytesRead;
                totalRead += bytesRead;
            }

            if (totalRead < fileSize) {
                System.err.println("Error: Conexión interrumpida. Archivo incompleto.");

                cipherOut.close();
                fileOut.close();
            
                Files.deleteIfExists(userPath);
            }

        }
        System.out.println("Archivo guardado y encriptado en: " + userPath);
    }

    private void handleDownload(DataInputStream in, DataOutputStream out, long userId) throws Exception {
        String fileName = in.readUTF();

        Path filePath = storageService.getPathForUserFile(userId, fileName);

        if (!Files.exists(filePath)) {
            out.writeLong(-1);
            return;
        }

        try (FileInputStream fis = new FileInputStream(filePath.toFile())) {

            byte[] iv = new byte[12];
            int bytesReadIv = fis.read(iv);
            if (bytesReadIv < 12) {
                throw new IOException("Archivo corrupto o incompleto (falta el IV)");
            }

            Cipher decryptor = securityService.getCipher(Cipher.DECRYPT_MODE, iv);

            long realFileSize = Files.size(filePath) - 12;

            out.writeLong(realFileSize);

            try (
                CipherInputStream cis = new CipherInputStream(fis, decryptor);
                ) 
                { 

                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = cis.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            out.flush();
        }
    }
}