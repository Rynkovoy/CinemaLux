package cinemalux;


import java.sql.SQLException;

public class AdminHandler {//обработчик событий админа
    private static AdminHandler instance;
    private Employee employee = null;

    private AdminHandler(Employee employee) {
        this.employee = employee;
    }

    public static AdminHandler getInstance(Employee employee) throws SQLException, ClassNotFoundException {
        if (instance == null)
            instance = new AdminHandler(employee);
        if (employee.getEmployeeTypeID() == EmployeeType.getTypeMap().get("Администратор"))
            return instance;
        return null;
    }
//добавить сотрудника
    public void addUser(Employee employee) throws SQLException, ClassNotFoundException {
        employee.insert();
    }
//добавить сеанс
    public void addMovingPictureShow(MovingPictureShow movingPictureShow) throws SQLException, ClassNotFoundException {
        movingPictureShow.insert();
    }
}
