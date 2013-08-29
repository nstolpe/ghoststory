package com.hh.ghoststory.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.hh.ghoststory.GhostStory;

public class CreateScreen extends AbstractScreen {
	protected Table table;
	private Stage stage;
	private SpriteBatch batch;
	
	public CreateScreen(GhostStory game) {
		super(game);
		
		this.stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		Gdx.input.setInputProcessor(stage);
        table = new Table();
        table.setFillParent(true);
        table.debug();
        stage.addActor(table);
        TextFieldStyle style = new TextFieldStyle();
		BitmapFont font = new BitmapFont(
    			Gdx.files.internal("fonts/crimson.fnt"),
    			Gdx.files.internal("fonts/crimson.png"),
    			false
    		);
        style.font = font;
        style.fontColor = new Color(0f, 0f, 0f, 1f);
//        Widget text = new Widget();
//        text.
//        table.add(text).width(100);
	}
	
	@Override
	public void show() {
		super.show();
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, true);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
}
