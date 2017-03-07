package cinemalux;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Employee {
    private int ID;
    private String name;
    private String login;
    private String password;
    private int employeeTypeID;

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    private int points;

    public Employee() throws SQLException, ClassNotFoundException {
        HashMap<String, Integer> typeMap = EmployeeType.getTypeMap();
        employeeTypeID = typeMap.get("Кассир");
    }


    @Override
    public String toString() {
        return "Employee{" +
                "ID=" + ID +
                ", name='" + name + '\'' +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", employeeTypeID=" + employeeTypeID +
                '}';
    }

    public void insert() throws SQLException, ClassNotFoundException {
        DataBaseAdapter adapter = DataBaseAdapter.getAdapter();
        if(adapter.exists("Login",login,"Employee"))
            throw new SQLException("User with login \'" + login + "\' exists");
        String sql = "INSERT INTO `Employee`(`Name`,`Login`,`Password`,`EmployeeTypeID`,`Points`)\n" +
                "VALUES(?,?,?,?,0);";
        PreparedStatement insertStatement = adapter.prepareStatement(sql);
        insertStatement.setString(1, name);
        insertStatement.setString(2, login);
        insertStatement.setString(3, password);
        insertStatement.setInt(4, employeeTypeID);
        insertStatement.executeUpdate();
        insertStatement.close();
    }


    public static ArrayList<Employee> getEmployees() throws SQLException, ClassNotFoundException {
        ArrayList<Employee> result = new ArrayList<>();
        DataBaseAdapter adapter = DataBaseAdapter.getAdapter();
        String sql = "SELECT * FROM `Employee`";
        PreparedStatement selectStatement = adapter.prepareStatement(sql);
        ResultSet resultSet = selectStatement.executeQuery();
        while(resultSet.next())
        {
            Employee e = new Employee();
            e.setID(resultSet.getInt("ID"));
            e.setEmployeeTypeID(resultSet.getInt("EmployeeTypeID"));
            e.setLogin(resultSet.getString("Login"));
            e.setName(resultSet.getString("Name"));
            result.add(e);
        }
        selectStatement.close();
        return result;
    }

    public static void remove(int id) throws SQLException, ClassNotFoundException {
        DataBaseAdapter adapter = DataBaseAdapter.getAdapter();
        String sql = "DELETE FROM `Employee` WHERE `ID` = ?";
        PreparedStatement removeStatement = adapter.prepareStatement(sql);
        removeStatement.setInt(1,id);
        removeStatement.executeUpdate();
    }

    public static void update(int id,int points) throws SQLException, ClassNotFoundException {
        DataBaseAdapter adapter = DataBaseAdapter.getAdapter();
        String sql = "UPDATE `Employee` SET `Points` = ? WHERE `ID` = ?";
        PreparedStatement updateStatement = adapter.prepareStatement(sql);
        updateStatement.setInt(1,points);
        updateStatement.setInt(2,id);
        updateStatement.executeUpdate();
    }

    public int getEmployeeTypeID() {
        return employeeTypeID;
    }

    public void setEmployeeTypeID(int employeeTypeID) {
        this.employeeTypeID = employeeTypeID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
