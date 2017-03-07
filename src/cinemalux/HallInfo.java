package cinemalux;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HallInfo {//информация о зале
    public static final int MAX_SIZE = 20;//максимальный размер матрицы(зал представлен в виде матрицы)
    public static final byte FREE = 0;//свободное место
    public static final byte BUSY = 1;//место занято
    public static final byte BOOKED = 2;//забронировано
    public static final byte UNUSED = 3;//не используется
    private byte[] hallState;//массив значений
    private int movingPictureShowID;//идентификатор киносеанса

    public HallInfo(int movingPictureShowID) throws SQLException, ClassNotFoundException {
        this.movingPictureShowID = movingPictureShowID;
        DataBaseAdapter adapter = DataBaseAdapter.getAdapter();
        String sql = "SELECT `HallState` FROM `HallInfo` WHERE `MovingPictureShowID` = ?";//выполняем запрос
        PreparedStatement selectStatement = adapter.prepareStatement(sql);
        selectStatement.setInt(1, movingPictureShowID);
        ResultSet result = selectStatement.executeQuery();
        result.next();
        hallState = result.getBytes("HallState");//получаем состояние зала
    }
//начальное заполнение зала
    public static byte[] getFill(int movingPictureShowID) throws SQLException, ClassNotFoundException {
        byte[] hallState = new byte[MAX_SIZE * MAX_SIZE];
        for (int i = 0; i < hallState.length; i++)
            hallState[i] = UNUSED;
        DataBaseAdapter adapter = DataBaseAdapter.getAdapter();
        String sql = "SELECT `HallID` FROM `MovingPictureShow` WHERE `ID` = ?";//получаем номер зала
        PreparedStatement selectStatement = adapter.prepareStatement(sql);
        selectStatement.setInt(1, movingPictureShowID);
        ResultSet result = selectStatement.executeQuery();
        result.next();
        int hallID = result.getInt("HallID");//получили
        selectStatement.close();
        for (Hall hall : Hall.getHallList())//получаем информацию о рядах
            if (hall.getID() == hallID)
                for (Row row : hall.getRows()) {
                    int num = row.getNumber();//номер ряда
                    int count = row.getCount();//кол-во мест
                    for (int i = 1; i <= count; i++)
                        hallState[num * MAX_SIZE + i] = FREE;//заполняем матрицу

                }
        return hallState;
    }

    public void update() throws SQLException, ClassNotFoundException {
        DataBaseAdapter adapter = DataBaseAdapter.getAdapter();
        //запрос на обновление состояния зала
        String sql = "UPDATE `HallInfo` SET `HallState` = ? WHERE `MovingPictureShowID` = ?";
        PreparedStatement updateStatement = adapter.prepareStatement(sql);
        updateStatement.setBytes(1, hallState);
        updateStatement.setInt(2, movingPictureShowID);
        updateStatement.executeUpdate();
        updateStatement.close();
    }

    public byte[] getHallState() {
        return hallState;
    }

    public void setHallState(byte[] hallState) {
        this.hallState = hallState;
    }

    public int getMovingPictureShowID() {
        return movingPictureShowID;
    }

    public void setMovingPictureShowID(int movingPictureShowID) throws SQLException, ClassNotFoundException {
        this.movingPictureShowID = movingPictureShowID;

        /*for (int i = 0; i < HallInfo.MAX_SIZE; i++) {
            for (int j = 0; j < HallInfo.MAX_SIZE; j++)
                System.out.print(data[i * HallInfo.MAX_SIZE + j] + " ");
            System.out.println();
        }*/

    }
//задаем состояние для каого-либо места
    public void setHallState(int row, int place, byte state) throws SQLException, ClassNotFoundException {
        hallState[row * MAX_SIZE + place] = state;
        update();
    }
}
