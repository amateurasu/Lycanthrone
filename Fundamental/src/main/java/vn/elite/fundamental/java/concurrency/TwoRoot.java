package vn.elite.fundamental.java.concurrency;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;

/**
 * @link https://stackoverflow.com/questions/4637215
 */
public class TwoRoot extends JFrame {

    private static final String s = "0.000000000000000";
    private JProgressBar progressBar = new JProgressBar(0, 100);
    private JLabel label = new JLabel(s, JLabel.CENTER);

    public TwoRoot() {
        this.setLayout(new GridLayout(0, 1));
        this.setTitle("√2");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.add(progressBar);
        this.add(label);
        this.setSize(161, 100);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            TwoRoot t = new TwoRoot();
            t.runCalc();
        });
    }

    public void runCalc() {
        progressBar.setIndeterminate(true);
        TwoWorker task = new TwoWorker();
        task.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("progress")) {
                progressBar.setIndeterminate(false);
                progressBar.setValue((Integer) e.getNewValue());
            }
        });
        task.execute();
    }

    private class TwoWorker extends SwingWorker<Double, Double> {

        private static final int N = 5;
        private final DecimalFormat df = new DecimalFormat(s);
        double x = 1;

        @Override
        protected Double doInBackground() throws Exception {
            for (int i = 1; i <= N; i++) {
                x = x - (x * x - 2) / (2 * x);
                setProgress(i * (100 / N));
                publish(x);
                Thread.sleep(1000); // simulate latency
            }
            return x;
        }

        @Override
        protected void process(List<Double> chunks) {
            for (double d : chunks) {
                label.setText(df.format(d));
            }
        }
    }
}
