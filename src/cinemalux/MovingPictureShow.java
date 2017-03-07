package cinemalux;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class MovingPictureShow {//киносеанс
    private int ID;//идентификатор
    private int hallID;//зал
    private int movieID;

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    private String title;//название
    private float cost;//стоимость
    private long time;//время показа

    public MovingPictureShow() {

    }

    public static long getTime(int day, int month, int year, int hour, int minutes) {//получаем время
        return new GregorianCalendar(year, month - 1, day, hour, minutes).getTimeInMillis();
    }

    //получаем список фильмов
    public static ArrayList<MovingPictureShow> getDatedList(long date) throws SQLException, ClassNotFoundException {
        Date d = new Date(date);
        DataBaseAdapter adapter = DataBaseAdapter.getAdapter();
        ArrayList<MovingPictureShow> movingPictureShows = new ArrayList<MovingPictureShow>();
        String sql = "SELECT * FROM `MovingPictureShow`";//берем список
        PreparedStatement selectStatement = adapter.prepareStatement(sql);
        ResultSet result = selectStatement.executeQuery();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        while (result.next())//получаем результат из БД
        {
            MovingPictureShow movingPictureShow = new MovingPictureShow();
            movingPictureShow.setCost(result.getFloat("Cost"));
            movingPictureShow.setHallID(result.getInt("HallID"));
            movingPictureShow.setID(result.getInt("ID"));
            movingPictureShow.setTime(result.getLong("Time"));
            movingPictureShow.setTitle(result.getString("Title"));
            movingPictureShow.setMovieID(result.getInt("MovieID"));
            //System.out.println(sdf.format(d) + " === " + sdf.format(new Date(movingPictureShow.getTime())));
            if (sdf.format(d).equals(sdf.format(new Date(movingPictureShow.getTime()))))
                movingPictureShows.add(movingPictureShow);//добавляем в список

        }
        selectStatement.close();
        return movingPictureShows;
    }

    public int getHallID() {
        return hallID;
    }

    public void setHallID(int hallID) {
        this.hallID = hallID;
    }

    public void insert() throws SQLException, ClassNotFoundException {//вставка данных в БД
        DataBaseAdapter adapter = DataBaseAdapter.getAdapter();//получаем объект
        String sql = "INSERT INTO `MovingPictureShow`(`HallID`,`MovieID`,`Title`,`Time`,`Cost`)\n" +
                "VALUES(?,?,?,?,?);";//запрос

        PreparedStatement insertStatement = adapter.prepareStatement(sql);//подготовка запроса
        insertStatement.setInt(1, hallID);//задаем аргументы запроса
        insertStatement.setInt(2, movieID);
        insertStatement.setString(3, title);
        insertStatement.setLong(4, time);
        insertStatement.setFloat(5, cost);
        insertStatement.executeUpdate();//выполняем запрос
        insertStatement.close();

        sql = "SELECT `ID` FROM `MovingPictureShow` ORDER BY `ID` DESC;";//получаем идентификатор последнего добавленного
        //сеанса
        PreparedStatement selectStatement = adapter.prepareStatement(sql);
        ResultSet result = selectStatement.executeQuery();
        result.next();
        ID = result.getInt("ID");
        selectStatement.close();
        //запрос на добавление информации о состоянии зала для сеанса
        sql = "INSERT INTO `HallInfo`(`MovingPictureShowID`,`HallState`)VALUES(?,?);";
        insertStatement = adapter.prepareStatement(sql);
        insertStatement.setInt(1, ID);
        insertStatement.setBytes(2, HallInfo.getFill(ID));
        insertStatement.executeUpdate();
        insertStatement.close();

    }

    @Override
    public String toString() {//строковое представление класса
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String timeString = sdf.format(new Date(time));
        return timeString + " - \"" + title + "\"";//+ cost + " грн";
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
