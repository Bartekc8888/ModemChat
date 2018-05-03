package ftims.TeleZad3;

import java.util.ArrayList;
import java.util.EnumSet;

import com.fazecast.jSerialComm.SerialPort;

public class PortSettings {
    public enum Parity {
        NO_PARITY(SerialPort.NO_PARITY),
        ODD_PARITY(SerialPort.ODD_PARITY),
        EVEN_PARITY(SerialPort.EVEN_PARITY),
        MARK_PARITY(SerialPort.MARK_PARITY),
        SPACE_PARITY(SerialPort.SPACE_PARITY);
        
        private final int value;
        private Parity(int id) { this.value = id; }
        public int getValue() { return value; }
        
        public static ArrayList<Parity> getListOfValues() {
            ArrayList<Parity> list = new ArrayList<Parity>(EnumSet.allOf(Parity.class));
            return list;
        }
        
        @Override
        public String toString() {
            return this.name();
        }
    }
    
    public enum StopBits {
        ONE_STOP_BIT(SerialPort.ONE_STOP_BIT),
        ONE_POINT_FIVE_STOP_BITS(SerialPort.ONE_POINT_FIVE_STOP_BITS),
        TWO_STOP_BITS(SerialPort.TWO_STOP_BITS);
        
        private final int value;
        private StopBits(int id) { this.value = id; }
        public int getValue() { return value; }
        
        public static ArrayList<StopBits> getListOfValues() {
            ArrayList<StopBits> list = new ArrayList<StopBits>(EnumSet.allOf(StopBits.class));
            return list;
        }
        
        @Override
        public String toString() {
            return this.name();
        }
    }
    
    public enum FlowControl {
        FLOW_CONTROL_CTS_ENABLED(SerialPort.FLOW_CONTROL_CTS_ENABLED),
        FLOW_CONTROL_DISABLED(SerialPort.FLOW_CONTROL_DISABLED),
        FLOW_CONTROL_DSR_ENABLED(SerialPort.FLOW_CONTROL_DSR_ENABLED),
        FLOW_CONTROL_DTR_ENABLED(SerialPort.FLOW_CONTROL_DTR_ENABLED),
        FLOW_CONTROL_RTS_ENABLED(SerialPort.FLOW_CONTROL_RTS_ENABLED),
        FLOW_CONTROL_XONXOFF_IN_ENABLED(SerialPort.FLOW_CONTROL_XONXOFF_IN_ENABLED),
        FLOW_CONTROL_XONXOFF_OUT_ENABLED(SerialPort.FLOW_CONTROL_XONXOFF_OUT_ENABLED),
        FLOW_CONTROL_RTS_CTS(SerialPort.FLOW_CONTROL_RTS_ENABLED | SerialPort.FLOW_CONTROL_CTS_ENABLED),
        FLOW_CONTROL_DTR_DSR(SerialPort.FLOW_CONTROL_DTR_ENABLED | SerialPort.FLOW_CONTROL_DSR_ENABLED);
        
        private final int value;
        private FlowControl(int id) { this.value = id; }
        public int getValue() { return value; }
        
        public static ArrayList<FlowControl> getListOfValues() {
            ArrayList<FlowControl> list = new ArrayList<FlowControl>(EnumSet.allOf(FlowControl.class));
            return list;
        }
        
        @Override
        public String toString() {
            return this.name();
        }
    }
    
    private String portName;
    private int baudRate;
    private int numberOfDataBits;
    private Parity parity;
    private StopBits stopBits;
    private FlowControl flowControl;
    
    PortSettings(String portName, int baudRate, int numberOfDataBits, Parity parity,
                 StopBits stopBits, FlowControl flowControl) {
        this.portName = portName;
        this.baudRate = baudRate;
        this.numberOfDataBits = numberOfDataBits;
        this.parity = parity;          
        this.stopBits = stopBits;      
        this.flowControl = flowControl;
    }

    public String getPortName() {
        return portName;
    }
    
    public int getBaudRate() {
        return baudRate;
    }

    public int getNumberOfDataBits() {
        return numberOfDataBits;
    }

    public Parity getParity() {
        return parity;
    }

    public StopBits getStopBits() {
        return stopBits;
    }

    public FlowControl getFlowControl() {
        return flowControl;
    }
}
