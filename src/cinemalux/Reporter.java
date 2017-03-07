package cinemalux;


import cinemalux.UI.Place;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Reporter {

    public static final String path = "";//сохранение отчетов и билетов

    public static void makeDatedReport(long date) throws SQLException, ClassNotFoundException, IOException {
        ArrayList<MovingPictureShow> list = MovingPictureShow.getDatedList(date);
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String name = sdf.format(new Date(date)) + ".xls";

        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("Report");

        org.apache.poi.ss.usermodel.Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Название");
        row.createCell(1).setCellValue("Кол-во проданных мест");
        row.createCell(2).setCellValue("Стоимость");

        int rowNum = 1;
        float total = 0.0f;

        ArrayList<Hall> hallList = Hall.getHallList();

        for (MovingPictureShow item : list) {
            float cost = 0.0f;
            String title = item.toString();
            int hallID = item.getHallID();
            int count = 0;
            byte[] hallState = new HallInfo(item.getID()).getHallState();
            for (int i = 0; i < HallInfo.MAX_SIZE; i++)
                for (int j = 0; j < HallInfo.MAX_SIZE; j++)
                    if (hallState[i * HallInfo.MAX_SIZE + j] == HallInfo.BUSY) {
                        cost += item.getCost() / 100.0f *
                                hallList.get(hallID - 1)
                                        .getRows().get(i - 1)
                                        .getCostPercent();
                        count++;
                    }
            row = sheet.createRow(rowNum);
            row.createCell(0).setCellValue(title);
            row.createCell(1).setCellValue(count);
            row.createCell(2).setCellValue(cost);
            total += cost;
            rowNum++;
        }
        row = sheet.createRow(rowNum);
        row.createCell(2).setCellValue(total);

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);

        FileOutputStream stream = new FileOutputStream(path + name);
        book.write(stream);

        book.close();
        System.out.println("Report[" + name + "] Saved");
    }
    public static void makeTicket(MovingPictureShow item,float cost,Place place) throws IOException {
        Workbook book = new HSSFWorkbook();
        Sheet sheet = book.createSheet("Ticket");

        String title = item.getTitle();
        long time = item.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
        String timeString = sdf.format(new Date(time));

        org.apache.poi.ss.usermodel.Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("Название");
        row.createCell(1).setCellValue(title);

        row = sheet.createRow(1);
        row.createCell(0).setCellValue("Время");
        row.createCell(1).setCellValue(timeString);

        row = sheet.createRow(2);
        row.createCell(0).setCellValue("Ряд");
        row.createCell(1).setCellValue(place.getRow());

        row = sheet.createRow(3);
        row.createCell(0).setCellValue("Место");
        row.createCell(1).setCellValue(place.getPlace());

        row = sheet.createRow(4);
        row.createCell(0).setCellValue("Стоимость");
        row.createCell(1).setCellValue(cost + " грн");

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        String name = title + " - " + timeString + " " + place.getRow() + " - " + place.getPlace() + ".xls";
        FileOutputStream stream = new FileOutputStream(path + name);
        book.write(stream);
        System.out.println("Ticket[" + name + "] Saved");
        book.close();
    }
}
