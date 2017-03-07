package cinemalux;


public class Row {//Класс - ряд в зале
    private int hallID;//номер зала
    private int count;//кол-во мест
    private int number;//номер ряда
    private float costPercent;//процент стоимости(100,120,150)

    public Row() {
    }

    public int getHallID() {
        return hallID;
    }

    public void setHallID(int hallID) {
        this.hallID = hallID;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public float getCostPercent() {
        return costPercent;
    }

    public void setCostPercent(float costPercent) {
        this.costPercent = costPercent;
    }
}
