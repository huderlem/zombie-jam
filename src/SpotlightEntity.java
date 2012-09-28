import org.lwjgl.opengl.GL11;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;


public class SpotlightEntity extends Entity {
	
	float radius = 100f;
	Image alphaMap;
	
	public SpotlightEntity(Entity parent, float x, float y) {
		super(parent);
		EntityManager.addSpotlightEntity(this);
		
		position = new Vector2f(x, y);
		width = 32.0f;
		height = 32.0f;
		mask = new Circle(position.x, position.y, radius);
	}

	@Override
	public void update(GameContainer gc, int delta, GameplayState game) {
		super.update(gc, delta, game);
		
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		g.drawImage(alphaMap, position.x-radius, position.y-radius, position.x+radius, position.y+radius, 0, 0, alphaMap.getWidth(), alphaMap.getHeight());
		
	}

	@Override
	protected void initTextures() {
		// TODO Auto-generated method stub
		alphaMap = TextureManager.getTexture("textures/soft-circle.png");
	}

	private void setMask() {
		mask.setLocation(position);
	}
	
	@Override
	public Shape getMask() {
		return mask;
	}

}
