/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.Pieces;

import model.Board.Board;

/**
 *
 * @author nxulu
 */
public class Knight extends Piece {

    public Knight(int x, int y, boolean iswhite, Board board, int value) {
        super(x, y, iswhite, board, value);
        this.pieceImage = PieceImages.KNIGHT;
    }

    public void intializeSide(int value) {
        super.intializeSide(value);
        if (isWhite()) {
            image = PieceImages.wn;
        } else {
            image = PieceImages.bn;
        }
    }

    public boolean canMove(int x, int y, Board board) {

        if ((board.getPiece(x, y) != null && board.getPiece(x, y).isWhite() == isWhite())) {
            return false;
        }

        if (x == xCord + 1 && y == yCord - 2) {
            return true;
        }
        if (x == xCord - 1 && y == yCord - 2) {
            return true;
        }

        if (x == xCord - 1 && y == yCord + 2) {
            return true;
        }
        if (x == xCord + 1 && y == yCord + 2) {
            return true;
        }

        if (x == xCord + 2 && y == yCord - 1) {
            return true;
        }
        if (x == xCord + 2 && y == yCord + 1) {
            return true;
        }
        if (x == xCord - 2 && y == yCord - 1) {
            return true;
        }
        if (x == xCord - 2 && y == yCord + 1) {
            return true;
        }

        return false;
    }
}
