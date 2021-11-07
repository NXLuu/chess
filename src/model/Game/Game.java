/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.Game;

/**
 *
 * @author nxulu
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import model.Board.Board;
import model.Board.Move;
import model.Pieces.Bishop;
import model.Pieces.King;
import model.Pieces.Knight;
import model.Pieces.Pawn;
import model.Pieces.Piece;
import model.Pieces.PieceImages;
import model.Pieces.Queen;
import model.Pieces.Rook;
import model.tcp.Client;
import model.tcp.Message;
import view.GameFrame;

public final class Game {

    public static Board board = new Board();

    static King wk;
    static King bk;
    static ArrayList<Piece> wPieces = new ArrayList<Piece>();
    static ArrayList<Piece> bPieces = new ArrayList<Piece>();
    static ArrayList<Piece> myPieces = new ArrayList<Piece>();
    static ArrayList<Piece> enemyPieces = new ArrayList<Piece>();

    static boolean player = true;
    static boolean mySide = true;
    public Piece active = null;
    public static boolean drag = false;
    public static ArrayList<Piece> AllPieces = new ArrayList<Piece>();
    public static boolean gameIsStarted = false;

    ArrayList<Move> allPossiblesMoves = new ArrayList<Move>();

    public static List<Move> allPlayersMove = new ArrayList<Move>();
    public static List<Move> allEnemysMove = new ArrayList<Move>();
    private static boolean gameOver = false;

    public Game(Boolean side) {
        PieceImages pieceImages = new PieceImages();
        loadFenPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        mySide = side;
        start();
    }

    public static boolean checkMyTurn() {
        return player == mySide;
    }

    public void reset() {
        
        gameOver = false;
        player = true;
        active = null;
        board = new Board();
        AllPieces = new ArrayList<>();
        allPossiblesMoves = new ArrayList<>();
        allPlayersMove = new ArrayList<>();
        allEnemysMove = new ArrayList<>();
        PieceImages pieceImages = new PieceImages();
        loadFenPosition("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        start();

    }

    public void SendMoveInfToServer(int fromX, int fromY, int x, int y, int choicePromte) {
        ArrayList inf = new ArrayList();
        inf.add(fromX);
        inf.add(fromY);
        inf.add(x);
        inf.add(y);
        inf.add(choicePromte);
        Message msg = new Message(Message.Message_Type.MovePiece);
        msg.content = inf;
        Client.Send(msg);
    }

    public static void sendLossInfor() {
        ArrayList inf = new ArrayList();
        Message msg = new Message(Message.Message_Type.CheckMate);
        msg.content = inf;
        Client.Send(msg);
        GameFrame.showFinishDiaglog(false);
    }

    public void start() {
        fillPieces();
        generatePlayersTurnMoves(board);
        generateEnemysMoves(board);
        checkPlayersLegalMoves();
    }

    public void draw(Graphics g, int x, int y, JPanel panel) {
        drawBoard(g);
        drawPiece(g, panel);
        drawPossibleMoves(g, panel);
        drag(active, x, y, g, panel);
        drawKingInCheck(mySide, g, panel);
    }

    public static void generatePlayersTurnMoves(Board board) {
        allPlayersMove = new ArrayList<Move>();
        for (Piece p : AllPieces) {
            if (p.isWhite() == mySide) {
                p.fillAllPseudoLegalMoves(board);
                allPlayersMove.addAll(p.getMoves());
            }
        }
    }

    public static void generateEnemysMoves(Board board) {
        allEnemysMove = new ArrayList<Move>();
        for (Piece p : AllPieces) {
            if (p.isWhite() != mySide) {
                p.fillAllPseudoLegalMoves(board);
                allEnemysMove.addAll(p.getMoves());
            }
        }
    }

    public static void changeSide() {
        player = !player;
        generateEnemysMoves(board);
        generatePlayersTurnMoves(board);
        checkPlayersLegalMoves();
    }

    public void randomPlay() {
        if (gameOver) {
            return;
        }
        if (!player) {
            Random r = new Random();
            active = bPieces.get(r.nextInt(bPieces.size()));
            while (active.getMoves().isEmpty()) {
                active = bPieces.get(r.nextInt(bPieces.size()));
            }
            Move m = active.getMoves().get(r.nextInt(active.getMoves().size()));
            move(m.getToX(), m.getToY());
        }
    }

    public void selectPiece(int x, int y) {
        if (!checkMyTurn() || !gameIsStarted) {
            return;
        }
        if (active == null && board.getPiece(x, y) != null && board.getPiece(x, y).isWhite() == mySide) {
            active = board.getPiece(x, y);
        }
    }

    public  void opponentCheckMate() {
        if (mySide) {
            for (Piece p : wPieces) {
                if (!p.getMoves().isEmpty()) {
                    return;
                }
            }
        } else {
            for (Piece p : bPieces) {
                if (!p.getMoves().isEmpty()) {
                    return;
                }
            }
        }
        gameOver = true;
        sendLossInfor();
        reset();
    }

    public static void checkMate() {
        if (!mySide) {
            for (Piece p : wPieces) {
                if (!p.getMoves().isEmpty()) {
                    return;
                }
            }
        } else {
            for (Piece p : bPieces) {
                if (!p.getMoves().isEmpty()) {
                    return;
                }
            }
        }
        if (!mySide) {
            if (wk.isInCheck()) {
                JOptionPane.showMessageDialog(null, "check mate " + (!player ? "white" : "black") + " wins");
            } else {
                JOptionPane.showMessageDialog(null, "stalemate ");

            }
        } else {
            if (bk.isInCheck()) {
                JOptionPane.showMessageDialog(null, "check mate " + (!player ? "white" : "black") + " wins");
            } else {
                JOptionPane.showMessageDialog(null, "stalemate ");

            }
        }
        gameOver = true;
    }

    public static void checkPlayersLegalMoves() {
        List<Piece> pieces = null;
        if (mySide) {
            pieces = wPieces;
        } else {
            pieces = bPieces;
        }
        for (Piece p : pieces) {
            checkLegalMoves(p);
        }
    }

    public static void checkLegalMoves(Piece piece) {
        List<Move> movesToRemove = new ArrayList<Move>();
        Board clonedBoard = board.getNewBoard();
        Piece clonedActive = piece.getClone();

        for (Move move : clonedActive.getMoves()) {
            clonedBoard = board.getNewBoard();
            clonedActive = piece.getClone();

            clonedActive.makeMove(move.getToX(), move.getToY(), clonedBoard);

            List<Piece> enemyPieces = new ArrayList<Piece>();
            Piece king = null;

            if (piece.isWhite()) {
                enemyPieces = bPieces;
                king = wk;
            } else {
                enemyPieces = wPieces;
                king = bk;
            }

            for (Piece enemyP : enemyPieces) {

                Piece clonedEnemyPiece = enemyP.getClone();
                clonedEnemyPiece.fillAllPseudoLegalMoves(clonedBoard);

                for (Move bMove : clonedEnemyPiece.getMoves()) {
                    if (!(clonedActive instanceof King) && bMove.getToX() == king.getXcord()
                            && bMove.getToY() == king.getYcord()
                            && clonedBoard.getGrid()[enemyP.getXcord()][enemyP.getYcord()] == enemyP
                            .getValueInTheboard()) {
                        movesToRemove.add(move);
                    } else if (clonedActive instanceof King) {
                        if (bMove.getToX() == clonedActive.getXcord() && bMove.getToY() == clonedActive.getYcord()
                                && clonedBoard.getGrid()[enemyP.getXcord()][enemyP.getYcord()] == enemyP
                                .getValueInTheboard()) {
                            movesToRemove.add(move);
                        }
                    }
                }

            }

        }

        for (Move move : movesToRemove) {
            piece.getMoves().remove(move);
        }
    }

    public void drag(Piece piece, int x, int y, Graphics g, JPanel panel) {
        if (piece != null && drag == true) {
            piece.draw2(g, player, x, y, panel);
        }
    }

    public void move(int x, int y) {
        if (active != null) {

            int lastX = active.getXcord();
            int lastY = active.getYcord();
            if (active.makeMove(x, y, board)) {
                int choicePromte = tryToPromote(active);
                SendMoveInfToServer(lastX, lastY, x, y, choicePromte);
                changeSide();
                checkMate();
                active = null;
            }
            drag = false;

        }
    }

    public void opponentMove(int fromX, int fromY, int x, int y, int choicePromte) {
        for (Piece p : AllPieces) {
            if (p.getXcord() == fromX && p.getYcord() == fromY) {
                p.makeMove(x, y, board);
                if (choicePromte != -1) {
                    choosePiece(p, choicePromte);
                }
//                tryToPromote(active);
                changeSide();
                opponentCheckMate();
                return;
            }
        }
    }

    public void drawKingInCheck(boolean isWhite, Graphics g, JPanel panel) {
        g.setColor(Color.RED);
        if (wk.isInCheck()) {
            g.drawRect(wk.getXcord() * Piece.size, wk.getYcord() * Piece.size, Piece.size, Piece.size);
        } else if (bk.isInCheck()) {
            g.drawRect(bk.getXcord() * Piece.size, bk.getYcord() * Piece.size, Piece.size, Piece.size);
        }
        panel.revalidate();
        panel.repaint();
    }

    public void drawBoard(Graphics g) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 1) {
                    g.setColor(new Color(118, 150, 86));
                } else {
                    g.setColor(new Color(238, 238, 210));
                }
                g.fillRect(i * Piece.size, j * Piece.size, Piece.size, Piece.size);
            }
        }
    }

    public int tryToPromote(Piece p) {
        if (p instanceof Pawn) {
            if (((Pawn) p).madeToTheEnd()) {
                return choosePiece(p, showMessageForPromotion());
            }
        }
        return -1;
    }

    public int showMessageForPromotion() {
        Object[] options = {"Queen", "Rook", "Knight", "Bishop"};

        drag = false;
        return JOptionPane.showOptionDialog(null, "Choose Piece To Promote to", null, JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }

    public static int choosePiece(Piece p, int choice) {

        switch (choice) {
            case 0:
                AllPieces.remove(p);
                p = new Queen(p.getXcord(), p.getYcord(), p.isWhite(), board, p.isWhite() ? 8 : -8);
                AllPieces.add(p);
                break;
            case 1:
                AllPieces.remove(p);
                p = new Rook(p.getXcord(), p.getYcord(), p.isWhite(), board, p.isWhite() ? 5 : -5);
                AllPieces.add(p);
                break;
            case 2:
                AllPieces.remove(p);
                p = new Knight(p.getXcord(), p.getYcord(), p.isWhite(), board, p.isWhite() ? 3 : -3);
                AllPieces.add(p);
                break;
            case 3:
                AllPieces.remove(p);
                p = new Bishop(p.getXcord(), p.getYcord(), p.isWhite(), board, p.isWhite() ? 3 : -3);
                AllPieces.add(p);
                break;
            default:
                AllPieces.remove(p);
                p = new Queen(p.getXcord(), p.getYcord(), p.isWhite(), board, p.isWhite() ? 8 : -8);
                AllPieces.add(p);
                break;
        }
        fillPieces();
        return choice;
    }

    public void drawPossibleMoves(Graphics g, JPanel panel) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
        if (active != null) {
            active.showMoves(g2, panel);
        }

    }

    public void drawPiece(Graphics g, JPanel panel) {
        for (Piece p : AllPieces) {
            p.draw(g, false, panel);
        }

    }

    public void loadFenPosition(String fenString) {
        String[] parts = fenString.split(" ");
        String position = parts[0];
        int row = 0, col = 0;
        for (char c : position.toCharArray()) {
            if (c == '/') {
                row++;
                col = 0;
            }
            if (Character.isLetter(c)) {
                if (Character.isLowerCase(c)) {
                    addToBoard(col, row, c, false);
                } else {
                    addToBoard(col, row, c, true);
                }
                col++;
            }
            if (Character.isDigit(c)) {
                col += Integer.parseInt(String.valueOf(c));
            }
        }

//        if (parts[1].equals("w")) {
//            player = true;
//        } else {
//            player = false;
//        }
    }

    public static void fillPieces() {
        wPieces = new ArrayList<Piece>();
        bPieces = new ArrayList<Piece>();
        enemyPieces = new ArrayList<Piece>();
        myPieces = new ArrayList<Piece>();
        for (Piece p : AllPieces) {
            if (p.isWhite()) {
                wPieces.add(p);
            } else {
                bPieces.add(p);
            }
        }

        for (Piece p : AllPieces) {
            if (p.isWhite() == mySide) {
                myPieces.add(p);
            } else {
                enemyPieces.add(p);
            }
        }
    }

    public void addToBoard(int x, int y, char c, boolean isWhite) {
        switch (String.valueOf(c).toUpperCase()) {
            case "R":
                AllPieces.add(new Rook(x, y, isWhite, board, isWhite ? 5 : -5));
                break;
            case "N":
                AllPieces.add(new Knight(x, y, isWhite, board, isWhite ? 3 : -3));
                break;
            case "B":
                AllPieces.add(new Bishop(x, y, isWhite, board, isWhite ? 3 : -3));
                break;
            case "Q":
                AllPieces.add(new Queen(x, y, isWhite, board, isWhite ? 8 : -8));
                break;
            case "K":
                King king = new King(x, y, isWhite, board, isWhite ? 10 : -10);
                AllPieces.add(king);
                if (isWhite) {
                    wk = king;
                } else {
                    bk = king;
                }
                break;
            case "P":
                AllPieces.add(new Pawn(x, y, isWhite, board, isWhite ? 1 : -1));
                break;
        }
    }

}
