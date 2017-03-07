package cinemalux.UI;


public class Place {
    private int row;
    private int place;

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public Place(int row, int place) {

        this.row = row;
        this.place = place;
    }
}
