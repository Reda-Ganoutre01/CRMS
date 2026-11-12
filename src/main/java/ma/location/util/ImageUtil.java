package ma.location.util;

import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

/** Enregistre et supprime les photos des véhicules. */
public class ImageUtil {

    // Dossier où les photos

    public static final String DOSSIER = System.getProperty("user.home") + File.separator + "location-images";

    // Enregistre la photo envoyée par le formulaire et retourne son nom de fichier
    public static String sauvegarder(Part part) throws IOException {

        String nomOriginal = part.getSubmittedFileName().toLowerCase();
        String extension = nomOriginal.substring(nomOriginal.lastIndexOf('.') + 1);


        if (!extension.equals("jpg") && !extension.equals("jpeg")
                && !extension.equals("png") && !extension.equals("webp")) {
            throw new IllegalArgumentException("Image invalide : jpg, png ou webp seulement.");
        }

        new File(DOSSIER).mkdirs();


        String nom = UUID.randomUUID() + "." + extension;

        try (InputStream in = part.getInputStream()) {
            Files.copy(in, Path.of(DOSSIER, nom));
        }
        return nom;
    }

    // Supprime une photo du disque (ne fait rien si le nom est vide)
    public static void supprimer(String nom) {
        if (nom != null && !nom.isEmpty()) {
            new File(DOSSIER, new File(nom).getName()).delete();
        }
    }
}