import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
 
public class GameplayState extends BasicGameState{
 
	
	Input input = null;
	World terrain;
	Properties prop = new Properties();
	static PlayerEntity player;
 
	private int stateID = -1;
	
	public int numSurvivors = 5;
	public int survivorsLeft = numSurvivors;
	public int numZombies = 2;
	
	Random rand = new Random();
	
	
	public GameplayState(int stateID) {
		this.stateID = stateID;
	}
    
    public void keyPressed(int key, char c) {
    	player.handleKeyPress(key, c);
    }
    
    public void keyReleased(int key, char c) {
    	player.handleKeyRelease(key, c);
    }
    
    public void mouseClicked(int button, int x, int y, int clickCount) {
    	player.handleMouseClicked(button, x, y, clickCount);
    }
    

	@Override
	public void init(GameContainer gc, StateBasedGame sbg)
			throws SlickException {
		try {
			prop.load(new FileInputStream("config/game_settings.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    	input = gc.getInput();
    	terrain = new World(20, 20, new Integer(prop.getProperty("cellWidth")));
    	terrain.generateWalls();
    	new ZombieEntity(null, 300f, 100f);
    	player = new PlayerEntity(null, 200f, 100f);
		
    	initLevel();
    	
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		// Build an alpha map out of the flashlight
    	g.setDrawMode(Graphics.MODE_ALPHA_MAP);
    	g.clearAlphaMap();
    	
    	g.setColor(Color.black);
    	
    	player.flashlight.render(g);
    	g.fill(player.light);
    	
    	for (SpotlightEntity e : EntityManager.spotlightEntities.values()) {
    		if (e.deleted == false)
    			e.render(g);
    	}
    	
    	g.setDrawMode(Graphics.MODE_ALPHA_BLEND);
    	
    	terrain.renderFloor(g);
    	//g.fill(player.flashlight.getMask());
    	
    	for (WallEntity e : EntityManager.wallEntities.values()) {
    		if (e.deleted == false)
    			e.render(g);
    	}
    	
    	g.setDrawMode(Graphics.MODE_NORMAL);
    	
    	for (ZombieEntity e : EntityManager.zombieEntities.values()) {
    		if (e.deleted == false)
    			e.render(g);
    	}
    	
    	for (SurvivorEntity e : EntityManager.survivorEntities.values()) {
    		if (e.deleted == false)
    			e.render(g);
    	}
    
    	player.render(g);
		g.drawString(""+player.score, 0, 200);
	}

	@Override
	public void update(GameContainer gc, StateBasedGame sbg, int delta)
			throws SlickException {
		for (Entity e : EntityManager.entities.values()) {
    		if (e.deleted == false)
    			e.update(gc, delta, this);
    	}
    	
    	EntityManager.syncEntities();
    	
    	/*if (this.survivorsLeft <= 0) {
    		survivorsLeft = this.numSurvivors;
    		clearLevel();
    		initLevel();
    	}*/
    	
	}

	@Override
	public int getID() {
		return this.stateID;
	}
	
	public void initLevel() {
		ArrayList<Vector2f> takenPoints = new ArrayList<Vector2f>();
		for (int a = 0; a < numSurvivors; a++) {
			int i = rand.nextInt(terrain.terrainGridWidth-2) + 1;
			int j = rand.nextInt(terrain.terrainGridHeight-2) + 1;
			Vector2f loc = new Vector2f(i, j);
			while (!locTaken(loc, takenPoints) && terrain.getCellContents((int)i, (int)j) != 0) {
				i = rand.nextInt(terrain.terrainGridWidth-2) + 1;
				j = rand.nextInt(terrain.terrainGridHeight-2) + 1;
				loc.set(i, j);
			}
			takenPoints.add(loc);
			new SurvivorEntity(null, i*terrain.terrainCellWidth, j*terrain.terrainCellWidth);
		}
		for (int a = 0; a < numZombies; a++) {
			int i = rand.nextInt(terrain.terrainGridWidth-2) + 1;
			int j = rand.nextInt(terrain.terrainGridHeight-2) + 1;
			Vector2f loc = new Vector2f(i, j);
			while (!locTaken(loc, takenPoints) && terrain.getCellContents((int)i, (int)j) != 0) {
				i = rand.nextInt(terrain.terrainGridWidth-2) + 1;
				j = rand.nextInt(terrain.terrainGridHeight-2) + 1;
				loc.set(i, j);
			}
			takenPoints.add(loc);
			new ZombieEntity(null, i*terrain.terrainCellWidth, j*terrain.terrainCellWidth);
		}
		
		
	}
	
	private boolean locTaken(Vector2f loc, ArrayList<Vector2f> locs) {
		for (Vector2f l : locs) {
			if (loc.equals(l)) {
				return true;
			}
		}
		return false;
	}
	
	public void clearLevel() {
		EntityManager.clear();
	}
	
    
    
}