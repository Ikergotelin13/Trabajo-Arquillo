import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/ValidationServlet")
public class ValidationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        /*  Obtiene parámetros del formulario */
        String usuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");

        /*  Valida credenciales */
        if (validarCredenciales(usuario, contrasena)) {
            /* Guardar información del usuario en la sesión */
            HttpSession session = request.getSession();
            session.setAttribute("usuario", usuario);

            /* Redireccionar a la página de bienvenida si las credenciales son válidas */
            response.sendRedirect("bienvenida.jsp");
        } else {
            /*  Volver a la página de login y mostrar mensaje de error si las credenciales no son válidas */
            response.sendRedirect("login.jsp?error=true");
        }
    }

    /*  Método para validar credenciales en la base de datos */
    private boolean validarCredenciales(String usuario, String contrasena) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            /* Aquí te conectas a la base de datos */
            String url = "jdbc:mysql://localhost:3306/final";
            String user = "root";
            String password = "";
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);

            /* Hash de la contraseña para que este haseada */
            String hashedContrasena = hashContrasena(contrasena);

            /* Aqui consultas la base de datos para validar las credenciales */
            String sql = "SELECT * FROM usuarios WHERE usuario = ? AND contrasena_hashed = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, usuario);
            preparedStatement.setString(2, hashedContrasena);
            resultSet = preparedStatement.executeQuery();

            /*En este apartado devuelve  true si se encontraron resultados (credenciales válidas) */
            return resultSet.next();
        } catch (ClassNotFoundException | SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /* Este es el método para hashear la contraseña */
    private String hashContrasena(String contrasena) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(contrasena.getBytes());

        /* En este apartado  coonvertimos el hash a formato hexadecimal para que lo pueda leer una persona */
        StringBuilder hexHash = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1) {
                hexHash.append('0');
            }
            hexHash.append(hex);
        }

        return hexHash.toString();
    }
}
