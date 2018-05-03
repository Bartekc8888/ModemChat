package ftims.TeleZad3;

import java.io.IOException;
import java.net.URL;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application
{
    private static final String mainWindowFXML = "fxml/MainWindow.fxml";
    private static final String portSettingsWindowFXML = "fxml/PortSettingsWindow.fxml";
    private static final String connectionSettingsWindowFXML = "fxml/ConnectionSettingsWindow.fxml";
    
    private MainWindowController mainWindowController;
    private PortSettingsWindow portSettingsWindow;
    
    private PortSettings portSettings;
    PortManager portManager;
    
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
        
        primaryStage.setTitle("ModemChat");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(720);
        primaryStage.setMinHeight(400);
        primaryStage.show();
    }
    
    public void setNewSettings(PortSettings settings) {
        portSettings = settings;
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
                connectPort();
            }
        });
    }
    
    private void connectPort() {
        if (portManager != null) {
            portManager.closePort();
            portManager = null;
        }
        
        try {
            portManager = new PortManager(portSettings);
            
            if (portManager.openPort()) {
                System.out.println("port opened");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
