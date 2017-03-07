package cinemalux;

import java.io.File;
import java.sql.*;


public class DataBaseAdapter {
    public static final String dbname = "db.sqlite";//название БД
    private static DataBaseAdapter instance;//сущность
    private Connection connection = null;//соединение


    private DataBaseAdapter() throws ClassNotFoundException, SQLException {
        File db = new File(dbname);
        boolean exists = db.exists();//проверяем наличае БД
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + dbname);//получаем соединение

        //запрос для формирования БД
        String sql = "CREATE TABLE `Employee` (\n" +
                "\t`ID`\tINTEGER,\n" +
                "\t`Name`\tVARCHAR(60) NOT NULL,\n" +
                "\t`Login`\tINTEGER NOT NULL,\n" +
                "\t`Password`\tINTEGER NOT NULL,\n" +
                "\t`EmployeeTypeID`\tINTEGER,\n" +
                "\t`Points`\tINTEGER,\n" +
                "\tPRIMARY KEY(ID)\n" +
                ");\n" +
                "CREATE TABLE `EmployeeType` (\n" +
                "\t`ID`\tINTEGER,\n" +
                "\t`Name`\tVARCHAR(60) ,\n" +
                "\tPRIMARY KEY(ID)\n" +
                ");\n" +
                "CREATE TABLE `MovingPictureShow` (\n" +
                "\t`ID`\tINTEGER,\n" +
                "\t`MovieID` INTEGER,\n" +
                "\t`HallID`INTEGER,\n" +
                "\t`Title`\tTEXT,\n" +
                "\t`Time`\tINTEGER,\n" +
                "\t`Cost`\tREAL,\n" +
                "\tPRIMARY KEY(ID)\n" +
                ");\n" +
                "CREATE TABLE \"Hall\" (\n" +
                "\t`ID`\tINTEGER,\n" +
                "\t`Name`\tVARCHAR(60),\n" +
                "\tPRIMARY KEY(ID)\n" +
                ");\n" +
                "CREATE TABLE `Row` (\n" +
                "\t`HallID`\tINTEGER,\n" +
                "\t`Number`\tINTEGER,\n" +
                "\t`Count`\tINTEGER,\n" +
                "\t`CostPercent`\tREAL\n" +
                ");\n" +
                "CREATE TABLE `HallInfo` (\n" +
                "\t`MovingPictureShowID`\tINTEGER,\n" +
                "\t`HallState`\tBLOB\n" +
                ");\n" +
                "CREATE TABLE `MovieInfo` (\n" +
                "\t`ID` INTEGER,\n" +
                "\t`Title` TEXT,\n" +
                "\t`Description` TEXT,\n" +
                "\t`Trailer` TEXT,\n" +
                "\t`Cover` BLOB,\n" +
                "\tPRIMARY KEY(ID)\n" +
                ");\n" +
                "INSERT INTO `Hall`(`Name`)VALUES(\"Красный\");\n" +
                "INSERT INTO `Hall`(`Name`)VALUES(\"Синий\");\n" +
                "INSERT INTO `Row`(`HallID`,`Number`,`Count`,`CostPercent`)VALUES(1,1,14,100);\n" +
                "INSERT INTO `Row`(`HallID`,`Number`,`Count`,`CostPercent`)VALUES(1,2,14,100);\n" +
                "INSERT INTO `Row`(`HallID`,`Number`,`Count`,`CostPercent`)VALUES(1,3,14,100);\n" +
                "INSERT INTO `Row`(`HallID`,`Number`,`Count`,`CostPercent`)VALUES(1,4,14,100);\n" +
                "INSERT INTO `Row`(`HallID`,`Number`,`Count`,`CostPercent`)VALUES(1,5,12,120);\n" +
                "INSERT INTO `Row`(`HallID`,`Number`,`Count`,`CostPercent`)VALUES(1,6,12,120);\n" +
                "INSERT INTO `Row`(`HallID`,`Number`,`Count`,`CostPercent`)VALUES(1,7,12,120);\n" +
                "INSERT INTO `Row`(`HallID`,`Number`,`Count`,`CostPercent`)VALUES(1,8,12,120);\n" +
                "INSERT INTO `Row`(`HallID`,`Number`,`Count`,`CostPercent`)VALUES(1,9,16,150);\n" +
                "INSERT INTO `Row`(`HallID`,`Number`,`Count`,`CostPercent`)VALUES(2,1,10,100);\n" +
                "INSERT INTO `Row`(`HallID`,`Number`,`Count`,`CostPercent`)VALUES(2,2,10,100);\n" +
                "INSERT INTO `Row`(`HallID`,`Number`,`Count`,`CostPercent`)VALUES(2,3,10,100);\n" +
                "INSERT INTO `Row`(`HallID`,`Number`,`Count`,`CostPercent`)VALUES(2,4,12,120);\n" +
                "INSERT INTO `Row`(`HallID`,`Number`,`Count`,`CostPercent`)VALUES(2,5,12,120);\n" +
                "INSERT INTO `Row`(`HallID`,`Number`,`Count`,`CostPercent`)VALUES(2,6,12,120);\n" +
                "INSERT INTO `Row`(`HallID`,`Number`,`Count`,`CostPercent`)VALUES(2,7,16,150);\n" +
                "INSERT INTO `EmployeeType`(`Name`)VALUES(\"Администратор\");\n" +
                "INSERT INTO `EmployeeType`(`Name`)VALUES(\"Кассир\");\n" +
                "INSERT INTO `EmployeeType`(`Name`)VALUES(\"Зритель\");\n" +
                "INSERT INTO `Employee`(`Name`,`Login`,`Password`,`EmployeeTypeID`)\n" +
                "VALUES(\"Admin\",\"Admin\",\"Admin\",1);";

        if (!exists) {//если БД нет
            Statement createStatement = connection.createStatement();
            createStatement.executeUpdate(sql);//создадим БД
            createStatement.close();
        }


    }
    //паттерн синглтон
    public static DataBaseAdapter getAdapter() throws SQLException, ClassNotFoundException {
        if (instance == null)
            instance = new DataBaseAdapter();
        return instance;
    }
    //подготовка запроса
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        if (connection != null)
            return connection.prepareStatement(sql);
        return null;
    }
    //проверка наличая той или иной записи
    public boolean exists(String field, String value, String table) throws SQLException {
        String sql = "SELECT * FROM `" + table + "` WHERE `" + field + "` = ?";
        PreparedStatement existsStatement = this.prepareStatement(sql);
        existsStatement.setString(1, value);
        ResultSet resultSet = existsStatement.executeQuery();
        return resultSet.next();
    }
    //закрыть
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
            connection = null;
        }

    }
}
