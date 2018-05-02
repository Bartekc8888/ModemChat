package ftims.TeleZad3;

import com.fazecast.jSerialComm.SerialPort;

public class PortManager {
    SerialPort port;
    
    PortManager(String portName, PortSettings settings) {
        port = findPort(portName);
        
        port.setComPortParameters(settings.getBaudRate(), settings.getNumberOfDataBits(),
                                  settings.getStopBits().getValue(), settings.getParity().getValue());
        port.setFlowControl(settings.getFlowControl().getValue());
        port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
    }
    
    public boolean openPort() {
        return port.openPort();
    }
    
    private SerialPort findPort(String portName) {
        SerialPort[] ports = SerialPort.getCommPorts();
        
        SerialPort chosenPort = null;
        for (SerialPort port : ports) {
            if (port.getSystemPortName().equals(portName)) {
                chosenPort = port;
                break;
            }
        }
        
        if (chosenPort == null) {
            throw new RuntimeException("Port \"" + portName + "\" not found");
        }
        
        return chosenPort;
    }
}
