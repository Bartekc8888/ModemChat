package ftims.TeleZad3;

import com.fazecast.jSerialComm.SerialPort;

public class Main 
{
    public static void main(String[] args)
    {
        SerialPort[] ports = SerialPort.getCommPorts();
        
        for (SerialPort port : ports) {
            System.out.println(port.getSystemPortName());
        }
    }
}
