package cinemalux.UI;


import javafx.scene.control.Alert;

public class AlertBuilder {
    private static Alert alert;

    private AlertBuilder() {

    }

    public static void showAlert(String text, String header, Alert.AlertType alertType) {
        if (alert == null)
            alert = new Alert(alertType);

        alert.setAlertType(alertType);
        alert.setHeaderText(header);
        alert.setContentText(text);
        alert.showAndWait();
    }
}
