package ftims.TeleZad3;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MainWindowController {
    @FXML
    private ListView chatView;
    
    @FXML
    private TextField chatInputField;
    
    @FXML
    private Button chatSendButton;
    
    @FXML
    private Button portSettings;
    
    @FXML
    private Button connectionSettings;
    
    @FXML
    private Button connectButton;
    
    @FXML
    private TextField commandField;
    
    @FXML
    private Button commandSendButton;
    
    @FXML
    public void initialize() {
        System.out.println("mainWindowContrInit");
    }
    
    public void setPortSettingsButtonEvent(EventHandler<ActionEvent> eventHandler) {
        portSettings.setOnAction(eventHandler);
    }
    
    public void setConnectButtonEvent(EventHandler<ActionEvent> eventHandler) {
        connectButton.setOnAction(eventHandler);
    }
}
