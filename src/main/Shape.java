package main;

import java.util.Random;

public class Shape {

    protected enum Tetrominoe {
        NoShape, ZShape, SShape, LineShape,
        TShape, SquareShape, LShape, MirroredLShape
    }

    private Tetrominoe pieceShape;
    private int[][] coords;
    private int[][][] coordsTable;

    public Shape() {
        coords = new int[4][2];
        coordsTable = new int[][][] {
                { {0,0},   {0,0},   {0,0},  {0,0}  },
                { {0,-1},  {0,0},  {-1,0}, {-1,1}  },
                { {0,-1},  {0,0},   {1,0},  {1,1}  },
                { {0,-1},  {0,0},   {0,1},  {0,2}  },
                { {-1,0},  {0,0},   {1,0},  {0,1}  },
                { {0,0},   {1,0},   {0,1},  {1,1}  },
                { {-1,-1}, {0,-1},  {0,0},  {0,1}  },
                { {1,-1},  {0,-1},  {0,0},  {0,1}  }
        };
        setShape(Tetrominoe.NoShape);
    }

    protected void setShape(Tetrominoe shape) {
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 2; j++)
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
        pieceShape = shape;
    }

    public void setRandomShape() {
        int x = Math.abs(new Random().nextInt()) % 7 + 1;
        setShape(Tetrominoe.values()[x]);
    }

    void setX(int i, int x) { coords[i][0] = x; }
    void setY(int i, int y) { coords[i][1] = y; }
    public int x(int i)     { return coords[i][0]; }
    public int y(int i)     { return coords[i][1]; }
    public Tetrominoe getShape() { return pieceShape; }

    public int minY() {
        int m = coords[0][1];
        for (int i = 0; i < 4; i++) m = Math.min(m, coords[i][1]);
        return m;
    }

    public Shape rotateLeft() {
        if (pieceShape == Tetrominoe.SquareShape) return this;
        var result = new Shape();
        result.pieceShape = pieceShape;
        for (int i = 0; i < 4; i++) {
            result.setX(i,  y(i));
            result.setY(i, -x(i));
        }
        return result;
    }

    public Shape rotateRight() {
        if (pieceShape == Tetrominoe.SquareShape) return this;
        var result = new Shape();
        result.pieceShape = pieceShape;
        for (int i = 0; i < 4; i++) {
            result.setX(i, -y(i));
            result.setY(i,  x(i));
        }
        return result;
    }
}