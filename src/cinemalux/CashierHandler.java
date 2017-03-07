package cinemalux;


import java.sql.SQLException;

public class CashierHandler {
    private static CashierHandler instance;
    private Employee employee = null;

    private CashierHandler(Employee employee) {
        this.employee = employee;
    }

    public static CashierHandler getInstance(Employee employee) throws SQLException, ClassNotFoundException {
        if (instance == null)
            instance = new CashierHandler(employee);
        if (employee.getEmployeeTypeID() == EmployeeType.getTypeMap().get("Кассир"))
            return instance;
        return null;
    }

    void sell(int movingPictureShowID, int row, int place) throws SQLException, ClassNotFoundException {
        HallInfo hallInfo = new HallInfo(movingPictureShowID);
        hallInfo.setHallState(row, place, HallInfo.BUSY);
        hallInfo.update();
    }

    void book(int movingPictureShowID, int row, int place) throws SQLException, ClassNotFoundException {
        HallInfo hallInfo = new HallInfo(movingPictureShowID);
        hallInfo.setHallState(row, place, HallInfo.BOOKED);
        hallInfo.update();
    }

    void unset(int movingPictureShowID, int row, int place) throws SQLException, ClassNotFoundException {
        HallInfo hallInfo = new HallInfo(movingPictureShowID);
        hallInfo.setHallState(row, place, HallInfo.FREE);
        hallInfo.update();
    }
}
