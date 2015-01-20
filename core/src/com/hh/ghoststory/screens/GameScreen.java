package com.hh.ghoststory.screens;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.TestShader;
import com.hh.ghoststory.accessors.GameModelTweenAccessor;
import com.hh.ghoststory.actors.PlayerCharacter;
import com.hh.ghoststory.game_models.Ghost;
import com.hh.ghoststory.game_models.Tile;
import com.hh.ghoststory.game_models.core.GameModel;

public class GameScreen extends AbstractScreen {
	private InputMultiplexer multiplexer = new InputMultiplexer();
	private TestShader testShader = new TestShader();
	private ModelBatch modelBatch;
	private ModelBatch shadowBatch = new ModelBatch(new DepthShaderProvider());
	private PlayerCharacter character;
	private DirectionalShadowLight shadowLight;
	private boolean shadows = false;

	public Ghost ghost;
	public boolean loading;
	public AssetManager assets = new AssetManager();
	public Array<GameModel> game_models = new Array<GameModel>();
	public Environment environment = new Environment();
	public TweenManager ghostManager;

	public PerspectiveCamera camera;

	public GameScreen(GhostStory game) {
		super(game);
		this.setupLights();
		this.setupGameModels();
		this.setupCamera();
		this.setupInputProcessors();
		this.setClear(0.5f, 0.5f, 0.5f, 1f);
		this.setupModelBatch();
		this.setupTweenEngine();

		this.loadCharacter(".ghost_story/character.json");
		this.ghost.setTexture(this.character.texture != null ? "models/" + this.character.texture : "models/ghost_texture_blue.png");
	}

	@Override
	public void show() {
		super.show();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		this.ghostManager.update(Gdx.graphics.getDeltaTime());
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		this.camera.update();

		if (doneLoading()) {
			this.updateModels();
			if (this.shadows) this.renderShadows();
			this.renderModels();
		}
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
		this.setupCamera(width, height);
	}

	private void setupCamera() {
		this.setupCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	private void setupCamera(int width, int height) {
		this.camera = new PerspectiveCamera(67, width, height);
//		this.camera.setToOrtho(false, 20, 20 * ((float) height / (float) width));
//		this.camera.position.set(100, 100, 100);
		this.camera.position.set(10, 10, 10);
		this.camera.direction.set(-1, -1, -1);
		this.camera.near = 1;
		this.camera.far = 300;
	}

	/*
	 * Instantiate the game models.
	 */
	private void setupGameModels() {
		this.ghost = new Ghost();
		this.game_models.add(this.ghost);

		for (int z = 0; z < 10; z++) {
			for (int x = 0; x < 10; x++) {
				this.game_models.add(new Tile(x, 0, z));
			}
		}
		loadGameModelAssets();
	}

	/*
	 * Load the GameModel assets
	 */
	private void loadGameModelAssets() {
		for (GameModel game_model : this.game_models)
			this.assets.load(game_model.model_resource, Model.class);
		this.loading = true;
	}

	/*
	 * Check if assets have all been loaded. Run in a loop.
	 */
	private boolean doneLoading() {
		if (this.loading && !this.assets.update()) {
			return false;
		} else if (this.loading && this.assets.update()) {
			for (GameModel game_model : this.game_models) {
				game_model.setModelResource(this.assets.get(game_model.model_resource, Model.class));
			}
			this.loading = false;
			return false;
		}
		return true;
	}

	private void setupLights() {
		this.environment.add(new PointLight().set(new Color(1f, 1f, 1f, 1f), 0, 1, 0, 1));
		this.environment.add(new PointLight().set(new Color(1f, 0f, 0f, 1f), 4, 1, 4, 1));
		this.environment.add(new PointLight().set(new Color(0f, 0f, 1f, 1f), 6, 1, 0, 1));
		this.environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .1f, .1f, .1f, .2f));
		this.environment.add(new DirectionalLight().set(0.4f, 0.4f, 0.4f, -1f, -.8f, -.2f));

		if (this.shadows) {
//			environment.add((shadowLight = new DirectionalShadowLight(Gdx.graphics.getWidth() * 4, Gdx.graphics.getHeight() * 4, 10f, 10 * ((float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()), 1f, 100f)).set(0.8f, 0.8f, 0.8f, -1f, -.8f, -.2f));
			this.environment.add((this.shadowLight = new DirectionalShadowLight(4096, 4096, 30f, 30f, 1f, 100f)).set(0.8f, 0.8f, 0.8f, -1f, -.8f, -.2f));
			this.environment.shadowMap = this.shadowLight;
		}
	}

	/*
	 * Loads the character from the character json.
	 */
	private void loadCharacter(String file_path) {
		FileHandle file = Gdx.files.local(file_path);
		Json json = new Json();
		this.character = json.fromJson(PlayerCharacter.class, file.readString());
	}

	private void updateModels() {
		for (GameModel game_model : this.game_models)
			game_model.update();
	}

	/*
	 * Renders the GameModels.
	 */
	private void renderModels() {
		this.modelBatch.begin(this.camera);

		for (GameModel game_model : this.game_models)
			this.modelBatch.render(game_model.model, this.environment);

		this.modelBatch.end();
	}

	/*
	 * Renders the GameModels shadows.
	 */
	private void renderShadows() {
		this.shadowLight.begin(Vector3.Zero, this.camera.direction);
		this.shadowBatch.begin(this.shadowLight.getCamera());

		for (GameModel game_model : this.game_models)
			this.shadowBatch.render(game_model.model, this.environment);

		this.shadowBatch.end();
		this.shadowLight.end();
	}

	/*
	 * Sets up the ModelBatch
	 */
	private void setupModelBatch() {
		this.modelBatch = new ModelBatch(Gdx.files.internal("shaders/default.vertex.glsl"), Gdx.files.internal("shaders/default.fragment.glsl"));
	}

	/*
	 * Registers the GameModelTweenAccessor and initializes the TweenManager.
	 */
	private void setupTweenEngine() {
		Tween.registerAccessor(Ghost.class, new GameModelTweenAccessor());
		Tween.setCombinedAttributesLimit(4);
		this.ghostManager = new TweenManager();
	}
	/*
     * Adds processors to the multiplexer and sets it as Gdx's input processor.
     */
	private void setupInputProcessors() {
		this.multiplexer.addProcessor(this.getDefaultInputAdapter());
		this.multiplexer.addProcessor(this.getDefaultGestureDetector());
		Gdx.input.setInputProcessor(multiplexer);
	}

	/*
	 * Returns the InputAdapter for this screen. Only handles scroll now.
	 */
	private InputAdapter getDefaultInputAdapter() {
		return new InputAdapter() {
			@Override
			public boolean scrolled(int amount) {
				//Zoom out
				if (amount > 0 && GameScreen.this.camera.fieldOfView < 67)
					GameScreen.this.camera.fieldOfView += 1f;
//				if (amount > 0 && GameScreen.this.camera.zoom < 1)
//					GameScreen.this.camera.zoom += 0.1f;
				//Zoom in
				if (amount < 0 && GameScreen.this.camera.fieldOfView > 1)
					GameScreen.this.camera.fieldOfView -= 1f;
//				if (amount < 0 && GameScreen.this.camera.zoom > 0.1)
//					GameScreen.this.camera.zoom -= 0.1f;

				return false;
			}
		};
	}

	/*
	 * Returns the GestureDetector for this screen.
	 */
	private GestureDetector getDefaultGestureDetector() {
		return new GestureDetector(new GestureDetector.GestureListener() {
			private final Plane xzPlane = new Plane(new Vector3(0, 1, 0), 0);
			private Vector3 intersection = new Vector3();
			private Vector3 position = new Vector3();
			private Vector3 curr = new Vector3();
			private Vector2 last = new Vector2(-1, -1);
			private Vector3 delta = new Vector3();
			private Vector3 axisVec = new Vector3();
			private Quaternion currentRotation = new Quaternion();
			private float initialScale = 1.0f;
			private Ray pickRay;

			@Override
			public boolean touchDown(float x, float y, int pointer, int button) {
//				this.initialScale = GameScreen.this.camera.zoom;
				return false;
			}

			@Override
			public boolean tap(float x, float y, int count, int button) {
				this.pickRay = this.getPickRay(x, y);
				Intersector.intersectRayPlane(this.pickRay, this.xzPlane, this.intersection);

				this.position = GameScreen.this.ghost.model.transform.getTranslation(this.position);
				this.currentRotation = GameScreen.this.ghost.model.transform.getRotation(this.currentRotation);

				float translationDuration = this.intersection.dst(this.position) / GameScreen.this.ghost.speed;
				float newAngle = MathUtils.atan2(this.intersection.x - this.position.x, this.intersection.z - this.position.z) * 180 / MathUtils.PI;
				// lines below also in getValues of the GhostModelTweenAccessor, maybe move them
//				float angle = GameScreen.this.ghost.model.transform.getRotation(new Quaternion()).getAxisAngle(this.axisVec) * this.axisVec.nor().y;
				float currentAngle = currentRotation.getYaw();
//				currentAngle = currentRotation.getAngleAround(0,1,0);

				// Get it to rotate in the direction of the shortest difference
				if (Math.abs(newAngle - currentAngle) >  180)
					newAngle += newAngle < currentAngle ? 360 : -360;

//				newAngle = (float) foo;
				float rotationDuration = Math.abs(currentAngle - newAngle) / 200;

				System.out.println("current: " + currentAngle);
				System.out.println("new: " + newAngle);

				GameScreen.this.ghostManager.killTarget(GameScreen.this.ghost);

				Timeline.createSequence()
						.push(Tween.to(GameScreen.this.ghost, GameModelTweenAccessor.ROTATION, rotationDuration)
								.target(newAngle)
								.ease(TweenEquations.easeNone))
						.push(Tween.to(GameScreen.this.ghost, GameModelTweenAccessor.POSITION_XYZ, translationDuration).
								target(this.intersection.x, this.intersection.y, this.intersection.z)
								.ease(TweenEquations.easeNone))
// Below rotates and translates at the same time.
//						.push(Tween.to(GameScreen.this.ghost, GameModelTweenAccessor.ALL, duration).
//								target(this.intersection.x, this.intersection.y, this.intersection.z, newAngle)
//								.ease(TweenEquations.easeNone))ne))
						.start(GameScreen.this.ghostManager);

				return false;
			}

			@Override
			public boolean longPress(float x, float y) {
				return false;
			}

			@Override
			public boolean fling(float velocityX, float velocityY, int button) {
				return false;
			}

			@Override
			public boolean pan(float x, float y, float deltaX, float deltaY) {
				this.pickRay = this.getPickRay(x, y);
				Intersector.intersectRayPlane(this.pickRay, xzPlane, curr);

				if (!(this.last.x == -1 && this.last.y == -1)) {
					this.pickRay = GameScreen.this.camera.getPickRay(this.last.x, this.last.y);
					Intersector.intersectRayPlane(this.pickRay, xzPlane, delta);
					this.delta.sub(this.curr);
					GameScreen.this.camera.position.add(this.delta.x, this.delta.y, this.delta.z);
				}

				this.last.set(x, y);

				return false;
			}

			@Override
			public boolean panStop(float x, float y, int pointer, int button) {
				this.last.set(-1, -1);
				return false;
			}

			@Override
			public boolean zoom(float initialDistance, float distance) {
				return false;
			}

			@Override
			public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
				float ratio = initialPointer1.dst(initialPointer2) / pointer1.dst(pointer2);
//				GameScreen.this.camera.zoom = MathUtils.clamp(this.initialScale * ratio, 0.1f, 1.0f);
				return false;
			}
			private Ray getPickRay(float x, float y) {
				return GameScreen.this.camera.getPickRay(x, y);
			}
		});
	}
}