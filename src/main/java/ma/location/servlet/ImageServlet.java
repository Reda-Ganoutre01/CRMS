package ma.location.servlet;

import ma.location.util.ImageUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/** Envoie une photo au navigateur : /images/nom-du-fichier.jpg */
@WebServlet("/images/*")
public class ImageServlet extends HttpServlet {

    private String mimeType(String fileName) {
        String type = getServletContext().getMimeType(fileName);
        if (type != null) {
            return type;
        }
        if (fileName.endsWith(".svg")) {
            return "image/svg+xml";
        }
        if (fileName.endsWith(".png")) {
            return "image/png";
        }
        if (fileName.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/jpeg";
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String chemin = req.getPathInfo(); // exemple : /3f2a....jpg
        if (chemin == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String nomFichier = new File(chemin).getName();

        // 1. Chercher d'abord dans les photos uploadées (ImageUtil.DOSSIER)
        File fichier = new File(ImageUtil.DOSSIER, nomFichier);
        if (fichier.exists()) {
            resp.setContentType(mimeType(fichier.getName()));
            resp.setHeader("Cache-Control", "max-age=86400");
            Files.copy(fichier.toPath(), resp.getOutputStream());
            return;
        }

        // 2. Chercher dans les images statiques de l'application (ex: logo.png, favicon.png)
        try (InputStream in = getServletContext().getResourceAsStream("/images/" + nomFichier)) {
            if (in != null) {
                resp.setContentType(mimeType(nomFichier));
                resp.setHeader("Cache-Control", "max-age=86400");
                in.transferTo(resp.getOutputStream());
                return;
            }
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}