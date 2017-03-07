package cinemalux;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Hall {
    private int ID;//номер зала
    private String name;//название
    private ArrayList<Row> rows;//ряды

    public Hall(int ID) throws SQLException, ClassNotFoundException {
        this.ID = ID;
        rows = new ArrayList<Row>();
        DataBaseAdapter adapter = DataBaseAdapter.getAdapter();
        String sql = "SELECT * FROM `Hall` WHERE `ID` = ?";//грузим инфу о залах из БД
        PreparedStatement selectStatement = adapter.prepareStatement(sql);
        selectStatement.setInt(1, ID);
        ResultSet result = selectStatement.executeQuery();
        result.next();
        name = result.getString("Name");
        selectStatement.close();

        sql = "SELECT * FROM `Row` WHERE `HallID` = ?";//грузим инфу о рядах
        selectStatement = adapter.prepareStatement(sql);
        selectStatement.setInt(1, ID);
        result = selectStatement.executeQuery();
        while (result.next()) {
            Row row = new Row();
            row.setHallID(ID);
            row.setCostPercent(result.getFloat("CostPercent"));
            row.setCount(result.getInt("Count"));
            row.setNumber(result.getInt("Number"));
            rows.add(row);//заполняем список рядами
        }
        selectStatement.close();
    }

    //вернем список залов
    public static ArrayList<Hall> getHallList() throws SQLException, ClassNotFoundException {
        DataBaseAdapter adapter = DataBaseAdapter.getAdapter();
        String sql = "SELECT * FROM `Hall`";//список залов
        PreparedStatement selectStatement = adapter.prepareStatement(sql);
        ArrayList<Hall> halls = new ArrayList<Hall>();

        ResultSet result = selectStatement.executeQuery();
        while (result.next()) {
            Hall hall = new Hall(result.getInt("ID"));
            hall.setName(result.getString("Name"));
            halls.add(hall);//добавляем в список
        }

        selectStatement.close();
        return halls;
    }

    public String[] getStringCost(float cost) {
        String[] result = new String[3];
        int[] groups = new int[3];
        float current = rows.get(0).getCostPercent();
        int j = 0;
        groups[0] = 1;
        for (int i = 0; i < rows.size(); i++)
            if (rows.get(i).getCostPercent() != current) {
                j++;
                groups[j] = i;
                current = rows.get(i).getCostPercent();
            }

        groups[2] = rows.size();

        result[0] = "Ряд " + groups[0] + " - " + groups[1] +
                " Цена " + cost / 100.0f * rows.get(groups[0]).getCostPercent() +
                " грн";
        result[1] = "Ряд " + (groups[1] + 1) + " - " + (groups[2] - 1) +
                " Цена " + cost / 100.0f * rows.get(groups[1] + 1).getCostPercent() +
                " грн";
        result[2] = "Ряд " + (groups[2]) +
                " Цена " + cost / 100.0f * rows.get(groups[2] - 1).getCostPercent() +
                " грн";
        return result;

    }

    @Override
    public String toString() {
        return name;
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

    public ArrayList<Row> getRows() {
        return rows;
    }

    public void setRows(ArrayList<Row> rows) {
        this.rows = rows;
    }
}
