/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.Pieces;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import model.Board.Board;
import model.Board.Move;
import model.Game.Game;

/**
 *
 * @author nxulu
 */
public abstract class Piece implements Cloneable, Serializable {

    protected int xCord;
    protected int yCord;
    protected boolean isWhite;
    protected boolean isAlive;
    protected int valueInTheBoard;
    protected Board board;
    protected String pieceImage;
    protected Color pieceColor;
    public static int size = 80;
    protected List<Move> moves = new ArrayList<>();
    protected Image image;

    

    public Piece(int x, int y, boolean iswhite, Board board, int value) {
        this.xCord = x;
        this.yCord = y;
        this.isWhite = iswhite;
        isAlive = true;
        this.board = board;
        intializeSide(value);
        board.setPieceIntoBoard(x, y, this);
    }

    public boolean makeMove(int toX, int toY, Board board) {
        Move move = new Move(xCord, yCord, toX, toY, this);
        if (!isAlive) {
            return false;
        }

        for (Move m : moves) {
            if (m.compareTo(move) == 0) {
                board.updatePieces(xCord, yCord, toX, toY, this);
                xCord = toX;
                yCord = toY;
                return true;
            }
        }

        return false;
    }

    public abstract boolean canMove(int x, int y, Board board);

    public boolean alive() {
        int positionInBoard = board.getXY(xCord, yCord);
        if (positionInBoard != valueInTheBoard || positionInBoard == 0 || board.getPiece(xCord, yCord) == null) {
            isAlive = false;
            Game.AllPieces.remove(getClass());
        }
        return isAlive;
    }

    public void intializeSide(int value) {
        if (isWhite) {
            pieceColor = PieceImages.WHITECOLOR;
        } else {
            pieceColor = PieceImages.BLACKCOLOR;
        }
        valueInTheBoard = value;
    }

    public void showMoves(Graphics g, JPanel panel) {

        Graphics2D g2 = (Graphics2D) g;

        for (Move m : moves) {
            if (board.getPiece(m.getToX(), m.getToY()) != null && board.getPiece(m.getToX(), m.getToY()).isWhite() != isWhite()) {
                g.setColor(Color.ORANGE);
            } else {
                g.setColor(Color.DARK_GRAY);
            }
            g.fillOval((m.getToX() * size) + size / 3, (m.getToY() * size) + size / 3, size / 3, size / 3);
            g2.setColor(Color.DARK_GRAY);
            if (Game.drag) {
                g2.fillRect(m.getFromX() * size, m.getFromY() * size, size, size);
            } else {
                g2.drawRect(m.getFromX() * size, m.getFromY() * size, size, size);
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    public void draw(Graphics g, boolean drag, JPanel panel) {
        g.drawImage(image, xCord * Piece.size, yCord * Piece.size, Piece.size, Piece.size, panel);
        panel.revalidate();
        panel.repaint();
    }

    public void draw2(Graphics g, boolean player, int x, int y, JPanel panel) {
        g.drawImage(image, x - Piece.size / 2, y - Piece.size / 2, Piece.size, Piece.size, panel);
        panel.revalidate();
        panel.repaint();
    }

    public void fillAllPseudoLegalMoves(Board b) {
        moves = new ArrayList<Move>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (canMove(i, j, b)) {
                    moves.add(new Move(xCord, yCord, i, j, this));
                }
            }
        }
    }

    public int getXcord() {
        return xCord;
    }

    public void setXcord(int xcord) {
        this.xCord = xcord;
    }

    public int getYcord() {
        return yCord;
    }

    public void setYcord(int ycord) {
        this.yCord = ycord;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setValueInTheboard(int value) {
        this.valueInTheBoard = value;
    }

    public int getValueInTheboard() {
        return valueInTheBoard;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    public Piece getClone() {
        try {
            return (Piece) this.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
