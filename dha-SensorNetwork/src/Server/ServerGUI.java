package Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import static Commons.Constants.*;

/**
 * GUI server class
 */
public class ServerGUI {
    private JTextArea infoPanel;
    private JPanel mainFrame;

    private static JButton refreshButton;
    private static Server server;
    private static JFrame frame;

    /**
     * Constructor, instantiates the server and starts it as a separate thread
     */
    private ServerGUI() {
        server = new Server(this);
        new Thread(server).start();
    }

    /**
     * GUI main
     *
     * @param args args
     */
    public static void main(String[] args) {
        frame = new JFrame("ServerGUI");
        frame.setContentPane(new ServerGUI().mainFrame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                server.stopProtocol();
            }
        });

        refreshButton.addActionListener(e -> {
            try {
                server.sendHello();
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(null, "Error in sending the datagram!", "Error!", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Updates the devices view (the devices are sorted in order to put first the ones that sent ALIVE more recently)
     *
     * @param devices a collection of Device objects
     */
    void updateDevices(Collection<Device> devices) {
        LinkedList<Device> devicesList = new LinkedList<Device>(devices);
        Collections.sort(devicesList);
        infoPanel.setText("");
        StringBuilder tmp = new StringBuilder();
        for (Device d : devicesList) {
            tmp.append(parseType(d.getType())).append(":\n\tLast confirm: ").append(parseInterval(d.getLastCommunication())).append("\n\tLast value sent: ").append(parseValue(d.getLastValueSent())).append("\n\n");
        }
        infoPanel.setText(tmp.toString());
    }

    /**
     * Parse the interval to show it, if the latest communication is more than 30 seconds, than shows "DISCONNECTED",
     * otherwise shows how much time passed from the last communication
     *
     * @param lastCommunication an Instant instance
     * @return a String to display
     */
    private String parseInterval(Instant lastCommunication) {
        long now = Instant.now().toEpochMilli();
        if (now - lastCommunication.toEpochMilli() > 30000) return "DISCONNECTED";
        return Long.toString((now - lastCommunication.toEpochMilli()) / 1000);

    }

    /**
     * Parses the value of a Device to display it
     *
     * @param value the value to display
     * @return a String to display
     */
    private String parseValue(Integer value) {
        if (value == null) return "No value sent!";
        return value.toString();
    }

    /**
     * Parses the type to make it a String
     *
     * @param type an int of the Constants interface
     * @return a String to display
     */
    private String parseType(int type) {
        switch (type) {
            case WASHING_MACHINE:
                return "Washing Machine";

            case FRIDGE:
                return "Fridge";

            case LIGHT_BULB:
                return "Light bulb";

            case THERMOSTAT:
                return "Thermostat";

            case OVEN:
                return "Oven";

            case FISH_TANK:
                return "Fish tank";

            case TV:
                return "TV";

            default:
                return "Device";
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainFrame = new JPanel();
        mainFrame.setLayout(new GridBagLayout());
        mainFrame.setFocusTraversalPolicyProvider(false);
        mainFrame.setFocusable(true);
        mainFrame.setMinimumSize(new Dimension(300, 300));
        mainFrame.setPreferredSize(new Dimension(600, 300));
        final JScrollPane scrollPane1 = new JScrollPane();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        mainFrame.add(scrollPane1, gbc);
        infoPanel = new JTextArea();
        infoPanel.setMinimumSize(new Dimension(575, 275));
        infoPanel.setPreferredSize(new Dimension(575, 275));
        scrollPane1.setViewportView(infoPanel);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainFrame;
    }

}
