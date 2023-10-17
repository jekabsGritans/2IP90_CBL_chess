import java.awt.Color;

import engine.board.ChessPiece.PieceColor;

public class FloorTileEntity extends Entity {
    public FloorTileEntity(PieceColor floorColor) {
        super();
        Color graphicColor = floorColor == PieceColor.BLACK ? Color.BLACK : Color.WHITE;
        setColor(graphicColor);
    }
}
