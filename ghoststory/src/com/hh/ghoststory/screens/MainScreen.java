package com.hh.ghoststory.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
        setInputListeners();
	}
	
	@Override
	public void show() {
		super.show();
	}
	
	@Override
	public void render(float delta) {
//		setClear(1, 1, 1, 1f);
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
	
	public void setInputListeners() {
		/*
		 * This is where you'll set input listeners.
		 */
		startButton.button.addListener(new ClickListener() {
	        @Override
	        public void clicked (InputEvent event, float x, float y) {
	            game.setScreen(game.getIsometricScreen());
	        }
		});
	}
	public void setScreen(int screen) {
//		switch(screen) {
//		case 1:
//			game.setScreen(game.getTurnScreen()); break;
//		case 2:
//			game.setScreen(game.getCharacterScreen()); break;
//		case 3:
//			game.setScreen(game.getIsometricScreen()); break;
//		}
	}
	private class MainScreenButton {
        NinePatch up = new NinePatch(new Texture("images/up.9.png"), 18, 38, 38, 38);
        NinePatch down = new NinePatch(new Texture("images/down.9.png"), 38, 38, 38, 38);
        TextButtonStyle style = new TextButtonStyle();
        BitmapFont font;
        TextButton button;
        
        public MainScreenButton(String label) {
    		font = new BitmapFont(Gdx.files.internal("fonts/crimson.fnt"),
   		         Gdx.files.internal("fonts/crimson.png"), false);
            style.up = new NinePatchDrawable(up);
            style.down = new NinePatchDrawable(down);
            style.font = font;
            style.fontColor = new Color(1f, 1f, 1f, 1f);
            
            button = new TextButton(label, style);
        }
	}
}
