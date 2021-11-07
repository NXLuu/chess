/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.Pieces;

/**
 *
 * @author nxulu
 */
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class PieceImages {

    static Color WHITECOLOR = Color.WHITE;
    static Color BLACKCOLOR = Color.BLACK;
    static String PAWN = "♟";
    static String ROOK = "♜";
    static String KNIGHT = "♞";
    static String BISHOP = "♝";
    static String QUEEN = "♛";
    static String KING = "♚";

    static BufferedImage wk;
    static BufferedImage bk;
    static BufferedImage wr;
    static BufferedImage br;
    static BufferedImage wq;
    static BufferedImage bq;
    static BufferedImage wb;
    static BufferedImage bb;
    static BufferedImage wn;
    static BufferedImage bn;
    static BufferedImage wp;
    static BufferedImage bp;

    public PieceImages() {
        try {
            wk = ImageIO.read(new File(new File("src/asset/images/wk.png").getCanonicalPath()));
            bk = ImageIO.read(new File(new File("src/asset/images/bk.png").getCanonicalPath()));
            wr = ImageIO.read(new File(new File("src/asset/images/wr.png").getCanonicalPath()));
            br = ImageIO.read(new File(new File("src/asset/images/br.png").getCanonicalPath()));
            wq = ImageIO.read(new File(new File("src/asset/images/wq.png").getCanonicalPath()));
            bq = ImageIO.read(new File(new File("src/asset/images/bq.png").getCanonicalPath()));
            wb = ImageIO.read(new File(new File("src/asset/images/wb.png").getCanonicalPath()));
            bb = ImageIO.read(new File(new File("src/asset/images/bb.png").getCanonicalPath()));
            wn = ImageIO.read(new File(new File("src/asset/images/wn.png").getCanonicalPath()));
            bn = ImageIO.read(new File(new File("src/asset/images/bn.png").getCanonicalPath()));
            wp = ImageIO.read(new File(new File("src/asset/images/wp.png").getCanonicalPath()));
            bp = ImageIO.read(new File(new File("src/asset/images/bp.png").getCanonicalPath()));
        } catch (IOException e) {
        }
    }
}
