import java.util.ArrayList;
import java.util.Hashtable;


public class EntityManager {

	private static int idCounter = 0;
	static Hashtable<Integer, Entity> entities = new Hashtable<Integer, Entity>();
	static Hashtable<Integer, ZombieEntity> zombieEntities = new Hashtable<Integer, ZombieEntity>();
	static Hashtable<Integer, GridSpace> gridSpaceEntities = new Hashtable<Integer, GridSpace>();
	static Hashtable<Integer, SurvivorEntity> survivorEntities = new Hashtable<Integer, SurvivorEntity>();
	static Hashtable<Integer, SpotlightEntity> spotlightEntities = new Hashtable<Integer, SpotlightEntity>();
	static Hashtable<Integer, GridSpace> litWalls = new Hashtable<Integer, GridSpace>();
	
	
	static ArrayList<Entity> toAdd = new ArrayList<Entity>();
	static ArrayList<Entity> toExpunge = new ArrayList<Entity>();
	
	
	public static Entity getEntity(int id) {
		return entities.get(id);
	}
	
	public static int registerEntity(Entity entityToRegister) {
		toAdd.add(entityToRegister);
		return ++idCounter;
	}
	
	/*
	 * Inserts the given entity into the Hashtable and increments the idCounter to keep the keys unique.
	 */
	private static void addEntity(Entity entityToAdd) {
		entities.put(entityToAdd.id(), entityToAdd);
	}
	
	public static void addZombieEntity(ZombieEntity entityToAdd) {
		zombieEntities.put(idCounter, entityToAdd);
	}
	
	public static void addGridSpaceEntity(GridSpace entityToAdd) {
		gridSpaceEntities.put(idCounter, entityToAdd);
	}
	
	public static void addSurvivorEntity(SurvivorEntity entityToAdd) {
		survivorEntities.put(idCounter, entityToAdd);
	}
	
	public static void addSpotlightEntity(SpotlightEntity entityToAdd) {
		spotlightEntities.put(idCounter, entityToAdd);
	}
	
	public static void addLitWallEntity(GridSpace entityToAdd) {
		if (litWalls.containsKey(entityToAdd.id()) == false) {
			litWalls.put(entityToAdd.id(), entityToAdd);
		}
	}
	
	public static int removeEntity(int id) {
		Entity toRemove = entities.get(id);
		if (toRemove != null) {
			return removeEntity(entities.get(id));
		} else {
			System.err.println("Could not remove entity with id = "+id);
			return 0;
		}
	}
	
	public static int removeEntity(Entity entityToRemove) {
		// First remove all children
		for (Entity e : entityToRemove.children.values()) {
			removeEntity(e);
		}
		// put to the entity in the toRemove queue
		toExpunge.add(entityToRemove);
		entityToRemove.deleted = true;
		return entityToRemove.id();
	}
	
	public static void syncEntities() {
		addNewEntities();
		expungeEntities();
	}
	
	private static void addNewEntities() {
		for (Entity e : toAdd) {
			Class<? extends Entity> eclass = e.getClass();
			if (eclass.equals(ZombieEntity.class)) {
				zombieEntities.put(e.id(), (ZombieEntity)e);
			} else if (eclass.equals(GridSpace.class)) {
				gridSpaceEntities.put(e.id(), (GridSpace)e);
			} else if (eclass.equals(SurvivorEntity.class)) {
				survivorEntities.put(e.id(), (SurvivorEntity)e);
			} else if (eclass.equals(SpotlightEntity.class)) {
				spotlightEntities.put(e.id(), (SpotlightEntity)e);
			}
			addEntity(e);
			System.out.println("Added entity "+e.id()+" ("+e.getClass()+")");
		}
		
		toAdd.clear();
	}
	
	private static void expungeEntities() {
		for (Entity e : toExpunge) {
			Class<? extends Entity> eclass = e.getClass();
			if (eclass.equals(ZombieEntity.class)) {
				zombieEntities.remove(e.id());
			} else if (eclass.equals(GridSpace.class)) {
				gridSpaceEntities.remove(e.id());
			} else if (eclass.equals(SurvivorEntity.class)) {
				survivorEntities.remove(e.id());
			} else if (eclass.equals(SpotlightEntity.class)) {
				spotlightEntities.remove(e.id());
			}
			entities.remove(e.id());
			System.out.println("Expunged entity " + e.id());
		}
		toExpunge.clear();
	}
	
	public static void clear() {
		idCounter = 0;
		entities.clear();
		zombieEntities.clear();
		gridSpaceEntities.clear();
		survivorEntities.clear();
		spotlightEntities.clear();
		toAdd.clear();
		toExpunge.clear();
	}
	
}
