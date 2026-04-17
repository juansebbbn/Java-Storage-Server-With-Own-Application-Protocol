package storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StorageService {
    private final Path rootPath;

    public StorageService(String rootDirectory) {
        this.rootPath = Paths.get(rootDirectory).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.rootPath);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar el almacenamiento raíz", e);
        }
    }

    public Path getPathForUserFile(long userId, String fileName) throws IOException {

        String safeFileName = Paths.get(fileName).getFileName().toString();
        
        Path userDirectory = rootPath.resolve("user_" + userId).normalize();
        
        if (!Files.exists(userDirectory)) {
            Files.createDirectories(userDirectory);
        }

        return userDirectory.resolve(safeFileName);
    }

    public Path getUserDirectory(long userId) {
        return rootPath.resolve("user_" + userId).normalize();
    }
}