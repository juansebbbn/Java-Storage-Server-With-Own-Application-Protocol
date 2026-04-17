import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/ver-archivos")
public class DashBoardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        String rutaStorage = "/home/storage_jcloud"; 
        
        File carpeta = new File(rutaStorage);
        File[] lista = carpeta.listFiles();

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html><head>");
            out.println("<title>J-Cloud Dashboard</title>");
            out.println("<style>");
            out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #1a1a1a; color: white; padding: 40px; }");
            out.println("table { width: 100%; border-collapse: collapse; margin-top: 20px; background: #2d2d2d; }");
            out.println("th, td { padding: 12px; border: 1px solid #444; text-align: left; }");
            out.println("th { background-color: #0078d4; color: white; }");
            out.println("tr:hover { background-color: #3d3d3d; }");
            out.println("h1 { color: #0078d4; }");
            out.println("</style></head><body>");
            
            out.println("<h1>🌐 J-Cloud Web Dashboard</h1>");
            out.println("<p>Explorando: <code>" + rutaStorage + "</code></p>");
            out.println("<table><tr><th>Nombre de la carpeta</th><th>Tamaño</th></tr>");

            out.println(lista[0]);

            if (lista != null && lista.length > 0) {
                for (File f : lista) {
                    String tamano = (f.length() / 1024) + " KB";
                    out.println("<tr><td>📄 " + f.getName() + "</td><td>" + tamano + "</td></tr>");
                }
            } else {
                out.println("<tr><td colspan='2'>No hay archivos o la ruta es incorrecta.</td></tr>");
            }

            out.println("</table></body></html>");
        }
    }
}