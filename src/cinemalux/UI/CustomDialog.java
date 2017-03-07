package cinemalux.UI;


import cinemalux.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

public class CustomDialog {


    public static void regestrationDialog() {
        Dialog<Employee> dialog = new Dialog<Employee>();
        dialog.setTitle("Регистрация");

        final ButtonType addButtonType = new ButtonType("Зарегистрироваться", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10,10,10,10));

        final TextField name = new TextField();
        name.setPromptText("ФИО");
        final TextField login = new TextField();
        login.setPromptText("Логин");
        final PasswordField password = new PasswordField();
        final PasswordField passwordRepeat = new PasswordField();


        grid.add(new Label("ФИО:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Логин:"), 0, 1);
        grid.add(login, 1, 1);
        grid.add(new Label("Пароль:"), 0, 2);
        grid.add(password, 1, 2);
        grid.add(new Label("Подтвердите пароль:"), 0, 3);
        grid.add(passwordRepeat, 1, 3);


        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> name.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    if(!password.getText().equals(passwordRepeat.getText()))
                        throw new Exception("Введенные пароли не совпадают!");

                    if(name.getText().trim().length() == 0)
                        throw new Exception("Поле ФИО не может быть пустым");

                    if(login.getText().trim().length() == 0)
                        throw new Exception("Поле Логин не может быть пустым");

                    if(password.getText().trim().length() < 4)
                        throw new Exception("Пароль не должен быть короче 4 символов!");

                    Employee employee = new Employee();
                    employee.setName(name.getText());
                    employee.setPassword(password.getText());
                    employee.setLogin(login.getText());
                    employee.setEmployeeTypeID(3);
                    employee.insert();
                } catch (Exception e) {
                    AlertBuilder.showAlert(e.getMessage(), "Ошибка!", Alert.AlertType.ERROR);
                }

            }
            return null;
        });

        Optional<Employee> result = dialog.showAndWait();

    }

    public static void emplloyeeDialog() {
        Dialog<Employee> dialog = new Dialog<Employee>();
        dialog.setTitle("Добавить сотрудника");

        final ButtonType addButtonType = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));

        final TextField name = new TextField();
        name.setPromptText("ФИО");
        final TextField login = new TextField();
        login.setPromptText("Логин");
        final PasswordField password = new PasswordField();
        final ComboBox<String> comboBox = new ComboBox<String>();

        try {

            comboBox.getItems().add(EmployeeType.getTypeName(1));
            comboBox.getItems().add(EmployeeType.getTypeName(2));
            comboBox.getItems().add(EmployeeType.getTypeName(3));
        } catch (Exception e) {
            e.printStackTrace();
        }
        comboBox.getSelectionModel().select(0);

        grid.add(new Label("ФИО:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Логин:"), 0, 1);
        grid.add(login, 1, 1);
        grid.add(new Label("Пароль:"), 0, 2);
        grid.add(password, 1, 2);
        grid.add(new Label("Должность:"), 0, 3);
        grid.add(comboBox, 1, 3);


        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> name.requestFocus());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    Employee employee = new Employee();
                    employee.setName(name.getText());
                    employee.setPassword(password.getText());
                    employee.setLogin(login.getText());
                    String selected = comboBox.getSelectionModel().getSelectedItem();
                    employee.setEmployeeTypeID(EmployeeType.getTypeMap().get(selected));
                    employee.insert();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
            return null;
        });

        Optional<Employee> result = dialog.showAndWait();

    }

    @SuppressWarnings("deprecation")
    public static void movingPictureShowDialog() throws SQLException, ClassNotFoundException {
        Dialog<MovingPictureShow> dialog = new Dialog<MovingPictureShow>();
        dialog.setTitle("Добавить киносеанс");

        final ButtonType addButtonType = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));


        final HashMap<String, Integer> movies = MovieInfo.getMovies();

        ComboBox<String> titles = new ComboBox<>();
        titles.getItems().addAll(movies.keySet());
        if (titles.getItems().size() > 0)
            titles.getSelectionModel().select(0);


        TextField cost = new TextField();
        ComboBox<Hall> hallComboBox = new ComboBox<>();


        DatePicker date = new DatePicker();
        TextField time = new TextField();
        time.setPromptText("10:00");


        hallComboBox.getItems().addAll(Hall.getHallList());
        hallComboBox.getSelectionModel().select(0);

        grid.add(new Label("Название:"), 0, 0);
        grid.add(titles, 1, 0);
        grid.add(new Label("Дата:"), 0, 1);
        grid.add(date, 1, 1);
        grid.add(new Label("Время:"), 0, 2);
        grid.add(time, 1, 2);
        grid.add(new Label("Стоимость:"), 0, 3);
        grid.add(cost, 1, 3);
        grid.add(new Label("Зал:"), 0, 4);
        grid.add(hallComboBox, 1, 4);

        dialog.getDialogPane().setContent(grid);
        Platform.runLater(() -> titles.requestFocus());


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    MovingPictureShow movingPictureShow = new MovingPictureShow();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    movingPictureShow.setHallID(hallComboBox.getSelectionModel().getSelectedItem().getID());
                    movingPictureShow.setTitle(titles.getSelectionModel().getSelectedItem());
                    movingPictureShow.setCost(Float.parseFloat(cost.getText()));
                    movingPictureShow.setMovieID(movies.get(titles.getSelectionModel().getSelectedItem()));
                    LocalDate ld = date.getValue();
                    Calendar c = Calendar.getInstance();
                    c.set(ld.getYear(), ld.getMonthValue() - 1, ld.getDayOfMonth());

                    Date tm = sdf.parse(time.getText());
                    Date dt = c.getTime();
                    dt.setHours(tm.getHours());
                    dt.setMinutes(tm.getMinutes());

                    movingPictureShow.setTime(dt.getTime());
                    movingPictureShow.insert();
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    AlertBuilder.showAlert(e.getMessage(), "Ошибка", Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        Optional<MovingPictureShow> result = dialog.showAndWait();
    }

    public static void movieInfoDialog() {
        Dialog<MovieInfo> dialog = new Dialog<>();
        dialog.setTitle("Добавить фильм");

        final ButtonType addButtonType = new ButtonType("Добавить", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType);
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 20, 20));


        TextField title = new TextField();
        title.setPromptText("Название");
        TextArea description = new TextArea();
        description.setPromptText("Описание");
        TextField trailer = new TextField();
        trailer.setPromptText("Ссылка на трейлер");
        final ImageView cover = new ImageView();
        cover.setFitWidth(120);
        cover.setFitHeight(180);
        Button fileDialog = new Button("Выбрать обложку");
        final FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выбрать обложку");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        fileDialog.setOnMouseClicked(event -> {
            File file = fileChooser.showOpenDialog(Main.stage);
            if (file != null) {
                //System.out.println(file.getAbsolutePath());
                cover.setImage(new Image("file:///" + file.getAbsolutePath()));
            }
        });


        grid.add(new Label("Название"), 0, 0);
        grid.add(new Label("Описание"), 0, 1);
        grid.add(new Label("Трейлер"), 0, 2);
        grid.add(fileDialog, 0, 3);

        grid.add(title, 1, 0);
        grid.add(description, 1, 1);
        grid.add(trailer, 1, 2);
        grid.add(cover, 1, 3);
        Platform.runLater(() -> title.requestFocus());


        dialog.getDialogPane().setContent(grid);


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    MovieInfo movie = new MovieInfo();
                    movie.setTitle(title.getText());
                    movie.setDescription(description.getText());
                    movie.setTrailer(trailer.getText());
                    BufferedImage bImage = SwingFXUtils.fromFXImage(cover.getImage(), null);
                    ByteArrayOutputStream s = new ByteArrayOutputStream();
                    ImageIO.write(bImage, "png", s);
                    movie.setCover(s.toByteArray());
                    movie.insert();
                } catch (Exception e) {
                    AlertBuilder.showAlert(e.getMessage(), "Ошибка", Alert.AlertType.ERROR);
                }
            }
            return null;
        });

        Optional<MovieInfo> result = dialog.showAndWait();

    }

    private static ObservableList<EmplTableItem> loadUsers() {
        ObservableList<EmplTableItem> usersData = FXCollections.observableArrayList();

        try {
            for (Employee e : Employee.getEmployees()) {
                EmplTableItem item = new EmplTableItem();
                item.setLogin(e.getLogin());
                item.setName(e.getName());
                item.setId(e.getID());
                item.setType(EmployeeType.getTypeName(e.getEmployeeTypeID()));
                usersData.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usersData;
    }

    public static void adminDialog() {
        Dialog<Employee> dialog = new Dialog<Employee>();
        dialog.setTitle("Список сотрудников");

        final ButtonType closeButtonType = new ButtonType("Закрыть", ButtonBar.ButtonData.APPLY);
        dialog.getDialogPane().getButtonTypes().addAll(closeButtonType);


        TableView table = new TableView();
        table.setPrefWidth(360);
        table.setEditable(false);
        TableColumn<EmplTableItem, Integer> id = new TableColumn<>("ID");
        TableColumn<EmplTableItem, String> name = new TableColumn<>("ФИО");
        TableColumn<EmplTableItem, String> login = new TableColumn<>("Логин");
        TableColumn<EmplTableItem, String> type = new TableColumn<>("Должность");

        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        login.setCellValueFactory(new PropertyValueFactory<>("login"));
        type.setCellValueFactory(new PropertyValueFactory<>("type"));


        Button addButton = new Button("Добавить");
        Button removeButton = new Button("Удалить");


        table.getColumns().addAll(id, name, login, type);
        table.setItems(loadUsers());

        addButton.setOnMouseClicked(event ->
        {
            CustomDialog.emplloyeeDialog();
            table.setItems(loadUsers());
        });
        removeButton.setOnMouseClicked(event -> {

            try {
                int selectedID = ((EmplTableItem) table.getSelectionModel().getSelectedItem()).getId();
                Employee.remove(selectedID);
                table.setItems(loadUsers());
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        final HBox hbox = new HBox();
        hbox.setSpacing(10);
        hbox.setPadding(new Insets(0, 0, 0, 0));


        hbox.getChildren().addAll(addButton, removeButton);

        final VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(20, 20, 20, 20));

        vbox.getChildren().addAll(table, hbox);
        dialog.getDialogPane().setContent(vbox);

        //dialog.getDialogPane().setContent(grid);
        //Platform.runLater(() -> name.requestFocus());
/*
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == removeButtonType) {
                try {

                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
            return null;
        });
*/
        Optional<Employee> result = dialog.showAndWait();
    }

    public static class EmplTableItem {
        private int id;
        private String name;
        private String login;
        private String type;

        public EmplTableItem() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}
