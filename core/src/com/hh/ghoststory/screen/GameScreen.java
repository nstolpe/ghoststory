package com.hh.ghoststory.screen;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenEquations;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
//import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
//import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.*;
import com.hh.ghoststory.scene.gamemodels.Character;
import com.hh.ghoststory.scene.gamemodels.Tile;
import com.hh.ghoststory.scene.gamemodels.core.GameModel;
import com.hh.ghoststory.render.renderers.ModelBatchRenderer;
import com.hh.ghoststory.lib.tween.accessors.ColorAccessor;
import com.hh.ghoststory.lib.tween.accessors.QuaternionAccessor;
import com.hh.ghoststory.lib.tween.accessors.Vector3Accessor;
import com.hh.ghoststory.screen.core.AbstractScreen;

import java.util.ArrayList;
import java.util.HashMap;

public class GameScreen extends AbstractScreen {
	public static int DEPTHMAPSIZE = 4096;
	public ModelBatchRenderer renderer;

	private InputHandler inputHandler;
	public CameraHandler cameraHandler;
	public TweenHandler tweenHandler;

//	private PlayerCharacter character;

	public Character character;
	public boolean loading;
	public Array<GameModel> gameModels = new Array<GameModel>();

	// AnimationController should be on GameModel
	private AnimationController animationController;
	public PointLight travellingLight;
	public PointLight colorSwitchLight;
	private Color colorSwitchColor = new Color(0.6f,0.2f,1f,1f);
	FPSLogger logger = new FPSLogger();
	public ArrayList<Light> lights = new ArrayList<Light>();

	public GameScreen(GhostStory game) {
		super(game);

		inputHandler = new InputHandler(this);

		cameraHandler = new CameraHandler(this, new OrthographicCamera());
		cameraHandler.setUpDefaultCamera(CameraHandler.ORTHOGRAPHIC);

		tweenHandler = new TweenHandler(this, 4, getTweenAccessors());

		renderer = new ModelBatchRenderer(this);
		setupLights();

		setupGameModels();
		loadGameModelAssets();

		setClear(0.5f, 0.5f, 0.5f, 1f);

//		this.loadCharacter(".ghost_story/character.json");
//		this.ghost.setTexture(this.character.texture != null ? "models/" + this.character.texture : "models/ghost_texture_blue.png");
	}

	@Override
	public void show() {
		super.show();
	}

	/*
	 * Updates the TweenManager, sets the glClear, updates the camera, then renders.
	 */
	@Override
	public void render(float delta) {
		super.render(delta);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		tweenHandler.update();
		cameraHandler.getActiveCamera().update();

		for (final Light light : lights) light.needsUpdate= true;
		if (doneLoading()) {
			this.updateModels();
			this.renderer.setRenderables(collectModelInstances());
			this.animationController.update(Gdx.graphics.getDeltaTime());
			this.renderer.render();
		}
		this.logger.log();

	}

	@Override
	public void dispose() {
		super.dispose();
	}

	/*
	 * Resizes the camera viewport to reflect the new size. Could maybe set a default zoom or layout for different sized screens.
	 *
	 * @see com.hh.ghoststory.screens.AbstractScreen#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
		this.cameraHandler.setCameraViewport(width, height);
	}

	public HashMap<Class, TweenAccessor> getTweenAccessors() {
		HashMap<Class, TweenAccessor> tweenAccessors = new HashMap<Class, TweenAccessor>();
		tweenAccessors.put(Vector3.class, new Vector3Accessor());
		tweenAccessors.put(Quaternion.class, new QuaternionAccessor());
		tweenAccessors.put(Color.class, new ColorAccessor());
		return tweenAccessors;
	}

	/*
	 * Builds an Array<ModelInstance> from the model fields attached to gameModels
	 */
	private Array<ModelInstance> collectModelInstances() {
		Array<ModelInstance> modelInstances = new Array<ModelInstance>(this.gameModels.size);
		for (GameModel gameModel : this.gameModels) modelInstances.add(gameModel.model);
		return modelInstances;
	}

	/*
	 * Instantiate the game models. There's some weird building of blocks with panes in there
	 * could maybe be refactored to use somewhere.
	 */
	private void setupGameModels() {
		this.character = new Character();
		this.gameModels.add(this.character);

//		10x10 grid on the ground.
		for (int z = 0; z < 10; z++) {
			for (int x = 0; x < 20; x++) {
				this.gameModels.add(new Tile(x, 0, z));
//				this.gameModels.add(new Tile());
			}
		}
//		builds the far side of the wall thingy, that can't be seen.
		for (int z = 0; z < 3; z++) {
			for (int y = 0; y < 2; y++) {
				Tile tile = new Tile(9.5f,y + 0.5f,z + 5);
				tile.rotation = 90;
				tile.verticalAxis = new Vector3(0,0,1);
				this.gameModels.add(tile);
			}
		}
//		top of the little wall thing.
		for (int z = 0; z < 3; z++) {
			this.gameModels.add(new Tile(10, 2, z + 5));
		}
//		front of the wall thing.
		for (int y = 0; y < 2; y++) {
			Tile tile = new Tile(10,y + 0.5f,7.5f);
			tile.rotation = 90;
			tile.verticalAxis = new Vector3(1,0,0);
			this.gameModels.add(tile);
		}
//		side of the wall thing, the big part.
		for (int z = 0; z < 3; z++) {
			for (int y = 0; y < 2; y++) {
				Tile tile = new Tile(10.5f,y + 0.5f,z + 5);
				tile.rotation = 270;
				tile.verticalAxis = new Vector3(0,0,1);
				this.gameModels.add(tile);
			}
		}
	}

	/*
	 * Load the GameModel assets
	 */
	private void loadGameModelAssets() {
		for (GameModel gameModel : this.gameModels)
			this.renderer.assetManager.load(gameModel.model_resource, Model.class);

		this.loading = true;
	}

	/*
	 * Check if assets have all been loaded. Run in a loop.
	 */
	private boolean doneLoading() {
		if (this.loading && !renderer.assetManager.update()) {
			return false;
		} else if (this.loading && renderer.assetManager.update()) {
			for (GameModel gameModel : this.gameModels) {
				setModelResource(gameModel);
//				gameModel.model.transform.setToScaling(10f, 4f, 4f);
				gameModel.setTranslation();
			}
//			tweenHandler.startCallbacks();

			this.animationController = new AnimationController(character.model);
			this.animationController.setAnimation("float", -1);
			this.loading = false;
			return false;
		}
		return true;
	}

	/*
	 * Sets the Model for the GameModel
	 */
	private void setModelResource(GameModel gameModel) {
		gameModel.model = new ModelInstance(renderer.assetManager.get(gameModel.model_resource, Model.class));
	}

	private void setupLights() {
//		travellingLight = new PointLight().set(new Color(0f,1f,0f,1f),6,1,6,1);
//		colorSwitchLight = new PointLight().set(colorSwitchColor,12,1,10,1);
//		BaseLight[] lights = {
//				new PointLight().set(new Color(1f, 1f, 1f, 1f), 0, 1, 0, 1),
//				new PointLight().set(new Color(1f, 0f, 0f, 1f), 4, 1, 4, 1),
//				new PointLight().set(new Color(0f, 0f, 1f, 1f), 6, 1, 0, 1),
//				travellingLight,
//				colorSwitchLight
//		};

//		lights.add(new PointLight(this, new Vector3(0f, 13.8f, 32f)));
		lights.add(new PointLight(this, new Vector3(-25.5f, 12.0f, -26f)));
		lights.add(new PointLight(this, new Vector3(5f, 5.0f, 0f)));
//		lights.add(new DirectionalLight(this, new Vector3(0, 0, 2), new Vector3(1, 1, 0)));
//		lights.add(new MovingPointLight(this, new Vector3(0f, 30.0f, 0f)));
		this.renderer.setUpLights(lights);
	}

	private void updateModels() {
		for (GameModel game_model : this.gameModels)
			game_model.update();
	}

	private void loadCharacter(String file_path) {
//		FileHandle file = Gdx.files.local(file_path);
//		Json json = new Json();
//		this.character = json.fromJson(PlayerCharacter.class, file.readString());
	}

	// Interface methods for communicating between components
	// This all is getting ugly and stupid now.
	//     CameraHandler

	// 3 uses in InputHandler
	public Ray getPickRay(float x, float y) {
		return cameraHandler.getActiveCamera().getPickRay(x, y);
	}

	// 1 use in InputHandler
	public void moveCameraBy(float x, float y, float z) {
		cameraHandler.getActiveCamera().position.add(x, y, z);
	}

	// 1 use in InputHandler
	public void translateCamera(float x, float y, float z) {
		cameraHandler.getActiveCamera().translate(x, y, z);
	}

	// 2 uses in InputHandler
	public Vector3 getCameraPosition() {
		return cameraHandler.getActiveCamera().position.cpy();
	}

	// 1 use in InputHandler
	public Vector3 getCameraDirection() {
		return new Vector3();
	}

	// 1 use in InputHandler
	public void zoomCamera(int distance) {
		cameraHandler.zoomCamera(distance);
	}

	// 1 use in ModelBatchRenderer
	public Camera getActiveCamera() {
		return cameraHandler.getActiveCamera();
	}

	// 2 uses in InputHandler
	public int getActiveCameraType() {
		return cameraHandler.getActiveCameraType();
	}

	// 2 uses in InputHandler
	public void setActiveCameraType(int type) {
		cameraHandler.setActiveCameraType(type);
	}

	//     TweenHandler

	// 1 use in InputHandler
	public void tweenFaceAndMoveTo(Quaternion currentRotation, Quaternion targetRotation, Vector3 currentPosition, Vector3 targetPosition, float rd, float td) {
		tweenHandler.runSequence(new Tween[] {
				tweenHandler.buildTween(currentRotation, new float[]{targetRotation.x, targetRotation.y, targetRotation.z, targetRotation.w }, QuaternionAccessor.ROTATION, rd, TweenEquations.easeNone),
				tweenHandler.buildTween(currentPosition, new float[] { targetPosition.x, targetPosition.y, targetPosition.z }, Vector3Accessor.POSITION_XYZ, td, TweenEquations.easeNone)
		});
	}

	// 2 uses in InputHandler
	public void killTween(Object target, int tweenType) {
		tweenHandler.tweenManager.killTarget(target, tweenType);
	}
}
