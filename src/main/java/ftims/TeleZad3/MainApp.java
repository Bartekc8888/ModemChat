package ftims.TeleZad3;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class MainApp extends Application
{
    private static final String mainWindowFXML = "fxml/MainWindow.fxml";
    private static final String portSettingsWindowFXML = "fxml/PortSettingsWindow.fxml";
    private static final String connectionSettingsWindowFXML = "fxml/ConnectionSettingsWindow.fxml";
    
    private Stage primaryStage;
    
    private MainWindowController mainWindowController;
    private PortSettingsWindow portSettingsWindow;
    
    private PortSettings portSettings;
    private PortManager portManager;
    private Thread portThread;
    
    private boolean connectionButtonState = false;
    
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = getClass().getClassLoader().getResource(mainWindowFXML);
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        mainWindowController = loader.getController();
        initMainWindowEvents(mainWindowController);
        
        portSettingsWindow = new PortSettingsWindow(portSettingsWindowFXML, this);
        portSettings = portSettingsWindow.getCurrentSettings();
        
        this.primaryStage = primaryStage;
        primaryStage.setTitle("ModemChat");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(720);
        primaryStage.setMinHeight(400);
        primaryStage.show();
    }
    
    public void setNewSettings(PortSettings settings) {
        portSettings = settings;
    }
    
    public Stage getStage() {
        return primaryStage;
    }
    
    public void sendMessage(String message) {
        if (portManager != null) {
            if (!portManager.sendMessageNonBlocking(message)) {
                showConnectionErrorBox();
            }
        } else {
            showConnectionErrorBox();
        }
    }
    
    public void messageSendingResult(boolean successfullySent, String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (successfullySent) {
                    mainWindowController.addTextToBoard(true, message);
                } else {
                    showConnectionErrorBox();
                }
            }
        });
    }
    
    public void newMessageReceived(String message) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                mainWindowController.addTextToBoard(false, message);
            }
        });
    }
    
    public void portStatusChanged(boolean isOpen) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                connectionButtonState = isOpen;
                mainWindowController.setConnectionButtonStatus(isOpen);
            }
        });
    }
    
    private void showConnectionErrorBox() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Błąd przesyłanaia");
        //alert.setHeaderText("Brak połączenia");
        alert.setContentText("Nie udało się przesłać wiadomości");
        alert.showAndWait();
    }
    
    private void initMainWindowEvents(MainWindowController controller) {
        controller.setPortSettingsButtonEvent(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                portSettingsWindow.showWindow();
            }
        });
        
        controller.setConnectButtonEvent(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!connectionButtonState) {
                    connectPort();
                } else {
                    disconnectPort();
                }
            }
        });
        
        controller.setChatSendButtonButtonEvent(this);
    }
    
    private void connectPort() {
        disconnectPort();
        
        try {
            portManager = new PortManager(portSettings, this);
            portThread = new Thread(portManager);
            portThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void disconnectPort() {
        if (portManager != null) {
            portManager.closePort();
            portManager = null;
            
            portThread.interrupt();
            portThread = null;
        }
    }
    
    @Override
    public void stop(){
        System.out.println("Stage is closing");
        if (portManager != null) {
            portManager.closePort();
        }
    }
}
