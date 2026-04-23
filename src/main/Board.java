package main;

import main.Shape.Tetrominoe;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Board extends JPanel {

    private final int BOARD_WIDTH     = 10;
    private final int BOARD_HEIGHT    = 22;
    private final int PERIOD_INTERVAL = 300;

    private static final int[] LINE_SCORES = { 0, 100, 300, 500, 800 };

    private Timer   timer;
    private boolean isFallingFinished = false;
    private boolean isPaused          = false;
    private boolean isGameOver        = false;

    private int score           = 0;
    private int level           = 1;
    private int numLinesRemoved = 0;
    private int curX            = 0;
    private int curY            = 0;

    private JLabel       statusbar;
    private Shape        curPiece;
    private Tetrominoe[] board;

    public Board(Tetris parent) {
        setFocusable(true);
        setBackground(new Color(15, 15, 25));
        statusbar = parent.getStatusBar();
        addKeyListener(new TAdapter());
    }

    void start() {
        isGameOver        = false;
        isFallingFinished = false;
        isPaused          = false;
        score             = 0;
        level             = 1;
        numLinesRemoved   = 0;

        curPiece = new Shape();
        board    = new Tetrominoe[BOARD_WIDTH * BOARD_HEIGHT];
        clearBoard();
        newPiece();

        if (timer != null) timer.stop();
        timer = new Timer(PERIOD_INTERVAL, new GameCycle());
        timer.start();

        updateStatusBar();
    }

    private void updateStatusBar() {
        statusbar.setText(String.format(
                "  Score: %d   Level: %d   Lines: %d",
                score, level, numLinesRemoved));
    }

    private void pause() {
        isPaused = !isPaused;
        statusbar.setText(isPaused
                ? "  PAUSED — press P to resume"
                : String.format("  Score: %d   Level: %d   Lines: %d",
                score, level, numLinesRemoved));
        repaint();
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
            timer.stop();
            isGameOver = true;
            repaint();
        }
    }

    private boolean tryMove(Shape piece, int newX, int newY) {
        for (int i = 0; i < 4; i++) {
            int x = newX + piece.x(i);
            int y = newY - piece.y(i);
            if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) return false;
            if (shapeAt(x, y) != Tetrominoe.NoShape)                      return false;
        }
        curPiece = piece;
        curX     = newX;
        curY     = newY;
        repaint();
        return true;
    }

    private void pieceDropped() {
        for (int i = 0; i < 4; i++) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
        }
        removeFullLines();
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

    private void removeFullLines() {
        int numFullLines = 0;

        for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean lineIsFull = true;
            for (int j = 0; j < BOARD_WIDTH; j++)
                if (shapeAt(j, i) == Tetrominoe.NoShape) { lineIsFull = false; break; }

            if (lineIsFull) {
                numFullLines++;
                for (int k = i; k < BOARD_HEIGHT - 1; k++)
                    for (int j = 0; j < BOARD_WIDTH; j++)
                        board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            score           += LINE_SCORES[numFullLines] * level;
            level            = numLinesRemoved / 10 + 1;
            timer.setDelay(Math.max(50, PERIOD_INTERVAL - (level - 1) * 25));
            updateStatusBar();
            isFallingFinished = true;
            curPiece.setShape(Tetrominoe.NoShape);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        doDrawing(g2);
        if (isPaused)   drawPauseOverlay(g2);
        if (isGameOver) drawGameOverOverlay(g2);
    }
    private void doDrawing(Graphics2D g) {
        int sw  = squareWidth();
        int sh  = squareHeight();
        int boardLeft = (getWidth()  - BOARD_WIDTH  * sw) / 2;  // center horizontally
        int boardTop  = (getHeight() - BOARD_HEIGHT * sh) / 2;  // center vertically

        // Grid cells — drawn inside exact board bounds
        g.setColor(new Color(30, 30, 48));
        for (int i = 0; i < BOARD_HEIGHT; i++)
            for (int j = 0; j < BOARD_WIDTH; j++)
                g.drawRect(boardLeft + j * sw,
                        boardTop  + i * sh,
                        sw, sh);

        // Board border
        g.setColor(new Color(70, 70, 110));
        g.setStroke(new java.awt.BasicStroke(2f));
        g.drawRect(boardLeft, boardTop, BOARD_WIDTH * sw, BOARD_HEIGHT * sh);
        g.setStroke(new java.awt.BasicStroke(1f));

        // Placed blocks
        for (int i = 0; i < BOARD_HEIGHT; i++)
            for (int j = 0; j < BOARD_WIDTH; j++) {
                Tetrominoe shape = shapeAt(j, BOARD_HEIGHT - i - 1);
                if (shape != Tetrominoe.NoShape)
                    drawSquare(g,
                            boardLeft + j * sw,
                            boardTop  + i * sh,
                            shape);
            }

        // Active piece
        if (curPiece.getShape() != Tetrominoe.NoShape)
            for (int i = 0; i < 4; i++) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g,
                        boardLeft + x * sw,
                        boardTop  + (BOARD_HEIGHT - y - 1) * sh,
                        curPiece.getShape());
            }
    }
    private void drawSquare(Graphics g, int x, int y, Tetrominoe shape) {
        Color[] colors = {
                new Color(0,0,0),       new Color(204,102,102),
                new Color(102,204,102), new Color(102,102,204),
                new Color(204,204,102), new Color(204,102,204),
                new Color(102,204,204), new Color(218,170,0)
        };
        Color color = colors[shape.ordinal()];

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

    private void drawGameOverOverlay(Graphics2D g) {
        int w = getWidth(), h = getHeight();

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.78f));
        g.setColor(new Color(8, 8, 18));
        g.fillRect(0, 0, w, h);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        g.setFont(new Font("Monospaced", Font.BOLD, 24));
        FontMetrics fm = g.getFontMetrics();
        String title = "GAME OVER";
        g.setColor(new Color(220, 60, 60));
        g.drawString(title, (w - fm.stringWidth(title)) / 2, h / 2 - 55);

        g.setColor(new Color(80, 30, 30));
        g.drawLine(w / 4, h / 2 - 40, 3 * w / 4, h / 2 - 40);

        g.setFont(new Font("Monospaced", Font.BOLD, 16));
        fm = g.getFontMetrics();
        String scoreLine = "Score  " + score;
        g.setColor(Color.WHITE);
        g.drawString(scoreLine, (w - fm.stringWidth(scoreLine)) / 2, h / 2 - 12);

        g.setFont(new Font("Monospaced", Font.PLAIN, 14));
        fm = g.getFontMetrics();
        String levelLine = "Level  " + level;
        g.setColor(new Color(160, 160, 210));
        g.drawString(levelLine, (w - fm.stringWidth(levelLine)) / 2, h / 2 + 14);

        String linesLine = "Lines  " + numLinesRemoved;
        g.drawString(linesLine, (w - fm.stringWidth(linesLine)) / 2, h / 2 + 40);

        g.setFont(new Font("Monospaced", Font.ITALIC, 12));
        fm = g.getFontMetrics();
        String hint = "[ R ] play again";
        g.setColor(new Color(90, 90, 130));
        g.drawString(hint, (w - fm.stringWidth(hint)) / 2, h / 2 + 80);
    }

    private void drawPauseOverlay(Graphics2D g) {
        int w = getWidth(), h = getHeight();

        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.55f));
        g.setColor(new Color(8, 8, 18));
        g.fillRect(0, 0, w, h);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        g.setFont(new Font("Monospaced", Font.BOLD, 22));
        FontMetrics fm = g.getFontMetrics();
        String text = "PAUSED";
        g.setColor(new Color(100, 180, 255));
        g.drawString(text, (w - fm.stringWidth(text)) / 2, h / 2);

        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        fm = g.getFontMetrics();
        String sub = "[ P ] resume";
        g.setColor(new Color(80, 100, 140));
        g.drawString(sub, (w - fm.stringWidth(sub)) / 2, h / 2 + 24);
    }

    private class GameCycle implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if (isPaused) return;
            if (isFallingFinished) { isFallingFinished = false; newPiece(); }
            else oneLineDown();
            repaint();
        }
    }

    class TAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            if (isGameOver) {
                if (e.getKeyCode() == KeyEvent.VK_R) start();
                return;
            }
            if (curPiece.getShape() == Tetrominoe.NoShape) return;
            switch (e.getKeyCode()) {
                case KeyEvent.VK_P     -> pause();
                case KeyEvent.VK_LEFT  -> tryMove(curPiece, curX - 1, curY);
                case KeyEvent.VK_RIGHT -> tryMove(curPiece, curX + 1, curY);
                case KeyEvent.VK_DOWN  -> oneLineDown();
                case KeyEvent.VK_Z     -> tryMove(curPiece.rotateLeft(),  curX, curY);
                case KeyEvent.VK_X     -> tryMove(curPiece.rotateRight(), curX, curY);
                case KeyEvent.VK_UP    -> tryMove(curPiece.rotateLeft(),  curX, curY);
                case KeyEvent.VK_SPACE -> dropDown();
                case KeyEvent.VK_D     -> oneLineDown();
            }
        }
    }
}