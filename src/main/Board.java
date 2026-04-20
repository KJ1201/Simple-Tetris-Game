package main;

import main.Shape.Tetrominoe;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;

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

    private void drawSquare(Graphics g, int x, int y, Tetrominoe shape) {

        Color[] colors = {
                new Color(0, 0, 0),       new Color(204, 102, 102),
                new Color(102, 204, 102), new Color(102, 102, 204),
                new Color(204, 204, 102), new Color(204, 102, 204),
                new Color(102, 204, 204), new Color(218, 170, 0)
        };

        var color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }
}