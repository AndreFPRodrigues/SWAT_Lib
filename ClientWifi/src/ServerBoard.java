import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
public class ServerBoard extends JFrame {
    private JTextArea messagesArea;
    private JButton sendButton;
    private JTextField message;
    private JButton startServer;
 
    public ServerBoard() {
  
        super("ServerBoard");
 
        JPanel panelFields = new JPanel();
        panelFields.setLayout(new BoxLayout(panelFields,BoxLayout.X_AXIS));
 
        JPanel panelFields2 = new JPanel();
        panelFields2.setLayout(new BoxLayout(panelFields2,BoxLayout.X_AXIS));
 
        //here we will have the text messages screen
        messagesArea = new JTextArea();
        messagesArea.setColumns(30);
        messagesArea.setRows(10);
        messagesArea.setEditable(false);
        JScrollPane sp = new JScrollPane(messagesArea);   // JTextArea is placed in a JScrollPane.

        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get the message from the text view
                String messageText = message.getText();
                // add message to the message area
                messagesArea.append("\n" + messageText);
                // send the message to the client
                TestingWifi.sendMessage(messageText);
                // clear text
                message.setText("");
            }
        });
        /*
        startServer = new JButton("Start");
        startServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // disable the start button
                startServer.setEnabled(false);
 
                
 
            }
        });*/
 
        //the box where the user enters the text (EditText is called in Android)
        message = new JTextField();
        message.setSize(200, 20);
 
        //add the buttons and the text fields to the panel
        panelFields.add(sp);
       // panelFields.add(startServer);
 
        panelFields2.add(message);
        panelFields2.add(sendButton);
 
        getContentPane().add(panelFields);
        getContentPane().add(panelFields2);
 
        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));
 
        setSize(300, 170);
        setVisible(true);
    }
    
    public void addText(String text){
    	 messagesArea.append("\n" + text);
    }
    
}