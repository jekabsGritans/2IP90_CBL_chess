import java.awt.Color;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.ImageIcon;

import engine.ChessPiece;

public class FloorTileEntity extends Entity implements MouseListener {
    public static Color blackTile = Color.decode("#596A37");
    public static Color whiteTile = Color.decode("#FFFFE3");
    public ChessScene board;
    /**
     * Constructor that calls entity constructor and also sets appropriate color
     * @param floorColor the color to which the file is set, depending on bits xxx??xxx
     */
    public FloorTileEntity(byte floorColor) {
        super();
        graphic.setOpaque(true);
        setColor(ChessPiece.isColor(floorColor, ChessPiece.White) ? whiteTile : blackTile);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    /**
     * Since tiles are also part of the "board" they also count for the board mouseReleased
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        board.mouseReleased = true;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
