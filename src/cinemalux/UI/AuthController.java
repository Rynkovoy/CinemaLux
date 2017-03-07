package cinemalux.UI;


import cinemalux.Auth;
import cinemalux.Employee;
import cinemalux.EmployeeType;
import javafx.event.Event;
import javafx.scene.control.*;

import java.sql.SQLException;

public class AuthController {
    public final String exceptionHeader = "Ошибка";
    public TextField loginField;
    public PasswordField passwordField;
    public Button loginButton;
    public CheckBox userCheckBox;
    public Button regButton;


    public void loginButtonClick(Event event) {
        try {
            if (userCheckBox.isSelected()) {
                Main.user = new Employee();
                Main.user.setID(-1);
                Main.user.setName("Зритель");
                Main.user.setEmployeeTypeID(EmployeeType.getTypeMap().get("Зритель"));
            } else
                Main.user = Auth.Authorize(loginField.getText(), passwordField.getText());

            //AlertBuilder.showAlert(Main.user.toString(), "OK", Alert.AlertType.INFORMATION);
            Main.setScene(Main.mainScene);

        } catch (SQLException e) {
            e.printStackTrace();
            AlertBuilder.showAlert(e.getMessage(), exceptionHeader, Alert.AlertType.ERROR);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            AlertBuilder.showAlert(e.getMessage(), exceptionHeader, Alert.AlertType.ERROR);
        } finally {
            loginField.setText("");
            passwordField.setText("");
            userCheckBox.setSelected(false);
        }
    }

    public void regButtonClick(Event event) {

        CustomDialog.regestrationDialog();

    }
}
