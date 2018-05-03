package ftims.TeleZad3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.SpinnerValueFactory.IntegerSpinnerValueFactory;

public class PortSettingsController {
    @FXML
    private Button okButton;
    
    @FXML
    private Button cancelButton;
    
    @FXML
    private TextField portNameField;
    
    @FXML
    private Spinner baudRateSpinner;
    
    @FXML
    private Spinner dataSizeSpinner;
    
    @FXML
    private ComboBox parityComboBox;
    
    @FXML
    private ComboBox stopBitComboBox;
    
    @FXML
    private ComboBox flowControlComboBox;

    PortSettings currentSettings;
    
    @FXML
    public void initialize() {
        System.out.println("portSetContrInit");
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
        portNameField.setText(currentSettings.getPortName());
        baudRateSpinner.getValueFactory().setValue(currentSettings.getBaudRate());
        dataSizeSpinner.getValueFactory().setValue(currentSettings.getNumberOfDataBits());
        parityComboBox.getSelectionModel().select(currentSettings.getParity());
        stopBitComboBox.getSelectionModel().select(currentSettings.getStopBits());
        flowControlComboBox.getSelectionModel().select(currentSettings.getFlowControl());
    }
    
    public PortSettings getSettings() {
        PortSettings settings = new PortSettings(portNameField.getText(), (int)baudRateSpinner.getValue(), (int)dataSizeSpinner.getValue(),
                (PortSettings.Parity)parityComboBox.getValue(), (PortSettings.StopBits)stopBitComboBox.getValue(),
                (PortSettings.FlowControl)flowControlComboBox.getValue());
        
        return settings;
    }
    
    private void initDataFields() {
        portNameField.setText("COM1");
        
        baudRateSpinner.setValueFactory(new IntegerSpinnerValueFactory(1, 1_000_000, 9600, 100));
        baudRateSpinner.setEditable(true);
        dataSizeSpinner.setValueFactory(new IntegerSpinnerValueFactory(1, 32, 8, 1));
        dataSizeSpinner.setEditable(true);
        
        ObservableList<PortSettings.Parity> parity = FXCollections.observableArrayList(PortSettings.Parity.getListOfValues());
        parityComboBox.setItems(parity);
        parityComboBox.getSelectionModel().select(2);
        ObservableList<PortSettings.StopBits> stopBits = FXCollections.observableArrayList(PortSettings.StopBits.getListOfValues());
        stopBitComboBox.setItems(stopBits);
        stopBitComboBox.getSelectionModel().select(0);
        ObservableList<PortSettings.FlowControl> flowControl = FXCollections.observableArrayList(PortSettings.FlowControl.getListOfValues());
        flowControlComboBox.setItems(flowControl);
        flowControlComboBox.getSelectionModel().select(7);
    }
}
