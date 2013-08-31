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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Button.ButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.hh.ghoststory.GhostStory;
import com.sun.org.apache.bcel.internal.generic.IFGT;

public class CreateScreen extends AbstractScreen {
	protected Table table;
	private Stage stage;
	private boolean TABLE_DEBUG = false;
	
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
        table.add(header).colspan(6);

        LabelStyle labelStyle = new LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = new Color(0.1f, 0.2f, 0.5f, 1f);
        
        TextFieldStyle textStyle = new TextFieldStyle();
        textStyle.font = font;
        textStyle.fontColor = new Color(0.949019607843f, 0.486274509804f, 0.0823529411765f, 1f);
        textStyle.background = new NinePatchDrawable(new NinePatch(new Texture("images/text_border.9.png"), 18, 38, 38, 38));
        TextureRegionDrawable fieldCursor = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("fonts/cursor.png"))));
        textStyle.cursor = fieldCursor;

        ButtonStyle upButtonStyle = new ButtonStyle();
        upButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("images/arrow_up_up.png"))));
        upButtonStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("images/arrow_up_down.png"))));
        
        ButtonStyle downButtonStyle = new ButtonStyle();
        downButtonStyle.up = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("images/arrow_down_up.png"))));
        downButtonStyle.down = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("images/arrow_down_down.png"))));
        
        table.row().left();
        Label nameLabel = new Label("Name:", labelStyle);
        TextField nameText = new TextField("", textStyle);
        table.add(nameLabel).width(180).height(80).colspan(2);
        table.add(nameText).width(320).height(80).colspan(4);
        
        String[] attrs = { "STR", "INT", "AGI", "REA", "STA", "WIL" };
        for(int i = 0, l = attrs.length; i < l; i++) {
        	if (i % 2 == 0) table.row().left();
        	
            Label label = new Label(attrs[i], labelStyle);
            TextField textField = new TextField("6", textStyle);
            textField.setDisabled(true);
            
            Table buttons = new Table();
            buttons.debug();
            Button upButton = new Button(upButtonStyle);
            Button downButton = new Button(downButtonStyle);
            buttons.add(upButton).padBottom(10);
            buttons.row();
            buttons.add(downButton).padTop(10);

            table.add(label).width(80).height(80);
            table.add(textField).width(100).height(80).right();
            table.add(buttons).width(70).height(80);
        }
        table.row();
        table.add(new Label("Point Pool:", labelStyle)).colspan(5).height(80).right();
        table.add(new Label(Integer.toString(50), labelStyle)).colspan(4).height(80);
	}
	
	@Override
	public void show() {
		super.show();
		stage.addAction(
				sequence(
					alpha(0f,0f),
					fadeIn(0.3f)//,
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
        if (TABLE_DEBUG) {
        	Table.drawDebug(stage);	
        }
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
}
