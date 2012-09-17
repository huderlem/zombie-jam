import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;


public class WallEntity extends Entity {
	
	Image texture;
	
	public WallEntity(Entity parent, float x, float y) {
		super(parent);
		EntityManager.addWallEntity(this);
		width = 20f;
		height = 20.0f;
		
		position = new Vector2f(x, y);
		mask = new Rectangle(x, y, width, height);
	}
	
	
	@Override
	public void update(GameContainer gc, int delta, GameplayState game) {
		super.update(gc, delta, game);
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		texture.draw(position.x, position.y, width, height);
	}


	@Override
	protected void initTextures() {
		texture = TextureManager.getTexture("textures/wall.png");
	}


	@Override
	public Shape getMask() {
		return mask;
	}

}
