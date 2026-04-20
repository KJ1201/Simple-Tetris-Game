package main;

public class Shape {

    protected enum Tetromino {
        NoShape, ZShape, SShape, LineShape,
        TShape, SquareShape, LShape, MirroredLShape
    }

    private Tetromino pieceShape;

    public Shape() {
        pieceShape = Tetromino.NoShape;
    }

    public Tetromino getShape() { return pieceShape; }
}