package ftims.TeleZad3;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class MainWindowController implements Initializable {
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
    
    @FXML
    public void chatInputFieldOnEnter(ActionEvent event){
        chatSendButton.fire();
    }
    
    public void addTextToBoard(boolean isOwnMessage, String message) {
        if (message.isEmpty()) {
            return;
        }
        
        if (isOwnMessage) {
            message = "Ja: " + message;
        } else {
            message = "Ktoś: " + message;
        }
        
        chatView.getItems().add(message);
    }
    
    public void setConnectionButtonStatus(boolean isPortOpen) {
        if (isPortOpen) {
            connectButton.setText("Rozłącz");
        } else {
            connectButton.setText("Połącz");
        }
    }
    
    public void setPortSettingsButtonEvent(EventHandler<ActionEvent> eventHandler) {
        portSettings.setOnAction(eventHandler);
    }
    
    public void setConnectButtonEvent(EventHandler<ActionEvent> eventHandler) {
        connectButton.setOnAction(eventHandler);
    }
    
    public void setChatSendButtonButtonEvent(MainApp messageHandler) {
        chatSendButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String messageToSend = chatInputField.getText();
                chatInputField.setText("");
                
                if (!messageToSend.isEmpty()) {
                    messageHandler.sendMessage(messageToSend);
                }
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
