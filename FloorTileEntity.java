import java.awt.Color;
import java.io.File;

import engine.ChessPiece;

public class FloorTileEntity extends Entity {
    public FloorTileEntity(byte floorColor) {
        super();
        String tilePath = System.getProperty("user.dir") + "\\textures\\";
        tilePath += ChessPiece.isColor(floorColor, ChessPiece.Black) ? "blackTile.png" : "whiteTile.png";
        File tileFile = new File(tilePath);
        loadTexture(tileFile);
    }
}
