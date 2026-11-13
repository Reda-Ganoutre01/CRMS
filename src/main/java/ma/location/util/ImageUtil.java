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

    /**
     * Enregistre la photo envoyée par le formulaire dans le dossier indiqué
     * et retourne son nom de fichier (sans chemin).
     *
     * @param part   La partie multipart contenant l'image
     * @param dossier Le chemin absolu du dossier de destination (ex: getRealPath("/images/cars"))
     */
    public static String sauvegarder(Part part, String dossier) throws IOException {
        String nomOriginal = part.getSubmittedFileName().toLowerCase();
        String extension = nomOriginal.substring(nomOriginal.lastIndexOf('.') + 1);

        if (!extension.equals("jpg") && !extension.equals("jpeg")
                && !extension.equals("png") && !extension.equals("webp")) {
            throw new IllegalArgumentException("Image invalide : jpg, png ou webp seulement.");
        }

        new File(dossier).mkdirs();

        String nom = UUID.randomUUID() + "." + extension;

        try (InputStream in = part.getInputStream()) {
            Files.copy(in, Path.of(dossier, nom));
        }
        return nom; // retourne juste le nom du fichier (ex: "abc123.jpg")
    }

    /**
     * Supprime une photo du dossier images/cars dans la webapp.
     *
     * @param nom    Le nom du fichier (ex: "abc123.jpg") ou null/vide pour ignorer
     * @param dossier Le chemin absolu du dossier (ex: getRealPath("/images/cars"))
     */
    public static void supprimer(String nom, String dossier) {
        if (nom != null && !nom.isEmpty() && dossier != null) {
            new File(dossier, new File(nom).getName()).delete();
        }
    }
}