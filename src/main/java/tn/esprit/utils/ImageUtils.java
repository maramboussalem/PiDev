package tn.esprit.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ImageUtils {
    private static final String DEFAULT_IMAGE = "/images/default-medicament.png";
    private static final String IMAGE_DIR = "src/main/resources/images/medicaments/";
    private static final String RESOURCE_PATH = "/images/medicaments/";

    public static ImageView loadMedicamentImage(String imageName) {
        ImageView imageView = new ImageView();
        try {
            if (imageName == null || imageName.isEmpty()) {
                // Load default image from resources
                imageView.setImage(loadImageFromStream(DEFAULT_IMAGE));
                return imageView;
            }

            // Try loading from file system first (for development)
            Path filePath = Paths.get(IMAGE_DIR + imageName);
            if (Files.exists(filePath)) {
                imageView.setImage(new Image(filePath.toUri().toString()));
                return imageView;
            }

            // Try loading from resources (for packaged JAR)
            String resourcePath = RESOURCE_PATH + imageName;
            InputStream imageStream = ImageUtils.class.getResourceAsStream(resourcePath);
            if (imageStream != null) {
                imageView.setImage(new Image(imageStream));
                return imageView;
            }

            // Fallback to default image
            imageView.setImage(loadImageFromStream(DEFAULT_IMAGE));
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            imageView.setImage(loadImageFromStream(DEFAULT_IMAGE));
        }
        return imageView;
    }

    private static Image loadImageFromStream(String path) {
        InputStream stream = ImageUtils.class.getResourceAsStream(path);
        if (stream != null) {
            return new Image(stream);
        }
        return null;
    }
}