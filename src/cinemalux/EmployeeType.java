package cinemalux;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class EmployeeType {//тип сотрудника
    private static EmployeeType instance = null;

    private static HashMap<String, Integer> employeeTypeMap;
    private static ArrayList<String> employeeTypeNameArray;

    private EmployeeType() throws SQLException, ClassNotFoundException {
        employeeTypeMap = new HashMap<String, Integer>();
        employeeTypeNameArray = new ArrayList<String>();
        DataBaseAdapter adapter = DataBaseAdapter.getAdapter();
        String sql = "SELECT * FROM `EmployeeType`";
        PreparedStatement typeStatement = adapter.prepareStatement(sql);
        ResultSet resultSet = typeStatement.executeQuery();
        while (resultSet.next()) {
            String name = resultSet.getString("Name");
            employeeTypeMap.put(name, resultSet.getInt("ID"));
            employeeTypeNameArray.add(name);
        }
        typeStatement.close();
    }

    public static HashMap<String, Integer> getTypeMap() throws SQLException, ClassNotFoundException {
        if (instance == null)
            instance = new EmployeeType();
        return employeeTypeMap;
    }

    public static String getTypeName(int ID) throws SQLException, ClassNotFoundException {
        if (instance == null)
            instance = new EmployeeType();
        return employeeTypeNameArray.get(ID - 1);
    }
}
