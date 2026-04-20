package main;

import main.Shape.Tetrominoe;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Board extends JPanel {

    private final int BOARD_WIDTH  = 10;
    private final int BOARD_HEIGHT = 22;

    private JLabel statusbar;
    private Shape curPiece;
    private Tetrominoe[] board;
    private int curX = 0;
    private int curY = 0;

    public Board(Tetris parent) {
        setFocusable(true);
        statusbar = parent.getStatusBar();
    }

    private int squareWidth()  { return (int) getSize().getWidth()  / BOARD_WIDTH;  }
    private int squareHeight() { return (int) getSize().getHeight() / BOARD_HEIGHT; }

    private Tetrominoe shapeAt(int x, int y) {
        return board[(y * BOARD_WIDTH) + x];
    }
}