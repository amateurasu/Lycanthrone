package vn.elite.fundamental.gui.console;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

public class Console extends WindowAdapter implements WindowListener, ActionListener, Runnable {
    private final PipedInputStream pin = new PipedInputStream();
    private final PipedInputStream pin2 = new PipedInputStream();
    Thread errorThrower; // just for testing (Throws an Exception at this ConsoleGUI
    private JFrame frame;
    private JTextArea textArea;
    private Thread reader;
    private Thread reader2;
    private boolean quit;

    public Console() {
        // create all components and add them
        frame = new JFrame("Java ConsoleGUI");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = new Dimension(screenSize.width / 2, screenSize.height / 2);
        int x = frameSize.width / 2;
        int y = frameSize.height / 2;
        frame.setBounds(x, y, frameSize.width, frameSize.height);

        textArea = new JTextArea();
        textArea.setEditable(false);
        JButton button = new JButton("clear");

        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.getContentPane().add(button, BorderLayout.SOUTH);
        frame.setVisible(true);

        frame.addWindowListener(this);
        button.addActionListener(this);

        try {
            PipedOutputStream pout = new PipedOutputStream(this.pin);
            System.setOut(new PrintStream(pout, true));
        } catch (IOException | SecurityException io) {
            textArea.append("Couldn't redirect STDOUT to this console\n" + io.getMessage());
        }

        try {
            PipedOutputStream pout2 = new PipedOutputStream(this.pin2);
            System.setErr(new PrintStream(pout2, true));
        } catch (IOException | SecurityException io) {
            textArea.append("Couldn't redirect STDERR to this console\n" + io.getMessage());
        }

        quit = false; // signals the Threads that they should exit

        // Starting two seperate threads to read from the PipedInputStreams
        //
        reader = new Thread(this);
        reader.setDaemon(true);
        reader.start();
        //
        reader2 = new Thread(this);
        reader2.setDaemon(true);
        reader2.start();

        // testing part
        // you may omit this part for your application
        //
        System.out.println("Hello World 2");
        System.out.println("All fonts available to Graphic2D:\n");
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontNames = ge.getAvailableFontFamilyNames();
        for (int n = 0; n < fontNames.length; n++) {
            System.out.println(fontNames[n]);
        }
        // Testing part: simple an error thrown anywhere in this JVM will be printed on the ConsoleGUI
        // We do it with a seperate Thread becasue we don't wan't to break a Thread used by the ConsoleGUI.
        System.out.println("\nLets throw an error on this console");
        errorThrower = new Thread(this);
        errorThrower.setDaemon(true);
        errorThrower.start();
    }

    public static void main(String[] arg) {
        new Console(); // create console with not reference
    }

    @Override
    public synchronized void windowClosed(WindowEvent evt) {
        quit = true;
        this.notifyAll(); // stop all threads
        try {
            reader.join(1000);
            pin.close();
        } catch (Exception ignored) {
        }
        try {
            reader2.join(1000);
            pin2.close();
        } catch (Exception ignored) {
        }
        System.exit(0);
    }

    @Override
    public synchronized void windowClosing(WindowEvent evt) {
        frame.setVisible(false); // default behaviour of JFrame
        frame.dispose();
    }

    @Override
    public synchronized void actionPerformed(ActionEvent evt) {
        textArea.setText("");
    }

    @Override
    public synchronized void run() {
        try {
            while (Thread.currentThread() == reader) {
                try {
                    this.wait(100);
                } catch (InterruptedException ignored) {
                }
                if (pin.available() != 0) {
                    String input = this.readLine(pin);
                    textArea.append(input);
                }
                if (quit) {
                    return;
                }
            }

            while (Thread.currentThread() == reader2) {
                try {
                    this.wait(100);
                } catch (InterruptedException ignored) {
                }
                if (pin2.available() != 0) {
                    String input = this.readLine(pin2);
                    textArea.append(input);
                }
                if (quit) {
                    return;
                }
            }
        } catch (Exception e) {
            textArea.append("\nConsoleGUI reports an Internal error.");
            textArea.append("The error is: " + e);
        }

        // just for testing (Throw a Nullpointer after 1 second)
        if (Thread.currentThread() == errorThrower) {
            try {
                this.wait(1000);
            } catch (InterruptedException ignored) {
            }
            throw new NullPointerException("Application test: throwing an NullPointerException It should arrive at the console");
        }
    }

    public synchronized String readLine(PipedInputStream in) throws IOException {
        String input = "";
        do {
            int available = in.available();
            if (available == 0) {
                break;
            }
            byte[] b = new byte[available];
            in.read(b);
            input = input + new String(b, 0, b.length);
        } while (!input.endsWith("\n") && !input.endsWith("\r\n") && !quit);
        return input;
    }
}
