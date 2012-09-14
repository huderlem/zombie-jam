import java.util.ArrayList;
import java.util.Hashtable;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;


public abstract class Entity {
	
	Entity parent;
	Hashtable<Integer, Entity> children = new Hashtable<Integer, Entity>();
	
	Vector2f position;
	Image texture;
	protected Shape mask;
	
	private int id;
	float width;
	float height;
	double scale = 1.0;
	
	boolean deleted = false;
	
	
	public Entity(Entity parent)
	{
		this.parent = parent;
		id = EntityManager.registerEntity(this);
		
		initTextures();
	}
	
	public void update(GameContainer gc, int delta, GameplayState game) {
	}
	public void render(Graphics g) {
	}
	protected abstract void initTextures();
	public abstract Shape getMask();
	
	public void setParent(Entity parent) {
		this.parent = parent;
	}
	
	public void addChild(Entity child) {
		children.put(child.id(), child);
	}
	
	public int id()
	{
		return this.id;
	}
	
	public Vector2f getCenterPos() {
		return new Vector2f(position.x + width/2.0f, position.y + height/2.0f);
	}
	
	public void delete() {
		deleted = true;
		EntityManager.removeEntity(this);
	}
		
}
