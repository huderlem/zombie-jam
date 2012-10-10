import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import org.lwjgl.opengl.GL11;
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
	int power;
	float reach;
	private float angle;
	
	private int numRays = 600;
	private float rayDelta = 5.0f;
	private Polygon[] rays = new Polygon[numRays-1];
	
		
	Graphics g = new Graphics();
	
	
	public Flashlight(Entity parent) {
		super(parent);
		position = new Vector2f(parent.position.x + parent.width/2, parent.position.y + parent.height/2);
		power = new Integer(GameplayState.prop.getProperty("flashlightPower"));
		reach = new Float(GameplayState.prop.getProperty("flashlightReach"));
		angle = new Integer(GameplayState.prop.getProperty("flashlightAngle"));
		extendRays();
	}

	@Override
	public void update(GameContainer gc, int delta, GameplayState game) {
		super.update(gc, delta, game);		
		position.set(parent.position.x + parent.width/2, parent.position.y + parent.height/2);
		setDirection(game);
		extendRays();
	}

	@Override
	public void render(Graphics g) {
		super.render(g);
		
		//g.fill(getMask());
		for (Polygon light : this.rays) {
			if (light != null) {
				g.fill(light);
			}
		}
		
	}

	@Override
	protected void initTextures() {
	}
	
	public void setDirection(GameplayState game) {
		float xDiff = game.input.getAbsoluteMouseX() - position.x;
		float yDiff = game.input.getAbsoluteMouseY() - position.y;
		direction = new Vector2f(xDiff, yDiff);
	}

	@Override
	//this method doesn't really make sense for flashlight. should never be used.
	public Shape getMask() {
		return null;
	}
	//instead, use this! illuminated, question mark?
	public Polygon illuminated(Entity target){
		for (Polygon lightMask : rays) {
			if (lightMask.intersects(target.getMask())) return lightMask;
		}
		return null;
	}
	private void setMask() {

	}
	
	/*
	 * Extends several rays out from the flashlight to handle lighting.  Also construct the light masks used for actually rendering the light/shadows.
	 */
	private void extendRays() {
		// angle between each individual ray
		double rayAngleSpacing = (2*angle)/((double)(numRays-1));
		Vector2f prevRay = null;
		for (int i = 0; i < numRays; i++) {
			Vector2f ray = new Vector2f(position.x, position.y);
			
			Vector2f rayDirection = direction.copy().sub(angle).add(rayAngleSpacing*i).normalise().scale(this.rayDelta);
			// Actually move the ray...
			ray = progressRay(ray, rayDirection);
			// Create a Polygon mask from the last two rays
			if (i > 0) {
				Polygon rayChunk = new Polygon();
				rayChunk.addPoint(position.x, position.y);
				rayChunk.addPoint(prevRay.x, prevRay.y);
				rayChunk.addPoint(ray.x, ray.y);
				this.rays[i-1] = rayChunk;
			}
			prevRay = ray.copy();
		}
	}
	 
	/*
	 * Extends an individual ray forward until it hits and passes through a wall or hits its max length.  Tells GridSpaces to be drawn when it hits them.
	 */
	private Vector2f progressRay(Vector2f ray, Vector2f rayDirection) {
		float length = 0f;
		int hitWallID = -1;
		GameplayState game = GameplayState.getGameplayState();
		// keep extending the ray forward until it's run out of power
		while (length < power*reach) {
			ray.add(rayDirection);
			GridSpace contactedGridSpace = ((GridSpace)EntityManager.getEntity(game.terrain.getCellContents(ray.x, ray.y)));
			if (contactedGridSpace != null) {
				// tell the GridSpace that the ray is touching to be drawn this frame
				contactedGridSpace.setShouldBeDrawn(true);
			
				// If the ray hits a wall, we light up that wall and end the ray
				if (hitWallID == -1 && contactedGridSpace.type == 1) {
					contactedGridSpace.illuminateWall();
					break;
				}
				
				length += this.rayDelta;
			} else {
				break;
			}
		}
		return ray;
	}
	
	
	
}
