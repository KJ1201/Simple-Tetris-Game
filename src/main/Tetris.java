package main;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class Tetris extends JFrame {

    public Tetris() {
        setVisible(true);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> new Tetris());
    }
}