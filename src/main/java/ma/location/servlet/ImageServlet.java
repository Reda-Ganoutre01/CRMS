package ma.location.servlet;

import ma.location.util.ImageUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/** Envoie une photo au navigateur : /images/nom-du-fichier.jpg */
@WebServlet("/images/*")
public class ImageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String chemin = req.getPathInfo(); // exemple : /3f2a....jpg
        if (chemin == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // getName() garde seulement le nom du fichier (sécurité : pas de ../ possible)
        File fichier = new File(ImageUtil.DOSSIER, new File(chemin).getName());
        if (!fichier.exists()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String type = getServletContext().getMimeType(fichier.getName());
        if (type == null) {
            type = "image/jpeg";
        }
        resp.setContentType(type);
        resp.setHeader("Cache-Control", "max-age=86400"); // le navigateur garde l'image 1 jour
        Files.copy(fichier.toPath(), resp.getOutputStream());
    }
}