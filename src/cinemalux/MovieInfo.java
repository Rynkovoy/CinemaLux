package cinemalux;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class MovieInfo {
    private String title;
    private int ID;
    private String description;
    private byte[] cover;
    private String trailer;

    public MovieInfo() {

    }

    public static HashMap<String, Integer> getMovies() throws SQLException, ClassNotFoundException {
        HashMap<String, Integer> movies = new HashMap<>();
        DataBaseAdapter adapter = DataBaseAdapter.getAdapter();
        String sql = "SELECT `ID`,`Title` FROM `MovieInfo`";//список залов
        PreparedStatement selectStatement = adapter.prepareStatement(sql);
        ResultSet result = selectStatement.executeQuery();
        while (result.next())
            movies.put(result.getString("Title"), result.getInt("ID"));
        selectStatement.close();
        return movies;
    }

    public static MovieInfo Load(int id) throws SQLException, ClassNotFoundException {
        DataBaseAdapter adapter = DataBaseAdapter.getAdapter();
        String sql = "SELECT * FROM `MovieInfo` WHERE `ID` = ?";
        PreparedStatement selectStatement = adapter.prepareStatement(sql);
        selectStatement.setInt(1,id);
        ResultSet resultSet = selectStatement.executeQuery();
        resultSet.next();
        MovieInfo result = new MovieInfo();
        result.setID(id);
        result.setTitle(resultSet.getString("Title"));
        result.setDescription(resultSet.getString("Description"));
        result.setCover(resultSet.getBytes("Cover"));
        result.setTrailer(resultSet.getString("Trailer"));
        selectStatement.close();
        return result;
    }

    public void insert() throws SQLException, ClassNotFoundException {
        DataBaseAdapter adapter = DataBaseAdapter.getAdapter();
        if (adapter.exists("Title", title, "MovieInfo"))
            throw new SQLException("Movie with title \'" + title + "\' exists");

        String sql = "INSERT INTO `MovieInfo`(`Title`,`Description`,`Cover`,`Trailer`)\n" +
                "VALUES(?,?,?,?);";
        PreparedStatement insertStatement = adapter.prepareStatement(sql);

        insertStatement.setString(1, title);
        insertStatement.setString(2, description);
        insertStatement.setBytes(3, cover);
        insertStatement.setString(4, trailer);

        insertStatement.executeUpdate();
        insertStatement.close();

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getCover() {
        return cover;
    }

    public void setCover(byte[] cover) {
        this.cover = cover;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }
}
