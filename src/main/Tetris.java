package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Tetris extends JFrame {

    private JLabel statusbar;

    public Tetris() {
        initUI();
    }

    private void initUI() {
        statusbar = new JLabel("  Score: 0   Level: 1   Lines: 0");
        statusbar.setFont(new Font("Monospaced", Font.PLAIN, 13));
        statusbar.setForeground(new Color(180, 180, 200));
        statusbar.setBackground(new Color(20, 20, 35));
        statusbar.setOpaque(true);
        statusbar.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
        add(statusbar, BorderLayout.SOUTH);

        var board = new Board(this);
        add(board);
        board.start();

        setTitle("Tetris");
        setSize(400, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    JLabel getStatusBar() { return statusbar; }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new Tetris());
    }
}