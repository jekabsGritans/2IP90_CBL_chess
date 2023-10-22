import java.awt.Color;

import engine.ChessPiece;

public class FloorTileEntity extends Entity {
    public FloorTileEntity(byte floorColor) {
        super();
        Color graphicColor = ChessPiece.isColor(floorColor, ChessPiece.Black) ? Color.BLACK : Color.WHITE;
        setColor(graphicColor);
    }
}
