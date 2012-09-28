import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;
import java.util.Random;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.*;
import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
 
public class GameplayState extends BasicGameState{
 
	
	Input input = null;
	World terrain;
	Properties prop = new Properties();
	static PlayerEntity player;
 
	private int stateID = -1;
	
	//a bunch of default values that get overridden by the config file
	public int numSurvivors = 1;
	public int survivorsLeft = numSurvivors;
	public int numZombies = 1;	
	
	public int worldXdim = 20;
	public int worldYdim = 20;
	
	Random rand = new Random();
	
	Image alphaMap;
	Graphics alphaG;
		
	
	public GameplayState(int stateID) {
		this.stateID = stateID;
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
		//should we have a try except block and throw a nice error when stuff is missing, or just
		//assume settings will always be properly formatted?
		numZombies=new Integer(prop.getProperty("numZombies"));
		numSurvivors=new Integer(prop.getProperty("numSurvivors"));
		worldXdim=new Integer(prop.getProperty("worldXdim"));
		worldYdim=new Integer(prop.getProperty("worldYdim"));
		
    	input = gc.getInput();

		terrain = new World(worldXdim, worldYdim, new Integer(prop.getProperty("cellWidth")));
		terrain.generateRoom();
    	player = new PlayerEntity(null, 20f, 20f, prop);		
    	initLevel();
    	
    	alphaMap = new Image(gc.getWidth(), gc.getHeight());
    	alphaG = alphaMap.getGraphics();
    	
	}

	@Override
	public void render(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		
		// To create the lighting effects, several steps must happen.
		// First, render_base() draws the entities and the environment.  Next, an alpha map 
		// is created using the player's flashlight and other various things.  Lastly, a black
		// rectangle is drawn over everything, but the rectangle is combined with the created alpha lighting
		// to create the desired effects.
		render_base(gc, sbg, g);
    	render_alpha_map(gc, sbg, g); // comment both of these lines out to turn off lighting
    	render_lighting(gc, sbg, g);
    	
    	// Display elements such as the score
    	render_ui(gc, sbg, g);
	}
	
	/*
	 * Renders the game's entities and environment
	 */
	private void render_base(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		g.setDrawMode(Graphics.MODE_NORMAL);
    	
    	for (GridSpace e : EntityManager.gridSpaceEntities.values()) {
    		if (e.deleted == false)
    			e.render(g);
    	}
    	
    	for (ZombieEntity e : EntityManager.zombieEntities.values()) {
    		if (e.deleted == false)
    			e.render(g);
    	}
    	
    	for (SurvivorEntity e : EntityManager.survivorEntities.values()) {
    		if (e.deleted == false)
    			e.render(g);
    	}
    	player.render(g);
	}
	
	/*
	 * Create an alpha map used to overlay the lighting effect.
	 */
	private void render_alpha_map(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		g.setDrawMode(Graphics.MODE_ALPHA_MAP);
    	g.clearAlphaMap();
    	
    	//GL11.glBlendFunc(GL11.GL_ZERO, GL11.GL_ZERO);

    	alphaG.clear();
    	
    	alphaG.setColor(Color.black);
    	player.flashlight.render(alphaG);
    	alphaG.fill(player.light);
    	
    	for (GridSpace s : EntityManager.litWalls.values()) {
    		alphaG.setColor(s.getIlluminationAlpha());
    		alphaG.fill(s.getMask());
    	}
    	
    	GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ONE);
    	for (SpotlightEntity e : EntityManager.spotlightEntities.values()) {
    		if (e.deleted == false)
    			e.render(alphaG);
    	}
    	
    	alphaG.setColor(Color.black);
    	
    	g.drawImage(alphaMap, 0, 0);
	}
	
	/*
	 * Draws a black rectangle over everything.  The rectangle is combined with the created alpha map.
	 */
	private void render_lighting(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		g.setDrawMode(Graphics.MODE_ALPHA_BLEND);
    	g.setColor(Color.black);
    	GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_DST_ALPHA);
    	
    	g.fillRect(0, 0, gc.getWidth(), gc.getHeight());
    	
    	g.setDrawMode(Graphics.MODE_NORMAL);
	}
	
	private void render_ui(GameContainer gc, StateBasedGame sbg, Graphics g)
			throws SlickException {
		g.setDrawMode(Graphics.MODE_NORMAL);
    	g.setColor(Color.white);
		g.drawString("Score: "+player.score, 0, 200);
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
			while (!locTaken(loc, takenPoints) && terrain.getCellGridSpaceType(i, j) != 0) {
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
			while (!locTaken(loc, takenPoints) && terrain.getCellGridSpaceType(i, j) != 0) {
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