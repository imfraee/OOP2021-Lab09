package it.unibo.oop.lab.reactivegui03;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import it.unibo.oop.lab.reactivegui02.ConcurrentGUI;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

public class AnotherConcurrentGUI extends JFrame {

    private static final long serialVersionUID = 1L;
    private static final double WIDTH = 0.2;
    private static final double HEIGHT = 0.1;
    
    
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");

    final Agent agent = new Agent();
    
    public AnotherConcurrentGUI() {
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
        
        
        up.addActionListener(e -> agent.upCounting());
        down.addActionListener(e -> agent.downCounting());
        stop.addActionListener(e -> {
            agent.stopCounting();
            stop.setEnabled(false);
            up.setEnabled(false);
            down.setEnabled(false);
        });
        new Thread(agent).start();
        new Thread(new TimerAgent()).start();
    }
    
    private class TimerAgent implements Runnable {
        
        private static final int SLEEP_TIME = 10_000;

        @Override
        public void run() {
            try {
                Thread.sleep(SLEEP_TIME);
                AnotherConcurrentGUI.this.stop.doClick();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        
    }
    
    private class Agent implements Runnable {

        private volatile boolean stop;
        private volatile boolean up = true;
        private volatile int counter;
        
        @Override
        public void run() {
            while(!this.stop) {
                try {
                    counter += up? 1: -1;
                    final var nextText = Integer.toString(counter);
                    SwingUtilities.invokeAndWait(new Runnable() {

                        @Override
                        public void run() {
                            AnotherConcurrentGUI.this.display.setText(nextText);
                        } 
                    });
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
        
        private void stopCounting() {
            this.stop = true;
        }
        
        private void upCounting() {
            this.up = true;
        }
        
        private void downCounting() {
            this.up = false;
        }
        
    }

}
