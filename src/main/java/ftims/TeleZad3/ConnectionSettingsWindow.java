package ftims.TeleZad3;

import java.io.IOException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ConnectionSettingsWindow {
    private ConnectionSettingsController connectionSettingsController;
    private Stage settingsStage;
    private MainApp parent;
    
    public ConnectionSettingsWindow(String connectionSettingsWindowFXML, MainApp parent) throws IOException {
        this.parent = parent;
        
        URL url = getClass().getClassLoader().getResource(connectionSettingsWindowFXML);
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        connectionSettingsController = loader.getController();
        initConnectionSettingsWindowEvents(connectionSettingsController);
        
        settingsStage = new Stage();
        settingsStage.setTitle("Ustawienia połączenia");
        settingsStage.setScene(scene);
        settingsStage.initOwner(parent.getStage());
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.setMinWidth(400);
        settingsStage.setMinHeight(300);
    }
    
    public void showWindow() {
        settingsStage.show();
    }
    
    public ConnectionSettings getCurrentSettings() {
        return connectionSettingsController.getSettings();
    }
    
    private void initConnectionSettingsWindowEvents(ConnectionSettingsController controller) {
        controller.setOkButtonEvent(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ConnectionSettings settings = connectionSettingsController.getSettings();
                parent.setNewConnectionSettings(settings);
                connectionSettingsController.saveSettings();
                settingsStage.hide();
            }
        });
        
        controller.setCancelButtonEvent(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                connectionSettingsController.resetSettings();
                settingsStage.hide();
            }
        });
    }
}
