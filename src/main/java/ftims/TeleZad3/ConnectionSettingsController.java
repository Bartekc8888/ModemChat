package ftims.TeleZad3;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;

public class ConnectionSettingsController implements Initializable {
    @FXML
    private Button okButton;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private TextField phoneNumberField;
    
    ConnectionSettings currentSettings;
    
    @FXML
    public void initialize() {
        System.out.println("connSetContrInit");
        initDataFields();
        saveSettings();
    }
    
    public void setOkButtonEvent(EventHandler<ActionEvent> eventHandler) {
        okButton.setOnAction(eventHandler);
    }
    
    public void setCancelButtonEvent(EventHandler<ActionEvent> eventHandler) {
        cancelButton.setOnAction(eventHandler);
    }
    
    public void saveSettings() {
        currentSettings = getSettings();
    }
    
    public void resetSettings() {
        phoneNumberField.setText(currentSettings.getPhoneNumber());
    }
    
    public ConnectionSettings getSettings() {
        ConnectionSettings settings = new ConnectionSettings(phoneNumberField.getText());
        
        return settings;
    }
    
    private void initDataFields() {
        phoneNumberField.setText("100200300");
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initialize();
    }
}
