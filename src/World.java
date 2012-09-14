import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Vector2f;


public class World {

	Random rand = new Random();
	Image floorTexture;
	
	float terrainCellWidth;
	int terrainGridWidth;
	int terrainGridHeight;
	
	int[][] terrainGrid;
	
	
	public World(int width, int height, float cellWidth) {
		terrainGridWidth = width;
		terrainGridHeight = height;
		terrainGrid = new int[width][height];
		this.terrainCellWidth = cellWidth;
		
		floorTexture = TextureManager.getTexture("textures/floor.png");
	}
	
	
	public void placeEntity(float x, float y, int id) {
		if (x < 0 || x > terrainCellWidth*terrainGridWidth || y < 0 || y > terrainCellWidth*terrainGridHeight) {
			System.err.println("Failed to place entity ("+id+") at ("+x+", "+y+")");
			return;
		}
		terrainGrid[(int)(x/terrainCellWidth)][(int)(y/terrainCellWidth)] = id;
	}
	
	public int getCellContents(float x, float y) {
		if (x < 0 || x > terrainCellWidth*terrainGridWidth || y < 0 || y > terrainCellWidth*terrainGridHeight) {
			System.err.println("Tried to get cell contents at invalid coords: ("+x+", "+y+")");
			return 0;
		}
		return terrainGrid[(int)(x/terrainCellWidth)][(int)(y/terrainCellWidth)];
	}
	
	public int getCellContents(int i, int j) {
		if (i < 0 || i > terrainGridWidth || j < 0 || j > terrainGridHeight) {
			System.err.println("Tried to get cell contents at invalid cell coords: ("+i+", "+j+")");
			return 0;
		} else
			return terrainGrid[i][j];
	}
	
	public ArrayList<Integer> getSurroundingCellContents(float x, float y) {
		int gridX = (int)(x/terrainCellWidth);
		int gridY = (int)(y/terrainCellWidth);
		
		ArrayList<Integer> surroundingCells = new ArrayList<Integer>();
		// Iterate through the 8 surrounding cells--don't include the middle cell
		for (int i = gridX-1; i <= gridX+1; i++) {
			for (int j = gridY-1; j <= gridY+1; j++) {
				if ( !(i == gridX && j == gridY) && i >= 0 && i < terrainGridWidth && j >= 0 && j < terrainGridHeight) {
					surroundingCells.add(getCellContents(i, j));
				}
			}
		}
		return surroundingCells;
	}	
	
	public void generateWalls() {	
		for (int i = 0; i < terrainGridWidth; i++) {
			terrainGrid[i][0] = new WallEntity(null, i*terrainCellWidth, 0).id();
			terrainGrid[i][terrainGridHeight-1] = new WallEntity(null, i*terrainCellWidth, (terrainGridHeight-1)*terrainCellWidth).id();
		}
		
		for (int i = 0; i < terrainGridHeight; i++) {
			terrainGrid[0][i] = new WallEntity(null, 0, i*terrainCellWidth).id();
			terrainGrid[terrainGridWidth-1][i] = new WallEntity(null, (terrainGridHeight-1)*terrainCellWidth, i*terrainCellWidth).id();
		}
		
	}
	
	public void renderFloor(Graphics g) {
		for (int i = 0; i < terrainGridWidth; i++){
			for (int j = 0; j < terrainGridHeight; j++) {
				if (terrainGrid[i][j] == 0) {
					floorTexture.draw(i*terrainCellWidth, j*terrainCellWidth, terrainCellWidth, terrainCellWidth);
				}
			}
		}
	}
	
	
	
}
