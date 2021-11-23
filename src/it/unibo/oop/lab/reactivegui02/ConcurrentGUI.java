package it.unibo.oop.lab.reactivegui02;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH = 0.2;
    private static final double HEIGHT = 0.1;
    
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");

    public ConcurrentGUI() {
        super();
        final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getWidth() * WIDTH), (int) (screenSize.getHeight() * HEIGHT));
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        final JPanel panel = new JPanel();
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        final Agent agent = new Agent();
        up.addActionListener(e -> agent.upCount());
        down.addActionListener(e -> agent.downCount());
        stop.addActionListener(e -> {
            agent.stopCounting();
            stop.setEnabled(false);
            up.setEnabled(false);
            down.setEnabled(false);
        });
        new Thread(agent).start();        
    }
    
    private class Agent implements Runnable {
        
        private volatile boolean stop;
        private volatile boolean up = true;
        private volatile int counter;

        @Override
        public void run() {
            while(!this.stop) {
                try {
                    counter += up? 1 : -1;
                    SwingUtilities.invokeAndWait(new Runnable() {
                        
                        @Override
                        public void run() {
                            ConcurrentGUI.this.display.setText(Integer.toString(Agent.this.counter));
                            
                        }
                    });
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
                
            }
        }
        
        private void upCount() {
            this.up = true;
        }
        
        private void downCount() {
            this.up = false;
        }
        
        private void stopCounting() {
            this.stop = true;
        }
    }

}
