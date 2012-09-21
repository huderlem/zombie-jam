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
			terrainGrid[terrainGridWidth-1][i] = new WallEntity(null, (terrainGridWidth-1)*terrainCellWidth, i*terrainCellWidth).id();
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
	
	/*
	 * Helper for room generation.  Chooses the orientation a wall should be constructed.
	 * Returns a 1 for horizontal or 0 for vertical.
	 */
	private int getOrientation(int width, int height) {
		if (width > height) 
			return 1; // Horizontal
		else if (height > width)
			return 0; // Vertical
		else
			return rand.nextInt(2);
	}
	
	/*
	 * Recursively
	 */
	private void placeWall(int i, int j, int width, int height, int iterations, int minRoomWidth, int doorwayWidth) {
		
		// If the resolution of our room is too small, we'll stop subdividing
		if (width < minRoomWidth*2+1 || height < minRoomWidth*2+1) { // Base case for recursive subdividing
			return;
		} else {
			// First determine the orientation we'll be dividing from
			boolean horizontal = getOrientation(width, height) == 1;
			
			// Calculate the i, j coords for the start of the wall
			int wallI = (i+minRoomWidth) + (horizontal ? rand.nextInt(width-2*minRoomWidth) : -minRoomWidth);
			int wallJ = (j+minRoomWidth) + (horizontal ? -minRoomWidth : rand.nextInt(height-2*minRoomWidth));
			
			// Calculate the i, j coords for the opening in the wall
			int openIndex = 1 + (horizontal ? rand.nextInt(height-doorwayWidth) : rand.nextInt(width-doorwayWidth));
			
			// Calculate the amount to move the wall when drawing it in
			int dx = horizontal ? 0 : 1;
			int dy = horizontal ? 1 : 0;

			// Draw in the walls to the grid
			for (int count = 0; count <= (horizontal ? height : width); count++) {
				// Calculate which grid coords we're placing a wall into
				int gridI = wallI+dx*count;
				int gridJ = wallJ+dy*count;
				
				// Do some basic index bounds checking
				if (gridI < 0 || gridI >= terrainGridWidth)
					continue;
				if (gridJ < 0 || gridJ >= terrainGridHeight)
					continue;
				
				// Continue the loop if this is an opening in the wall
				int openDiff = openIndex-count;
				if (openDiff >= 0 & openDiff < doorwayWidth)
					continue;
				
				// Don't place a new wall if one already exists here
				if (terrainGrid[gridI][gridJ] == 0)
					terrainGrid[gridI][gridJ] = new WallEntity(null, gridI*terrainCellWidth, gridJ*terrainCellWidth).id();
			}
			
			if (horizontal) {
				// Divide the left and right rooms
				placeWall(i, j, wallI-i, height, iterations-1, minRoomWidth, doorwayWidth);
				placeWall(wallI, j, i+width-wallI, height, iterations-1, minRoomWidth, doorwayWidth);
			} else {
				// Divide the top and bottom rooms
				placeWall(i, j, width, wallJ-j, iterations-1, minRoomWidth, doorwayWidth);
				placeWall(i, wallJ, width, j+height-wallJ, iterations-1, minRoomWidth, doorwayWidth);
			}
			
			// And that's how we do recursion!  Fun stuff, really.
		}	
	}
	
	public void generateRoom(int iterations) {
		placeWall(0, 0, terrainGridWidth, terrainGridHeight, iterations, 4, 3);
		generateWalls();
	}
	
	
	
	
	
}
