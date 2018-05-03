package ftims.TeleZad3;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application
{
    private static final String mainWindowFXML = "fxml/MainWindow.fxml";
    private static final String portSettingsWindowFXML = "fxml/PortSettingsWindow.fxml";
    private static final String connectionSettingsWindowFXML = "fxml/ConnectionSettingsWindow.fxml";
    
    public static void main(String[] args)
    {
        launch(args);
        
        //String portName = "COM1";
        //
        //PortSettings settings = new PortSettings(9600, 8, PortSettings.Parity.EVEN_PARITY,
        //        PortSettings.StopBits.ONE_STOP_BIT, PortSettings.FlowControl.FLOW_CONTROL_RTS_CTS);
        //
        //PortManager manager = new PortManager(portName, settings);
        //if (manager.openPort()) {
        //    System.out.println("port opened");
        //}
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL url = getClass().getClassLoader().getResource(mainWindowFXML);
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("JavaFX");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(720);
        primaryStage.setMinHeight(400);
        primaryStage.show();
    }
}
