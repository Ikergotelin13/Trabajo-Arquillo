<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>
</head>
<body>
    <form action="/ValidationServlet" method="post">
        Usuario: <input type="text" name="usuario"><br>
        Contraseña: <input type="password" name="contrasena"><br>
        <input type="submit" value="Iniciar sesión">
    </form>

    <%-- Mostrar mensaje de error si existe --%>
    <c:if test="${param.error == 'true'}">
        <p style="color: red;">Credenciales incorrectas. Inténtelo de nuevo.</p>
    </c:if>
</body>
</html>

