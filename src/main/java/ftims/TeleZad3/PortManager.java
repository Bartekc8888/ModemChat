package ftims.TeleZad3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class PortManager implements Runnable {
    private final static String INCOMING_CONNECTION = "RING";
    private final static String PHONE_CALL_ESTABLISHED = "CONNECT";
    private final static String CALL_DROPPED = "NO CARRIER";
    private final static String CONNECT_CMD_FAILED = "BUSY";
    private final static String MODEM_NORMAL_RESPONSE = "OK";
    private final static String MODEM_ERROR_RESPONSE = "ERROR";
    
    private final static String END_MESSAGE_SEQUENCE = "!*#";
    private final static String CARRIAGE_CYPHER = "\\@r";
    private final static String LINE_FEED_CYPHER = "\\@n";
    private final static String CMD_CYPHER = "A_T";
    
    private String receivedMessageBuffer = "";
    private String receivedResponseBuffer = "";
    
    private SerialPort port;
    private MainApp parent;
    
    private boolean incomingConnection = false;
    private boolean skipingEchoInProgress = false;
    private enum ResponseState { 
        None, FirstCR, FirstLF, SecondCR, SecondLF {
            @Override
            public ResponseState next() {
                return None;
            };
        };
        
        private static final ResponseState[] allValues = values();
        
        public ResponseState next() {
            return allValues[ordinal() + 1];
        }
    }
    private enum HangupState { 
        None, Timer, AwaitingCommand, AwaitingConfirmation {
            @Override
            public HangupState next() {
                return None;
            };
        };
        
        private static final HangupState[] allValues = values();
        
        public HangupState next() {
            return allValues[ordinal() + 1];
        }
    }
    private ResponseState currentResponseState = ResponseState.None;
    private HangupState currentHangupState = HangupState.None;
    
    public PortManager(PortSettings settings, MainApp parent) throws IOException {
        this.parent = parent;
        
        port = findPort(settings.getPortName());
        port.setComPortParameters(settings.getBaudRate(), settings.getNumberOfDataBits(),
                                  settings.getStopBits().getValue(), settings.getParity().getValue());
        port.setFlowControl(settings.getFlowControl().getValue());
        port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
        
        port.addDataListener(new SerialPortDataListener() {
            @Override
            public void serialEvent(SerialPortEvent event) { onDataReceived(event); }
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
        currentHangupState = HangupState.Timer;
        
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sendRawData("+++");
                currentHangupState = HangupState.AwaitingCommand;
            }
        }, 1200);
        
        new Timer().schedule(new TimerTask() { // timeout
            @Override
            public void run() {
                currentHangupState = HangupState.None;
            }
        }, 4000);
    }
    
    public synchronized boolean sendMessageNonBlocking(String message) {
        if (port.isOpen() && currentHangupState == HangupState.None) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String sanitizedMessage = sanitizeString(message);
                    sendMessage(sanitizedMessage);
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
            System.out.println("Cmd:" + command);
            
            strWriter.write(command);
            strWriter.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    private void onDataReceived(SerialPortEvent event) {
        if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
            return;
        }
        
        String receivedString = readDataFromPort();
        if (receivedString.isEmpty()) {
            return;
        }
        
        classifyAndProcessData(receivedString);
    }
    
    private String readDataFromPort() {
        byte[] newData = new byte[port.bytesAvailable()];
        int numRead = port.readBytes(newData, newData.length);
        System.out.println("Read " + numRead + " bytes.");
        
        String receivedString = "";
        try {
            receivedString = new String(newData, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        
        return receivedString;
    }
    
    private void classifyAndProcessData(String receivedString) {
        System.out.println("Msg:" + receivedString);

        if (receivedString.startsWith("AT")) { // assume echo
            skipingEchoInProgress = true;
        }
        
        if (skipingEchoInProgress && receivedString.contains("\r")) { // end of echo
            skipingEchoInProgress = false;
            int position = receivedString.indexOf("\r");
            receivedString = receivedString.substring(position + 1, receivedString.length());
        }
        
        if (skipingEchoInProgress) {
            return;
        }
        
        
        int index = -1;
        while ((index = receivedString.indexOf("\r", ++index)) != -1) {
            currentResponseState = currentResponseState.next();
        }
        while ((index = receivedString.indexOf("\n", ++index)) != -1) {
            currentResponseState = currentResponseState.next();
            
            if (currentResponseState == ResponseState.None) {
                receivedResponseBuffer = "";
                receivedString = "";
            }
        }

        if (currentResponseState != ResponseState.None) {
            processReceivedResponse(receivedString);
        } else {
            processReceivedMessage(receivedString);
        }
    }
    
    private void processReceivedResponse(String receivedString) {
        receivedResponseBuffer += receivedString;
        
        if (receivedResponseBuffer.contains(INCOMING_CONNECTION)) {
            if (!incomingConnection) {
                parent.newCallIncoming();
                incomingConnection = true;
            }
            receivedResponseBuffer = "";
            currentResponseState = currentResponseState.next();
            
        } else if (receivedResponseBuffer.contains(PHONE_CALL_ESTABLISHED)) {
            parent.callStatusChanged(true);
            receivedResponseBuffer = "";
            receivedMessageBuffer = "";
            currentResponseState = currentResponseState.next();
            
        } else if (receivedResponseBuffer.contains(CALL_DROPPED) ||
                   receivedResponseBuffer.contains(CONNECT_CMD_FAILED)) {
            receivedMessageBuffer = "";
            receivedResponseBuffer = "";
            currentResponseState = currentResponseState.next();
            currentHangupState = HangupState.None;
            parent.callStatusChanged(false);
            
        } else if (receivedResponseBuffer.contains(MODEM_NORMAL_RESPONSE)) {
            receivedResponseBuffer = "";
            currentResponseState = currentResponseState.next();
            
            if (currentHangupState == HangupState.AwaitingCommand) {
                currentHangupState = HangupState.AwaitingConfirmation;
                sendRawData("ATH" + '\r');
            } else if (currentHangupState == HangupState.AwaitingConfirmation) {
                currentHangupState = HangupState.None;
                parent.callStatusChanged(false);
            }
        } else if (receivedResponseBuffer.contains(MODEM_ERROR_RESPONSE)) {
            receivedResponseBuffer = "";
            currentResponseState = currentResponseState.next();
            
        } else if (receivedResponseBuffer.startsWith("\r\n") && 
                receivedResponseBuffer.endsWith("\r\n") &&
                receivedResponseBuffer.length() > 4) {
            receivedResponseBuffer = "";
            currentResponseState = ResponseState.None;
        }
    }
    
    private void processReceivedMessage(String receivedString) {
        String desanitizedMessage = desanitizeString(receivedString);
        receivedMessageBuffer += desanitizedMessage;
        
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
    
    private String sanitizeString(String stringToChange) {
        String sanitizedString = stringToChange.replace("\r", CARRIAGE_CYPHER);
        sanitizedString = sanitizedString.replace("\n", LINE_FEED_CYPHER);
        sanitizedString = sanitizedString.replace("AT", CMD_CYPHER);
        
        return sanitizedString;
    }
    
    private String desanitizeString(String stringToChange) {
        String desanitizedString = stringToChange.replace(CARRIAGE_CYPHER, "\r");
        desanitizedString = desanitizedString.replace(LINE_FEED_CYPHER, "\n");
        desanitizedString = desanitizedString.replace(CMD_CYPHER, "AT");
        
        return desanitizedString;
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
