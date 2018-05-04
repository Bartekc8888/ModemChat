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

public class PortSettingsWindow {
    private PortSettingsController portSettingsController;
    private Stage settingsStage;
    private MainApp parent;
    
    public PortSettingsWindow(String portSettingsWindowFXML, MainApp parent) throws IOException {
        this.parent = parent;
        
        URL url = getClass().getClassLoader().getResource(portSettingsWindowFXML);
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        
        portSettingsController = loader.getController();
        initPortSettingsWindowEvents(portSettingsController);
        
        settingsStage = new Stage();
        settingsStage.setTitle("Ustawienia portu");
        settingsStage.setScene(scene);
        settingsStage.initOwner(parent.getStage());
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.setMinWidth(400);
        settingsStage.setMinHeight(300);
    }
    
    public void showWindow() {
        settingsStage.show();
    }
    
    public PortSettings getCurrentSettings() {
        return portSettingsController.getSettings();
    }
    
    private void initPortSettingsWindowEvents(PortSettingsController controller) {
        controller.setOkButtonEvent(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                PortSettings settings = portSettingsController.getSettings();
                parent.setNewSettings(settings);
                portSettingsController.saveSettings();
                settingsStage.hide();
            }
        });
        
        controller.setCancelButtonEvent(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                portSettingsController.resetSettings();
                settingsStage.hide();
            }
        });
    }
}
