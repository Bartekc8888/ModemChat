package ftims.TeleZad3;

import com.fazecast.jSerialComm.SerialPort;

public class Main 
{
    public static void main(String[] args)
    {
        String portName = "COM1";
        
        PortSettings settings = new PortSettings(9600, 8, PortSettings.Parity.EVEN_PARITY,
                PortSettings.StopBits.ONE_STOP_BIT, PortSettings.FlowControl.FLOW_CONTROL_DISABLED);
        
        PortManager manager = new PortManager(portName, settings);
        if (manager.openPort()) {
            System.out.println("port opened");
        }
    }
}
