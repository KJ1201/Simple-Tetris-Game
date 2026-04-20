package main;

import main.Shape.Tetrominoe;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;

public class Board extends JPanel {

    private final int BOARD_WIDTH  = 10;
    private final int BOARD_HEIGHT = 22;

    private boolean isFallingFinished = false;
    private JLabel statusbar;
    private Shape curPiece;
    private Tetrominoe[] board;
    private int curX = 0;
    private int curY = 0;

    public Board(Tetris parent) {
        setFocusable(true);
        statusbar = parent.getStatusBar();
    }

    void start() {
        curPiece = new Shape();
        board    = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];
        clearBoard();
        newPiece();
    }

    private int squareWidth()  { return (int) getSize().getWidth()  / BOARD_WIDTH;  }
    private int squareHeight() { return (int) getSize().getHeight() / BOARD_HEIGHT; }

    private Tetrominoe shapeAt(int x, int y) {
        return board[(y * BOARD_WIDTH) + x];
    }

    private void clearBoard() {
        for (int i = 0; i < BOARD_HEIGHT * BOARD_WIDTH; i++)
            board[i] = Tetrominoe.NoShape;
    }

    private void newPiece() {
        curPiece.setRandomShape();
        curX = BOARD_WIDTH / 2 + 1;
        curY = BOARD_HEIGHT - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetrominoe.NoShape);
            statusbar.setText("Game over");
        }
    }

    private boolean tryMove(Shape piece, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int x = newX + piece.x(i);
            int y = newY - piece.y(i);
            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT)
                return false;
            if (shapeAt(x, y) != Tetrominoe.NoShape)
                return false;
        }
        curPiece = piece;
        curX     = newX;
        curY     = newY;
        repaint();
        return true;
    }

    // ── NEW in this commit ────────────────────────────────────────────────

    private void pieceDropped() {
        for (int i = 0; i < 4; i++) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
        }
        if (!isFallingFinished) newPiece();
    }

    private void oneLineDown() {
        if (!tryMove(curPiece, curX, curY - 1)) pieceDropped();
    }

    private void dropDown() {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1)) break;
            newY--;
        }
        pieceDropped();
    }

    // ── End of additions ──────────────────────────────────────────────────

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        doDrawing(g);
    }

    private void doDrawing(Graphics g) {

        int boardTop = (int) getSize().getHeight() - BOARD_HEIGHT * squareHeight();

        for (int i = 0; i < BOARD_HEIGHT; i++)
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Tetrominoe shape = shapeAt(j, BOARD_HEIGHT - i - 1);
                if (shape != Tetrominoe.NoShape)
                    drawSquare(g, j * squareWidth(),
                            boardTop + i * squareHeight(), shape);
            }

        if (curPiece.getShape() != Tetrominoe.NoShape)
            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, x * squareWidth(),
                        boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(),
                        curPiece.getShape());
            }
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