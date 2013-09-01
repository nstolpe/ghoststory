package com.hh.ghoststory.screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.lights.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.lights.Lights;
import com.badlogic.gdx.graphics.g3d.materials.Material;
import com.badlogic.gdx.graphics.g3d.materials.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
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
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.game_models.Ghost;
import com.hh.ghoststory.game_models.core.GameModel;

public class CreateScreen extends AbstractScreen {
	protected Table table;
	private Stage stage;
	private boolean TABLE_DEBUG = false;
	private Map<String, InputListener> listeners = new HashMap<String, InputListener>();
	private MainScreenButton tableDebugSwitch;
	private ModelBatch modelBatch = new ModelBatch();
	public AssetManager assets = new AssetManager();
	private Ghost ghost;
	public boolean loading;
	public Array<GameModel> game_models = new Array<GameModel>();
	private PerspectiveCamera mcamera = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	public Lights lights = new Lights();
	private int GHOST_COLOR = 0;
	
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
            buttons.add(upButton).padBottom(4);
            buttons.row();
            buttons.add(downButton).padTop(4);

            table.add(label).width(80).height(80);
            table.add(textField).width(100).height(80).right();
            table.add(buttons).width(70).height(80);
        }
        table.row();
        table.add(new Label("Point Pool:", labelStyle)).colspan(5).height(80).right();
        table.add(new Label(Integer.toString(50), labelStyle)).colspan(4).height(80);
        table.row();
        tableDebugSwitch = new MainScreenButton("Debug Table Switch"); 
        table.add(tableDebugSwitch.button).colspan(9).height(100);
        setInputListeners();
        
        ghost = new Ghost();
        game_models.add(ghost);
        ghost.setPosition(new Vector3(0,-3.5f,0));
        ghost.verticalAxis = new Vector3(0,1,0);
        ghost.setRotation(0f);
        
        assets.load(ghost.model_resource, Model.class);
        loading = true;
//        mcamera.setToOrtho(false, 10, 10 * ((float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()));
//		mcamera.position.set(5, 5, 5);
//		mcamera.direction.set(-1, -1, -1);
        mcamera.position.set(0, 0, 5);
        mcamera.update();
//		mcamera.near = 1;
//		mcamera.far = 100;
        lights.ambientLight.set(0.5f, 0.5f, 0.5f, 1f);
        lights.add(new DirectionalLight().set(0.4f, 0.4f, 0.4f, -1f, -1f, -1f));
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
        if (doneLoading()) {
	        modelBatch.begin(mcamera);
	        ghost.update();
	        ghost.model.transform.rotate(new Vector3(1, 0, 0), -20);
	        modelBatch.render(ghost.model, lights);
	        modelBatch.end();
        }
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	public void setInputListeners() {
		ClickListener debugTableListener = new ClickListener() {
    		@Override
    		public void clicked (InputEvent event, float x, float y) {
//    			if (TABLE_DEBUG == true)
//    				TABLE_DEBUG = false;
//    			else
//    				TABLE_DEBUG = true;
			
    			
    			String res = new String();
    			switch (GHOST_COLOR) {
    				case  0:
    					System.out.println(GHOST_COLOR);
    					GHOST_COLOR = 1;
    					res = "ghost_texture_red.png";
    					break;
    				case 1:
    					System.out.println(GHOST_COLOR);
    					GHOST_COLOR = 2;
    					res = "ghost_texture_blue.png";
    					break;
    				case 2:
    					System.out.println(GHOST_COLOR);
    					GHOST_COLOR = 3;
    					res = "ghost_texture_orange.png";
    					break;
    				case 3:
    					System.out.println(GHOST_COLOR);
    					GHOST_COLOR = 0;
    					res = "ghost_texture_green.png";
    					break;
    			}
    			ghost.model.getMaterial("Texture_001").set(new TextureAttribute(TextureAttribute.Diffuse, new Texture("models/" + Gdx.files.internal(res))));
//    			TextureAttribute textureAttribute1 = new TextureAttribute(TextureAttribute.Diffuse, new Texture(Gdx.files.internal("models/ghost_texture_green.png")));
//    			mat.set(textureAttribute1);
    		}
		};
		listeners.put("debug_table_switch", debugTableListener);
		tableDebugSwitch.button.addListener(listeners.get("debug_table_switch"));
	}
	
	/*
	 * Check if assets have all been loaded. Run in a loop.
	 */
    private boolean doneLoading() {
    	if (loading && assets.update() != true) {
    		return false;
    	} else if (loading && assets.update()){
        	for (GameModel game_model : game_models) {
        		game_model.setModelResource(assets);
        	}
        	loading = false;
        	return false;
    	}
    	return true;
    }
}
