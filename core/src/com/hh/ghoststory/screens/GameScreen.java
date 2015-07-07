package com.hh.ghoststory.screens;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.InputManager;
import com.hh.ghoststory.TestShader;
import com.hh.ghoststory.tween_accessors.*;
import com.hh.ghoststory.actors.PlayerCharacter;
import com.hh.ghoststory.game_models.Ghost;
import com.hh.ghoststory.game_models.Tile;
import com.hh.ghoststory.game_models.core.GameModel;
import com.hh.ghoststory.renderers.ModelBatchRenderer;

import java.util.Random;

public class GameScreen extends AbstractScreen {
	public ModelBatchRenderer renderer;

	private InputManager inputManager;
//	private InputMultiplexer multiplexer = new InputMultiplexer();
	private TestShader testShader = new TestShader();
	private PlayerCharacter character;

	public Ghost ghost;
	public boolean loading;
	public Array<GameModel> gameModels = new Array<GameModel>();
	public TweenManager tweenManager = new TweenManager();
	private AnimationController controller;
	private PointLight fooLight;
	public PointLight barLight;
	private Color barColor = new Color(0.6f,0.2f,1f,1f);
	FPSLogger logger = new FPSLogger();

	public GameScreen(GhostStory game) {
		super(game);
		this.setUpRenderer();
		this.setupLights();
		this.renderer.setUpDefaultCamera(ModelBatchRenderer.ORTHOGRAPHIC);

		this.setupGameModels();
		this.loadGameModelAssets();

		this.setupInputProcessors();
		this.setClear(0.5f, 0.5f, 0.5f, 1f);
		this.setupTweenEngine();

		this.loadCharacter(".ghost_story/character.json");
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

		this.tweenManager.update(Gdx.graphics.getDeltaTime());
		this.renderer.getActiveCamera().update();

		if (doneLoading()) {
			this.updateModels();
			this.renderer.setRenderables(collectModelInstances());
			this.controller.update(Gdx.graphics.getDeltaTime());
			this.renderer.render();
		}
		this.logger.log();

	}

	@Override
	public void dispose() {
		super.dispose();
		this.testShader.dispose();
	}

	/*
	 * Resizes the camera viewport to reflect the new size. Could maybe set a default zoom or layout for different sized screens.
	 *
	 * @see com.hh.ghoststory.screens.AbstractScreen#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
		this.renderer.setCameraViewport(width, height);
	}

	private void setUpRenderer() {
		this.renderer = new ModelBatchRenderer();
		this.renderer.setUpDefaultCamera(ModelBatchRenderer.PERSPECTIVE);
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
		this.ghost = new Ghost();
		this.gameModels.add(this.ghost);

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
//			this.assets.load(gameModel.model_resource, Model.class);
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
				gameModel.setTranslation();
			}
			Tween.call(lightCallback).start(tweenManager);
			Tween.call(colorCallback).start(tweenManager);
			this.controller = new AnimationController(ghost.model);
			this.controller.setAnimation("float", -1);
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
//		gameModel.model = new ModelInstance(assets.get(gameModel.model_resource, Model.class));

	}

	private void setupLights() {
		fooLight = new PointLight().set(new Color(0f,1f,0f,1f),6,1,6,1);
		barLight = new PointLight().set(barColor,12,1,10,1);
		BaseLight[] lights = {
				new PointLight().set(new Color(1f, 1f, 1f, 1f), 0, 1, 0, 1),
				new PointLight().set(new Color(1f, 0f, 0f, 1f), 4, 1, 4, 1),
				new PointLight().set(new Color(0f, 0f, 1f, 1f), 6, 1, 0, 1),
				fooLight,
				barLight
		};
		this.renderer.setUpLights(lights);
	}

	private void loadCharacter(String file_path) {
		FileHandle file = Gdx.files.local(file_path);
		Json json = new Json();
		this.character = json.fromJson(PlayerCharacter.class, file.readString());
	}

	private void updateModels() {
		for (GameModel game_model : this.gameModels)
			game_model.update();
	}

	/*
	 * Registers the GameModelTweenAccessor and initializes the TweenManager.
	 */
	private void setupTweenEngine() {
		Tween.setCombinedAttributesLimit(4); // ColorAccessor returns 4 values (rgba) in one instance
		Tween.registerAccessor(Ghost.class, new GameModelTweenAccessor());
		Tween.registerAccessor(Vector3.class, new Vector3Accessor());
		Tween.registerAccessor(Color.class, new ColorAccessor());
//		Tween.registerAccessor(Matrix4.class, new Matrix4Accessor());
		Tween.registerAccessor(Quaternion.class, new QuaternionAccessor());
	}

	private final TweenCallback lightCallback = new TweenCallback(){
		@Override
		public void onEvent(int i, BaseTween<?> baseTween) {
			Timeline.createSequence()
						.push(Tween.to(GameScreen.this.barLight.position, Vector3Accessor.POSITION_Z, 1)
								.target(10)
								.ease(TweenEquations.easeInSine))
					.push(Tween.to(GameScreen.this.barLight.position, Vector3Accessor.POSITION_Z, 1)
							.target(0)
								.ease(TweenEquations.easeInSine))
					.setCallback(lightCallback)
					.start(tweenManager);
		}
	};
	private final TweenCallback colorCallback = new TweenCallback(){
		@Override
		public void onEvent(int i, BaseTween<?> baseTween) {
			Random generator = new Random();
			float red = generator.nextFloat();
			float green = generator.nextFloat();
			float blue = generator.nextFloat();
			Timeline.createSequence()
					.push(Tween.to(GameScreen.this.fooLight.color, ColorAccessor.COLORS, 1)
							.target(red,green,blue)
							.ease(TweenEquations.easeNone))
					.setCallback(colorCallback)
					.start(tweenManager);
		}
	};
	/*
     * Adds processors to the multiplexer and sets it as Gdx's input processor.
     */
	private void setupInputProcessors() {
		this.inputManager = new InputManager(this);
	}

	private void tweenFaceAndMoveTo(GameModel gameModel, float rotation, float rotDur, float x, float y, float z, float transDur) {
		Timeline.createSequence()
				.push(Tween.to(gameModel, GameModelTweenAccessor.ROTATION, rotDur)
						.target(rotation)
						.ease(TweenEquations.easeNone))
				.push(Tween.to(gameModel, GameModelTweenAccessor.POSITION_XYZ, transDur).
						target(x, y, z)
						.ease(TweenEquations.easeNone))
// Below rotates and translates at the same time.
//						.push(Tween.to(this.ghost, GameModelTweenAccessor.ALL, duration).
//								target(x, y, z, newAngle)
//								.ease(TweenEquations.easeNone))
				.start(tweenManager);
	}

	/*
	 * Creates a tween timeline that will rotate to face a point and then move to it.
	 * Made public right now because it's used by input manager. Should change.
	 *
	 * @param Quaternion currentRotation  The current facing rotation of the object being tweened.
	 * @param Quaternion targetRotation   The rotation the object should tween towards.
	 * @param Vector3    currentPosition  The current translation of the object being tweened.
	 * @param Vector3    targetPosition   The translation the object should tween toward.
	 * @param float      rd               The duration that the object's rotation should take to complete.
	 * @param float      td               The duration that the object's translation should take to complete.
	 */
	public void tweenFaceAndMoveTo(Quaternion currentRotation, Quaternion targetRotation, Vector3 currentPosition, Vector3 targetPosition, float rd, float td) {
		Timeline.createSequence()
				.push(Tween.to(currentRotation, QuaternionAccessor.ROTATION, rd)
						.target(targetRotation.x, targetRotation.y, targetRotation.z, targetRotation.w)
						.ease(TweenEquations.easeNone))
				.push(Tween.to(currentPosition, Vector3Accessor.POSITION_XYZ, td).
						target(targetPosition.x, targetPosition.y, targetPosition.z)
						.ease(TweenEquations.easeNone))
				.start(tweenManager);
	}
}