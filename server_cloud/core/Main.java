package core;

import connection.JCloudServer;
import security.SecurityService;
import storage.StorageService;

public class Main {
    public static void main(String[] args) {
        String rutaStorage = "/home/storage_jcloud";

        SecurityService ses = new SecurityService();

        StorageService sts = new StorageService(rutaStorage);

        JCloudServer server = new JCloudServer(8083, ses, sts);
        server.start();
    }
}
