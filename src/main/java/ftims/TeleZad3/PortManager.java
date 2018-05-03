package ftims.TeleZad3;

import java.io.IOException;

import com.fazecast.jSerialComm.SerialPort;

public class PortManager {
    SerialPort port;
    
    PortManager(PortSettings settings) throws IOException {
        port = findPort(settings.getPortName());
        
        port.setComPortParameters(settings.getBaudRate(), settings.getNumberOfDataBits(),
                                  settings.getStopBits().getValue(), settings.getParity().getValue());
        port.setFlowControl(settings.getFlowControl().getValue());
        port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
    }
    
    public boolean openPort() {
        return port.openPort();
    }
    
    public void closePort() {
        port.closePort();
    }
    
    private SerialPort findPort(String portName) throws IOException {
        SerialPort[] ports = SerialPort.getCommPorts();
        
        SerialPort chosenPort = null;
        for (SerialPort port : ports) {
            if (port.getSystemPortName().equals(portName)) {
                chosenPort = port;
                break;
            }
        }
        
        if (chosenPort == null) {
            throw new IOException("Port \"" + portName + "\" not found");
        }
        
        return chosenPort;
    }
}
