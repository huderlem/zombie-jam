import java.util.Hashtable;

import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;


public class TextureManager {

	static Hashtable<String, Image> textures = new Hashtable<String, Image>();
		
	
	private static Image loadTexture(String filepath) {
		Image texture;
		try {
			texture = new Image(filepath);
		} catch (SlickException e) {
			e.printStackTrace();
			texture = null;
		}
		
		textures.put(filepath, texture);
		return texture;
	}
	
	public static Image getTexture(String filepath) {
		if ( !textures.containsKey(filepath) ) {
			return loadTexture(filepath);
		} else
			return textures.get(filepath);
	}
	
	
	
	
}
