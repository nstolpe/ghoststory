package com.hh.ghoststory.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.hh.ghoststory.GhostStory;

public class MainScreen extends AbstractScreen {
	protected Table table;
	private Stage stage;
	private SpriteBatch batch;
	private MainScreenButton startButton;
	private MainScreenButton createButton;
	
	public MainScreen(GhostStory game) {
		super(game);

		this.stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		Gdx.input.setInputProcessor(stage);
        table = new Table();
        table.setFillParent(true);
        table.debug();
        stage.addActor(table);
        startButton = new MainScreenButton("Play Ghost Story");
        table.add(startButton.button).width(500).height(100);
        table.row();
        createButton = new MainScreenButton("Create Character");
        table.add(createButton.button).width(500).height(100);
	}
	
	/*
	 * Adds a fade in action to the screen and enables input listeners when it's finished.
	 * @see com.hh.ghoststory.screens.AbstractScreen#show()
	 */
	@Override
	public void show() {
		super.show();
		stage.addAction(
			sequence(
				alpha(0f,0f),
				fadeIn(1.25f),
				new Action() {
					@Override
					public boolean act(float delta) {
						setInputListeners();
						return true;
					}
				}
			)
		);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
		startButton.font.dispose();
		createButton.font.dispose();
		stage.dispose();
	}

	/*
	 * This is where you'll set input listeners.
	 */
	public void setInputListeners() {
		startButton.button.addListener(new ClickListener() {
	        @Override
	        public void clicked (InputEvent event, float x, float y) {
	            game.setScreen(game.getIsometricScreen());
	        }
		});
		createButton.button.addListener(new ClickListener() {
			@Override
			public void clicked (InputEvent event, float x, float y) {
				game.setScreen(game.getCreateScreen());
			}
		});
	}
	
	/*
	 * Class to create buttons on the main screen. A factory may be better, or use libGDX skin. Or move
	 * to own class.
	 */
	private class MainScreenButton {
        NinePatch up = new NinePatch(new Texture("images/up.9.png"), 18, 38, 38, 38);
        NinePatch down = new NinePatch(new Texture("images/down.9.png"), 38, 38, 38, 38);
        TextButtonStyle style = new TextButtonStyle();
        BitmapFont font;
        TextButton button;
        
        public MainScreenButton(String label) {
    		font = new BitmapFont(
    			Gdx.files.internal("fonts/crimson.fnt"),
    			Gdx.files.internal("fonts/crimson.png"),
    			false
    		);
            style.up = new NinePatchDrawable(up);
            style.down = new NinePatchDrawable(down);
            style.font = font;
            style.fontColor = new Color(1f, 1f, 1f, 1f);
            
            button = new TextButton(label, style);
        }
	}
}
