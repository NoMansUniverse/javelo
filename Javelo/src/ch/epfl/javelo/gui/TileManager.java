package ch.epfl.javelo.gui;

import javafx.scene.image.Image;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
/**
 * @author Robin Bochatay (329724)
 */
public final class TileManager {
    private final Path PATH;
    private final String SERVERNAME;
    private Map<TileId, Image> memoryCache= new LinkedHashMap<>(100,(float) 0.75, true);

    public TileManager(Path path, String serverName){
        PATH=path;
        SERVERNAME=serverName;

    }

    public record TileId(int zoomLevel, int indexX, int indexY){


        public static boolean isValid(int zoomLevel, int indexX, int indexY){

            if(zoomLevel>=0 && indexX>=0 && indexY>=0){
                    return true;
            }
            return false;
        }

    }

    /**
     * download image for openstreetmap server if not existent in cache:
     * cache is either program memory or if more than 100 tiles in memory, tile is stored on disk
     * @param tileId identity of the tile wanted
     * @return image of the tileId, null if neither in identity is not valid
     * @throws IOException if file cannot be read
     */
    public Image imageForTileAt(TileId tileId) throws IOException {
        if(TileId.isValid(tileId.zoomLevel(), tileId.indexX(), tileId.indexY())){

            if(memoryCache.containsKey(tileId)){

                return memoryCache.get(tileId);
            }

            Path localCache= PATH.resolve(tileId.zoomLevel()+"\\"+ tileId.indexX()+"\\"+tileId.indexY()+".png");

           if(Files.exists(localCache)){

                try(InputStream s = new FileInputStream(localCache.toString())){

                    Image image = new Image(s);
                    put(tileId, image);

                    return image;
                }
             }
           else {

                StringBuilder j = new StringBuilder();
                j.append("https://tile.openstreetmap.org/" +
                        tileId.zoomLevel() + "/" + tileId.indexX() + "/" + tileId.indexY() + ".png");

                URL u = new URL(j.toString());
                URLConnection c = u.openConnection();
                c.setRequestProperty("User-Agent", "JaVelo");

                try (InputStream i = c.getInputStream()) {
                    byte[] array = i.readAllBytes();

                    Path tilePath = Path.of(PATH.toString(), String.valueOf(tileId.zoomLevel()),
                            String.valueOf(tileId.indexX()));
                    Image image = new Image(new ByteArrayInputStream(array));
                    Files.createDirectories(tilePath);
                    Path imagePath = Path.of(String.valueOf(tilePath), String.valueOf(tileId.indexY()) + ".png");

                    try (OutputStream o = new FileOutputStream(imagePath.toString())) {
                        InputStream stream = new ByteArrayInputStream(array);
                        stream.transferTo(o);
                        put(tileId, image);
                        return image;
                    }
                    }
                }
            }
            return null;
        }

    /**
     * put in the cache (hash map) the tile identity (key) and image (value). If hash map contains 100 images already, remove least used image from cache
     * @param tileId
     * @param image
     */
        private void put(TileId tileId, Image image){
            if(memoryCache.size()>=99){
                memoryCache.remove(memoryCache.keySet().iterator().next());
            }
            memoryCache.put(tileId, image);
        }
    }

