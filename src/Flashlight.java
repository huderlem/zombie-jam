import java.util.ArrayList;
import java.util.Arrays;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.ShapeFill;
import org.newdawn.slick.geom.Line;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.ImageBuffer;
import org.newdawn.slick.BigImage;



public class Flashlight extends Entity {

	private Vector2f direction = new Vector2f(1f, 0f);
	int power = 10;
	float reach = 16f;
	private float angle = 20;
		
	Graphics g = new Graphics();
	
	
	public Flashlight(Entity parent) {
		super(parent);
		position = new Vector2f(parent.position.x + parent.width/2, parent.position.y + parent.height/2);
		
		setMask();
	}

	@Override
	public void update(GameContainer gc, int delta, GameplayState game) {
		super.update(gc, delta, game);
		
		position.set(parent.position.x + parent.width/2, parent.position.y + parent.height/2);
		setDirection(game);
		setMask();
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		
		g.fill(getMask());
	}

	@Override
	protected void initTextures() {
		texture = TextureManager.getTexture("textures/bomb.png");
	}
	
	public void setDirection(GameplayState game) {
		float xDiff = game.input.getAbsoluteMouseX() - position.x;
		float yDiff = game.input.getAbsoluteMouseY() - position.y;
		direction = new Vector2f(xDiff, yDiff);
	}

	@Override
	public Shape getMask() {
		return mask;
	}
	
	private void setMask() {
		Vector2f beam = direction.copy().normalise().scale(power*reach);
		Vector2f p1 = beam.copy().add(angle).add(position);
		Vector2f p2 = beam.copy().sub(angle).add(position);
		
		Polygon mask = new Polygon();
		mask.addPoint(position.x, position.y);
		mask.addPoint(p1.x, p1.y);
		mask.addPoint(p2.x, p2.y);
		mask.addPoint(position.x, position.y);
		this.mask = mask;
	}
	
}
