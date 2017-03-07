package cinemalux;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Auth {
    private Auth() {
    }
    //авторизация
    public static Employee Authorize(String login, String password) throws SQLException, ClassNotFoundException {

        DataBaseAdapter adapter = DataBaseAdapter.getAdapter();
        String sql = "SELECT * FROM `Employee` WHERE `Login` = ? AND `Password` = ?";
        PreparedStatement authStatement = adapter.prepareStatement(sql);
        authStatement.setString(1, login);
        authStatement.setString(2, password);
        ResultSet result = authStatement.executeQuery();

        Employee employee;

        if (result.next()) {//если нашли что-то
            employee = new Employee();
            employee.setName(result.getString("Name"));
            employee.setID(result.getInt("ID"));
            employee.setLogin(result.getString("Login"));
            employee.setPassword(result.getString("Password"));
            employee.setEmployeeTypeID(result.getInt("EmployeeTypeID"));
            employee.setPoints(result.getInt("Points"));
            result.close();
            return employee;//вернем инфу о том кого нашли
        } else//иначе бросим исключение
            throw new SQLException("Incorrect login or password");
    }


}
