package com.hh.ghoststory.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.hh.ghoststory.GhostStory;

public class CreateScreen extends AbstractScreen {
	protected Table table;
	private Stage stage;
	private BitmapFont font = new BitmapFont(
			Gdx.files.internal("fonts/crimson.fnt"),
			Gdx.files.internal("fonts/crimson.png"),
			false
		);
	
	public CreateScreen(GhostStory game) {
		super(game);
		
		this.stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		Gdx.input.setInputProcessor(stage);
        table = new Table();
        table.setFillParent(true);
        table.top();
        table.debug();
        stage.addActor(table);
        TextButtonStyle headerStyle = new TextButtonStyle();
        headerStyle.font = font;
        headerStyle.fontColor = new Color(0.949019607843f, 0.486274509804f, 0.0823529411765f, 1f);
        TextButton header = new TextButton("Create a Character:", headerStyle);
        header.setDisabled(true);
        table.add(header);

        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = new Color(0.1f, 0.2f, 0.5f, 1f);
        TextFieldStyle textStyle = new TextFieldStyle();
        textStyle.font = font;
        textStyle.fontColor = new Color(0.949019607843f, 0.486274509804f, 0.0823529411765f, 1f);
        TextureRegionDrawable fieldCursor = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("fonts/cursor.png"))));
        textStyle.cursor = fieldCursor;

        table.row().left();
        Label nameLabel = new Label("Name:", labelStyle);
        TextField nameText = new TextField("", textStyle);
        nameText.setMessageText("Name!");
        table.add(nameLabel);
        table.add(nameText);
        table.row().left();
        Label addressLabel = new Label("Address:", labelStyle);
        TextField addressText = new TextField("", textStyle);
        table.add(addressLabel);
        table.add(addressText);
	}
	
	@Override
	public void show() {
		super.show();
		stage.addAction(
				sequence(
					alpha(0f,0f),
					fadeIn(0.5f)//,
//					new Action() {
//						@Override
//						public boolean act(float delta) {
//							setInputListeners();
//							return true;
//						}
//					}
				)
			);
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, true);
	}
	
	@Override
	public void render(float delta) {
//		Gdx.gl.glClearColor(0f, 0f, 0f, 1);
		Gdx.gl.glClearColor(1f, 1f, 1f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
//        Table.drawDebug(stage);
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
}
