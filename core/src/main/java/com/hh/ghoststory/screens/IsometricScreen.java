package com.hh.ghoststory.screens;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.TestShader;
import com.hh.ghoststory.actors.PlayerCharacter;
import com.hh.ghoststory.game_models.Ghost;
import com.hh.ghoststory.game_models.Tile;
import com.hh.ghoststory.game_models.core.GameModel;

public class IsometricScreen extends AbstractScreen implements InputProcessor {
	final Plane xzPlane = new Plane(new Vector3(0, 1, 0), 0);
	final Vector3 intersection = new Vector3();
	final Vector3 curr = new Vector3();
	final Vector3 last = new Vector3(-1, -1, -1);
	final Vector3 delta = new Vector3();

	private boolean justDragged = false;
	private Ghost ghost;

//    protected PerspectiveCamera camera = new PerspectiveCamera(100, 10, 10 * ((float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()));

    private TestShader testShader = new TestShader();
	private ModelBatch modelBatch;
    private ModelBatch shadowBatch = new ModelBatch(new DepthShaderProvider());
	public boolean loading;
	public AssetManager assets = new AssetManager();
	public Array<GameModel> game_models = new Array<GameModel>();

	public Environment environment = new Environment();
	
	private PlayerCharacter character;
    private DirectionalShadowLight shadowLight;

    private boolean shadows = false;

	public IsometricScreen(GhostStory game) {
		super(game);

        setupLights();
		setupGameModels();

        setupCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		loadCharacter(".ghost_story/character.json");
        ghost.setTexture(character.texture != null ? "models/" + character.texture : "models/ghost_texture_blue.png");

		setClear(0.5f, 0.5f, 0.5f, 1f);
		Gdx.input.setInputProcessor(this);
        modelBatch = new ModelBatch(Gdx.files.internal("shaders/default.vertex.glsl"), Gdx.files.internal("shaders/default.fragment.glsl"));
	}
	
	@Override
	public void show() {
		super.show();
	}
	
	@Override
	public void render(float delta) {
		super.render(delta);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//Gdx.gl20.
//        Array<PointLight> plights = environment.pointLights;
//        for (PointLight pl : plights) {
//            System.out.println(pl.position);
////            pl.set(new Color(1f, 1f, 1f, 1f), camera.position.x, camera.position.y, camera.position.z, 1);
////            pl.set(new Color(1f, 1f, 1f, 1f), 0, 2, 0, 1);
////            pl.set(new Color(1f, 1f, 1f, 1f), pl.position.mul(camera.projection), 1);
//        }
		camera.update();

		if (doneLoading()) {
            updateModels();
            if (this.shadows) renderShadows();
            renderModels();
		}
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		justDragged = false;
		return false;
	}

    /*
     * Might be the issue w/ movement here. Maybe trigger should happen on touchdown?
     */
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if (justDragged == false) {
			Ray pickRay = camera.getPickRay(Gdx.input.getX(), Gdx.input.getY());
			Intersector.intersectRayPlane(pickRay, xzPlane, intersection);

			ghost.setStartPosition(ghost.position);
			ghost.setTargetPosition(intersection.x, 0, intersection.z);

			float rotation = MathUtils.atan2(intersection.x - ghost.position.x, intersection.z - ghost.position.z) * 180 / MathUtils.PI;
			ghost.setTargetRotation(rotation < 0 ? rotation += 360 : rotation);
		} else {
			justDragged = false;
		}
		last.set(-1, -1, -1);
		return false;
	}

	/*
	 * Lets the screen be dragged and moved. Taken from libgdx isometric tutorial.
	 */
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		Ray pickRay = camera.getPickRay(screenX, screenY);
		Intersector.intersectRayPlane(pickRay, xzPlane, curr);
 
		if(!(last.x == -1 && last.y == -1 && last.z == -1)) {
			pickRay = camera.getPickRay(last.x, last.y);
			Intersector.intersectRayPlane(pickRay, xzPlane, delta);	
			delta.sub(curr);

            Array<PointLight> plights = environment.pointLights;
            for (PointLight pl : plights) {
//                pl.set(new Color(1f, 1f, 1f, 1f), 0, 2, 0, 1);
//                pl.position.add(delta.x, delta.y, delta.z);
            }
            camera.position.add(delta.x, delta.y, delta.z);
		}
		last.set(screenX, screenY, 0);
		justDragged = true;
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	/*
	 * Resizes the camera viewport to reflect the new size. Could do a lot more (like show more of the world at larger sizes).
	 * 
	 * @see com.hh.ghoststory.screens.AbstractScreen#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
		setupCamera(width, height);
	}
	
	private void setupCamera(int width, int height) {
		camera.setToOrtho(false, 10, 10 * ((float) height / (float) width));
		camera.position.set(5, 5, 5);
//		camera.direction.set(-1, -1, -1);
		camera.lookAt(0,0,0);
		camera.near = 1;
		camera.far = 300;
	}
	
	/*
	 * Instantiate the game models.
	 */
	private void setupGameModels() {
        ghost = new Ghost();
        game_models.add(ghost);
        
        for(int z = 0; z < 10; z++) {
			for(int x = 0; x < 10; x++) {
				game_models.add(new Tile(x,0,z));
			}
		}
        loadGameModelAssets();
	}
	
	/*
	 * Load the GameModel assets
	 */
	private void loadGameModelAssets() {
        for (GameModel game_model : game_models) {
        	assets.load(game_model.model_resource, Model.class);
        }
        loading = true;
	}
	
	/*
	 * Check if assets have all been loaded. Run in a loop.
	 */
    private boolean doneLoading() {
    	if (loading && assets.update() != true) {
    		return false;
    	} else if (loading && assets.update()) {
        	for (GameModel game_model : game_models) {
        		game_model.setModelResource(assets.get(game_model.model_resource, Model.class));
        	}
        	loading = false;
        	return false;
    	}
    	return true;
    }
	   
    private void setupLights() {
	    environment.add(new PointLight().set(new Color(1f, 1f, 1f, 1f), 0, 1, 0, 1));
	    environment.add(new PointLight().set(new Color(1f,0f,0f, 1f),  4, 1, 4, 1));
	    environment.add(new PointLight().set(new Color(0f,0f,1f, 1f), 6, 1, 0, 1));
	    environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .1f, .1f, .1f, .2f));
	    environment.add(new DirectionalLight().set(0.4f, 0.4f, 0.4f, -1f, -.8f, -.2f));

        if (this.shadows) {
//            environment.add((shadowLight = new DirectionalShadowLight(Gdx.graphics.getWidth() * 4, Gdx.graphics.getHeight() * 4, 10f, 10 * ((float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()), 1f, 100f)).set(0.8f, 0.8f, 0.8f, -1f, -.8f, -.2f));
            environment.add((shadowLight = new DirectionalShadowLight(4096, 4096, 30f, 30f, 1f, 100f)).set(0.8f, 0.8f, 0.8f, -1f, -.8f, -.2f));
            environment.shadowMap = shadowLight;
        }
    }
    
    /*
     * Loads the character from the character json.
     */
    private void loadCharacter(String file_path) {
		FileHandle file = Gdx.files.local(file_path);
		Json json = new Json();
		character = json.fromJson(PlayerCharacter.class, file.readString().toString());
    }

    private void updateModels() {
        for (GameModel game_model : game_models) {
            game_model.update();
        }
    }
    /*
     * Renders the GameModels.
     */
    private void renderModels() {
        modelBatch.begin(camera);

		for (GameModel game_model : game_models) {
			modelBatch.render(game_model.model, environment);
		}
        modelBatch.end();
    }
    /*
     * Renders the GameModels shadows.
     */
    private void renderShadows() {
        shadowLight.begin(Vector3.Zero, camera.direction);
        shadowBatch.begin(shadowLight.getCamera());
		for (GameModel game_model : game_models) {
			shadowBatch.render(game_model.model, environment);
		}
        shadowBatch.end();
        shadowLight.end();
    }
}
