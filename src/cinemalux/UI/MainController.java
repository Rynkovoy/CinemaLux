package cinemalux.UI;


import cinemalux.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainController {
    public static Thread helper;
    public final ArrayList<Place> selection = new ArrayList<>();
    private final String exceptionHeader = "Ошибка";
    public ArrayList<Hall> hallList;
    public float cost;
    public Label userInfoLabel;
    public Label dateLabel;
    public ListView movieList;
    public DatePicker datePicker;
    public SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm");
    public AnimationTimer timer = new AnimationTimer() {
        @Override
        public void handle(long now) {
            dateLabel.setText(sdf.format(new Date()));
        }
    };
    public Label titleLabel;
    public Button sellButton;
    public Button bookButton;
    public Button addMovieButton;
    public GridPane gridPane;
    public TextField costField;
    public TextField payField;
    public TextField payBackField;
    public Button unbookButton;
    public Button unsellButton;
    public Label rowLabel1;
    public Label rowLabel2;
    public Label rowLabel3;
    public Button saveReportButton;
    public Button addMovieInfoButton;
    public Label descriptionField;
    public ImageView coverView;
    public Button adminPanel;
    public Label pointsLabel;
    public CheckBox usePoints;
    public WebView webView;


    public MainController() {

        isMainShown();

    }

    private void isMainShown() {
        if (helper != null)
            if (!helper.isInterrupted())
                helper.interrupt();
        helper = new Thread(() -> {
            while (Main.RUN) {
                if (Main.user != null) {
                    Platform.runLater(() -> onShow());
                    return;
                } else System.err.print("");
            }
        });
        helper.start();
    }

    public void setPointsLabel(int points) {
        pointsLabel.setText("У вас : " + points + " баллов (" + points * 10 + " грн)");
    }

    public void matchSelection() throws SQLException, ClassNotFoundException {
        MovingPictureShow item = (MovingPictureShow) movieList.getSelectionModel().getSelectedItem();
        cost = 0.0f;
        ArrayList<Row> rows = hallList.get(item.getHallID() - 1).getRows();
        for (Place place : selection)
            cost += item.getCost() / 100.0f * rows.get(place.getRow() - 1).getCostPercent();
        costField.setText(cost + "");
    }

    public void sell() {
        try {
            if (selection.isEmpty())
                throw new Exception("Ничего не выбрано");
            MovingPictureShow item = (MovingPictureShow) movieList.getSelectionModel().getSelectedItem();
            HallInfo hallInfo = new HallInfo(item.getID());
            byte[] hallState = hallInfo.getHallState();
            for (Place place : selection)
                if (hallState[place.getRow() * HallInfo.MAX_SIZE + place.getPlace()] != HallInfo.FREE)
                    throw new Exception("Все места должны быть свободны!");


            if (usePoints.isSelected()) {
                int points = Main.user.getPoints();
                if (points * 10.0f < cost)
                    throw new Exception("Недостаточно баллов!");

                for (Place place : selection) {
                    hallInfo.setHallState(place.getRow(), place.getPlace(), HallInfo.BUSY);
                    float cost = item.getCost() / 100.0f *
                            hallList.get(item.getHallID() - 1).getRows()
                                    .get(place.getRow() - 1)
                                    .getCostPercent();
                    Reporter.makeTicket(item, cost, place);
                }

                int diff = (int) (points * 10.0f - cost) / 10;
                Main.user.setPoints(diff);

                Employee.update(Main.user.getID(), Main.user.getPoints());
                setPointsLabel(Main.user.getPoints());
                selection.clear();
                payField.setText("");
                payBackField.setText("");
                matchSelection();
                drawHall(item);
                return;
            }

            float pay = Float.parseFloat(payField.getText());

            if (pay < cost)
                throw new Exception("Недостаточно средств!");

            payBackField.setText((pay - cost) + "");

            int count = selection.size();

            for (Place place : selection) {
                hallInfo.setHallState(place.getRow(), place.getPlace(), HallInfo.BUSY);
                float cost = item.getCost() / 100.0f *
                        hallList.get(item.getHallID() - 1).getRows()
                                .get(place.getRow() - 1)
                                .getCostPercent();
                Reporter.makeTicket(item, cost, place);
            }
            selection.clear();
            payField.setText("");
            matchSelection();
            drawHall(item);

            if (Main.user.getEmployeeTypeID() == 3 && Main.user.getID() != -1) {
                int points = Main.user.getPoints() + count;
                Main.user.setPoints(points);
                Employee.update(Main.user.getID(), Main.user.getPoints());
                setPointsLabel(Main.user.getPoints());
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertBuilder.showAlert(e.getMessage(), exceptionHeader, Alert.AlertType.ERROR);
        }
    }

    public void unsell() {
        try {
            if (selection.isEmpty())
                throw new Exception("Ничего не выбрано");
            MovingPictureShow item = (MovingPictureShow) movieList.getSelectionModel().getSelectedItem();
            HallInfo hallInfo = new HallInfo(item.getID());
            byte[] hallState = hallInfo.getHallState();
            for (Place place : selection)
                if (hallState[place.getRow() * HallInfo.MAX_SIZE + place.getPlace()] != HallInfo.BUSY)
                    throw new Exception("Не все выбранные места проданы!");
            for (Place place : selection)
                hallInfo.setHallState(place.getRow(), place.getPlace(), HallInfo.FREE);
            payBackField.setText(cost + "");
            selection.clear();
            payField.setText("");
            matchSelection();
            drawHall(item);
        } catch (Exception e) {
            e.printStackTrace();
            AlertBuilder.showAlert(e.getMessage(), exceptionHeader, Alert.AlertType.ERROR);
        }
    }

    public void unbook() {
        try {
            if (selection.isEmpty())
                throw new Exception("Ничего не выбрано");
            MovingPictureShow item = (MovingPictureShow) movieList.getSelectionModel().getSelectedItem();
            HallInfo hallInfo = new HallInfo(item.getID());
            byte[] hallState = hallInfo.getHallState();
            for (Place place : selection)
                if (hallState[place.getRow() * HallInfo.MAX_SIZE + place.getPlace()] != HallInfo.BOOKED)
                    throw new Exception("Не все выбранные места забронированы!");
            for (Place place : selection)
                hallInfo.setHallState(place.getRow(), place.getPlace(), HallInfo.FREE);
            selection.clear();
            payField.setText("");
            matchSelection();
            drawHall(item);
        } catch (Exception e) {
            e.printStackTrace();
            AlertBuilder.showAlert(e.getMessage(), exceptionHeader, Alert.AlertType.ERROR);
        }
    }

    public void book() {
        try {
            if (selection.isEmpty())
                throw new Exception("Ничего не выбрано");
            MovingPictureShow item = (MovingPictureShow) movieList.getSelectionModel().getSelectedItem();
            HallInfo hallInfo = new HallInfo(item.getID());
            byte[] hallState = hallInfo.getHallState();
            for (Place place : selection)
                if (hallState[place.getRow() * HallInfo.MAX_SIZE + place.getPlace()] != HallInfo.FREE)
                    throw new Exception("Все места должны быть свободны!");
            for (Place place : selection)
                hallInfo.setHallState(place.getRow(), place.getPlace(), HallInfo.BOOKED);
            selection.clear();
            payField.setText("");
            matchSelection();
            drawHall(item);
        } catch (Exception e) {
            e.printStackTrace();
            AlertBuilder.showAlert(e.getMessage(), exceptionHeader, Alert.AlertType.ERROR);
        }
    }

    public void onShow() {
        try {

            hallList = Hall.getHallList();

            datePicker.setValue(LocalDate.now());
            if (Main.user.getEmployeeTypeID() == 1) {
                sellButton.setDisable(true);
                bookButton.setDisable(true);
                unsellButton.setDisable(true);
                unbookButton.setDisable(true);
            } else if (Main.user.getEmployeeTypeID() == 2) {
                addMovieButton.setDisable(true);
                adminPanel.setDisable(true);
                addMovieInfoButton.setDisable(true);
            } else if (Main.user.getEmployeeTypeID() == 3) {
                if (Main.user.getID() == -1) {
                    sellButton.setDisable(true);
                    unbookButton.setDisable(true);
                    bookButton.setDisable(true);
                } else {
                    setPointsLabel(Main.user.getPoints());
                    usePoints.setVisible(true);
                }
                unsellButton.setDisable(true);
                addMovieButton.setDisable(true);
                adminPanel.setDisable(true);
                saveReportButton.setDisable(true);
                addMovieInfoButton.setDisable(true);

            }


            movieList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            movieList.setOnMouseClicked(event -> onMovieSelect((MovingPictureShow) movieList.getSelectionModel().getSelectedItem()));
            datePicker.valueProperty().addListener((observable, oldValue, newValue) ->
            {
                try {
                    LoadMovieList();
                } catch (Exception e) {
                    AlertBuilder.showAlert(e.getMessage(), exceptionHeader, Alert.AlertType.ERROR);
                }
            });


            adminPanel.setOnMouseClicked(event -> CustomDialog.adminDialog());
            addMovieButton.setOnMouseClicked(event -> {
                try {
                    CustomDialog.movingPictureShowDialog();
                    LoadMovieList();
                } catch (Exception e) {
                    AlertBuilder.showAlert(e.getMessage(), exceptionHeader, Alert.AlertType.ERROR);
                }
            });


            addMovieInfoButton.setOnMouseClicked(event1 -> CustomDialog.movieInfoDialog());

            sellButton.setOnMouseClicked(event -> sell());
            bookButton.setOnMouseClicked(event -> book());
            unbookButton.setOnMouseClicked(event -> unbook());
            unsellButton.setOnMouseClicked(event -> unsell());
            saveReportButton.setOnMouseClicked(event -> {
                LocalDate ld = datePicker.getValue();
                Calendar c = Calendar.getInstance();
                c.set(ld.getYear(), ld.getMonthValue() - 1, ld.getDayOfMonth());
                try {
                    Reporter.makeDatedReport(c.getTime().getTime());
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertBuilder.showAlert(e.getMessage(), exceptionHeader, Alert.AlertType.ERROR);
                }
            });


            if (!helper.isInterrupted())
                helper.interrupt();
            timer.start();
            //String typeName = EmployeeType.getTypeName(Main.user.getEmployeeTypeID());
            userInfoLabel.setText("Здравствуйте, " + Main.user.getName());
            LoadMovieList();


        } catch (Exception e) {
            e.printStackTrace();
            AlertBuilder.showAlert(e.getMessage(), exceptionHeader, Alert.AlertType.ERROR);
        }
    }

    public void drawHall(MovingPictureShow item) throws SQLException, ClassNotFoundException {
        gridPane.getChildren().clear();
        HallInfo hallInfo = new HallInfo(item.getID());
        final Hall hall = hallList.get(item.getHallID() - 1);
        byte[] hallState = hallInfo.getHallState();
        int centerX = HallInfo.MAX_SIZE / 2;
        int rows = hall.getRows().size();

        gridPane.setAlignment(Pos.CENTER);
        String style = "-fx-padding: 5px;" +
                "-fx-background-radius: 8px;" +
                "-fx-pref-width: 25px;" +
                "-fx-alignment: center;";

        String headerColor = "-fx-background-color: #0000FF;";
        String busyColor = "-fx-background-color: #D50000;";
        String freeColor = "-fx-background-color: #9E9E9E;";
        String bookColor = "-fx-background-color: #FF9800;";
        String selectionColor = "-fx-background-color: #76FF03;";

        for (int i = 0; i <= rows; i++) {
            if (i > 0) {
                Label labeledRectHeader = new Label("Р" + i);
                labeledRectHeader.setStyle(style + headerColor);
                gridPane.add(labeledRectHeader, 0, i);

                labeledRectHeader = new Label("Р" + i);
                labeledRectHeader.setStyle(style + headerColor);
                gridPane.add(labeledRectHeader, HallInfo.MAX_SIZE - 1, i);
            }
            for (int j = 0; j < HallInfo.MAX_SIZE; j++) {
                final Label labeledRect = new Label(j + "");

                if (hallState[i * HallInfo.MAX_SIZE + j] == HallInfo.FREE)
                    labeledRect.setStyle(style + freeColor);
                if (hallState[i * HallInfo.MAX_SIZE + j] == HallInfo.BOOKED)
                    labeledRect.setStyle(style + bookColor);
                if (hallState[i * HallInfo.MAX_SIZE + j] == HallInfo.BUSY)
                    labeledRect.setStyle(style + busyColor);

                if (hallState[i * HallInfo.MAX_SIZE + j] == HallInfo.UNUSED)
                    continue;

                int count = hall.getRows().get(i - 1).getCount() / 2 + 1;

                final int row = i;
                final int col = j;
                if (item.getTime() > new Date().getTime())
                    labeledRect.setOnMouseClicked(event ->
                    {
                        try {
                            Place tmp = null;
                            for (Place place : selection)
                                if (place.getPlace() == col && place.getRow() == row) {
                                    tmp = place;
                                    if (hallState[row * HallInfo.MAX_SIZE + col] == HallInfo.FREE)
                                        labeledRect.setStyle(style + freeColor);
                                    if (hallState[row * HallInfo.MAX_SIZE + col] == HallInfo.BOOKED)
                                        labeledRect.setStyle(style + bookColor);
                                    if (hallState[row * HallInfo.MAX_SIZE + col] == HallInfo.BUSY)
                                        labeledRect.setStyle(style + busyColor);
                                }
                            if (tmp != null)
                                selection.remove(tmp);
                            else {
                                selection.add(new Place(row, col));
                                labeledRect.setStyle(style + selectionColor);
                            }
                            matchSelection();
                        } catch (Exception e) {
                            e.printStackTrace();
                            AlertBuilder.showAlert(e.getMessage(), exceptionHeader, Alert.AlertType.ERROR);
                        }
                    });
                gridPane.add(labeledRect, centerX - count + j, i);
            }
        }

    }

    public void LoadMovieList() throws SQLException, ClassNotFoundException, ParseException {
        movieList.getItems().clear();
        LocalDate ld = datePicker.getValue();
        Calendar c = Calendar.getInstance();
        c.set(ld.getYear(), ld.getMonthValue() - 1, ld.getDayOfMonth());
        for (MovingPictureShow s : MovingPictureShow.getDatedList(c.getTime().getTime()))
            movieList.getItems().add(s);
        if (!movieList.getItems().isEmpty()) {
            movieList.getSelectionModel().select(0);
            onMovieSelect((MovingPictureShow) movieList.getSelectionModel().getSelectedItem());
        }
    }

    public void onMovieSelect(MovingPictureShow item) {
        try {
            titleLabel.setText(item.toString());
            selection.clear();
            matchSelection();
            String[] labels = hallList.get(item.getHallID() - 1).getStringCost(item.getCost());
            rowLabel1.setText(labels[0]);
            rowLabel2.setText(labels[1]);
            rowLabel3.setText(labels[2]);
            drawHall((MovingPictureShow) movieList.getSelectionModel().getSelectedItem());

            MovieInfo movie = MovieInfo.Load(item.getMovieID());
            descriptionField.setText(movie.getDescription());

            webView.getEngine().load(movie.getTrailer());


            BufferedImage img = ImageIO.read(new ByteArrayInputStream(movie.getCover()));
            coverView.setImage(SwingFXUtils.toFXImage(img, null));

        } catch (Exception e) {
            e.printStackTrace();
            AlertBuilder.showAlert(e.getMessage(), exceptionHeader, Alert.AlertType.ERROR);
        }
    }

    public void graphicClick(ActionEvent actionEvent) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("graphic.fxml"));
        Parent root1 = (Parent) fxmlLoader.load();
        Stage stage = new Stage();
        //stage.initModality(Modality.APPLICATION_MODAL);
        //stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("ABC");
        stage.setScene(new Scene(root1));
        stage.show();
    }
}
