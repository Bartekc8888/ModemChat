package ftims.TeleZad3;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class PortManager implements Runnable {
    private final static String INCOMING_CONNECTION = "RING";
    private final static String PHONE_CALL_ESTABLISHED = "CONNECT";
    private final static String CALL_DROPPED = "NO CARRIER";
    private final static String MODEM_NORMAL_RESPONSE = "OK";
    private final static String MODEM_ERROR_RESPONSE = "ERROR";
    
    private final static String END_MESSAGE_SEQUENCE = "!*#";
    private String receivedMessageBuffer = "";
    
    private SerialPort port;
    private MainApp parent;
    
    private boolean incomingConnection = false;
    
    public PortManager(PortSettings settings, MainApp parent) throws IOException {
        this.parent = parent;
        
        port = findPort(settings.getPortName());
        port.setComPortParameters(settings.getBaudRate(), settings.getNumberOfDataBits(),
                                  settings.getStopBits().getValue(), settings.getParity().getValue());
        port.setFlowControl(settings.getFlowControl().getValue());
        port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
        
        port.addDataListener(new SerialPortDataListener() {
            @Override
            public void serialEvent(SerialPortEvent event) { messageReceived(event); }
            @Override
            public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
        });
    }
    
    public void callPhone(String phoneNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRawData("ATD" + phoneNumber + '\r');
            }
        }).start();
    }
    
    public void answerCall() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRawData("ATA" + '\r');
                incomingConnection = false;
            }
        }).start();
    }
    
    public void declineCall() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendRawData("ATH" + '\r');
                incomingConnection = false;
            }
        }).start();
    }
    
    public synchronized void hangUpCall() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                port.clearDTR(); // hangUp
                parent.callStatusChanged(false);
            }
        }).start();
    }
    
    public synchronized boolean sendMessageNonBlocking(String message) {
        if (port.isOpen()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendMessage(message);
                }
            }).start();
            return true;
        } else {
            return false;
        }
    }
    
    public synchronized boolean sendRawCommandNonBlocking(String command) {
        if (port.isOpen()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sendRawData(command + '\r');
                }
            }).start();
            return true;
        } else {
            return false;
        }
    }
    
    private synchronized void sendMessage(String message) {
        if (sendRawData(message + END_MESSAGE_SEQUENCE)) {
            System.out.println("Message sent: " + message);
            parent.messageSendingResult(true, message);
        } else {
            parent.messageSendingResult(false, message);
        }
    }
    
    private synchronized boolean sendRawData(String command) {
        OutputStream stream = port.getOutputStream();
        OutputStreamWriter strWriter = new OutputStreamWriter(stream, StandardCharsets.UTF_8);
        
        try {
            strWriter.write(command);
            strWriter.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    private void messageReceived(SerialPortEvent event) {
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
            return;
        }
        
        byte[] newData = new byte[port.bytesAvailable()];
        int numRead = port.readBytes(newData, newData.length);
        System.out.println("Read " + numRead + " bytes.");
        
        String receivedString = "";
        try {
            receivedString = new String(newData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        receivedString = receivedString.replace("\r\n", "");
        if (receivedString.isEmpty()) {
            return;
        }
        
        switch (receivedString) {
        case INCOMING_CONNECTION:
            if (!incomingConnection) {
                parent.newCallIncoming();
                incomingConnection = true;
            }
            break;
        case PHONE_CALL_ESTABLISHED:
            parent.callStatusChanged(true);
            break;
        case CALL_DROPPED:
            parent.callStatusChanged(false);
            receivedMessageBuffer = "";
            break;
        case MODEM_NORMAL_RESPONSE:
            break;
        case MODEM_ERROR_RESPONSE:
            break;
        default:
            receivedMessageBuffer += receivedString;
            
            if (receivedMessageBuffer.contains(END_MESSAGE_SEQUENCE)) {
                String[] receivedMessages = receivedMessageBuffer.split(Pattern.quote(END_MESSAGE_SEQUENCE));
                
                for (int i = 0; i > receivedMessages.length - 1; i++) {
                    parent.newMessageReceived(receivedMessages[i]);
                }
                
                if (receivedMessageBuffer.endsWith(END_MESSAGE_SEQUENCE)) {
                    parent.newMessageReceived(receivedMessages[receivedMessages.length - 1]);
                    receivedMessageBuffer = "";
                } else {
                    receivedMessageBuffer = receivedMessages[receivedMessages.length - 1];
                }
            }
            break;
        }
    }
    
    public synchronized void closePort() {
        if (port.closePort()) {
            parent.portStatusChanged(false);
            System.out.println("Port closed");
        }
    }
    
    private synchronized boolean openPort() {
        return port.openPort();
    }
    
    private synchronized boolean establishConnection() {
        return false;
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

    @Override
    public void run() {
        if (openPort()) {
            System.out.println("port opened");
            if (establishConnection()) {
                System.out.println("connection established");
            }
            
            parent.portStatusChanged(true);
        }
    }
}
