import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * 
 * @author Marcus Huderle
 *
 */
public class ZombieJam extends StateBasedGame {

	public static final int MAINMENUSTATE = 0;
    public static final int GAMEPLAYSTATE = 1;
	
	public ZombieJam() {
		super("ZombieJam");
	}

	@Override
	public void initStatesList(GameContainer arg0) throws SlickException {
		this.addState(new MainMenuState(MAINMENUSTATE));
		this.addState(new GameplayState(GAMEPLAYSTATE));
		
	}

	public static void main(String[] args) throws SlickException {
		AppGameContainer app = new AppGameContainer(new ZombieJam());
		app.setTargetFrameRate(100);
        app.setDisplayMode(800, 600, false);
        //app.setVSync(true);
        app.start();

	}
}
