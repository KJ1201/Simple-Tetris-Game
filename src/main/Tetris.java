package main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Tetris extends JFrame {

    private JLabel statusbar;

    public Tetris() {
        initUI();
    }

    private void initUI() {
        statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.SOUTH);

        setTitle("Java Project - 24BCS10791, 24BCS10403, 24BCS107XX, 24BCS107XX");
        setSize(600, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    JLabel getStatusBar() { return statusbar; }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new Tetris());
    }
}