package com.hh.ghoststory.screens;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.TestShader;
import com.hh.ghoststory.tweenAccessors.ColorAccessor;
import com.hh.ghoststory.tweenAccessors.GameModelTweenAccessor;
import com.hh.ghoststory.tweenAccessors.PointLightTweenAccessor;
import com.hh.ghoststory.actors.PlayerCharacter;
import com.hh.ghoststory.game_models.Ghost;
import com.hh.ghoststory.game_models.Tile;
import com.hh.ghoststory.game_models.core.GameModel;
import com.hh.ghoststory.renderers.ModelBatchRenderer;
import com.hh.ghoststory.tweenAccessors.Vector3Accessor;

import java.util.Random;

public class GameScreen extends AbstractScreen {
	private ModelBatchRenderer renderer;
	private InputMultiplexer multiplexer = new InputMultiplexer();
	private TestShader testShader = new TestShader();
	private ModelBatch shadowBatch = new ModelBatch(new DepthShaderProvider());
	private PlayerCharacter character;
	private DirectionalShadowLight shadowLight;
	private boolean shadows = false;

	public Ghost ghost;
	public boolean loading;
	public Array<GameModel> gameModels = new Array<GameModel>();
	public TweenManager tweenManager = new TweenManager();
	private AnimationController controller;
	private PointLight fooLight;
	private PointLight barLight;
	private Color barColor = new Color(0.6f,0.2f,1f,1f);
	FPSLogger logger = new FPSLogger();

	public GameScreen(GhostStory game) {
		super(game);
		setUpRenderer();
		setupLights();
		setupShadows();
		if (shadows) activateShadows();
		renderer.setUpDefaultCamera(ModelBatchRenderer.ORTHO);

		setupGameModels();
		loadGameModelAssets();

		this.setupInputProcessors();
		this.setClear(0.5f, 0.5f, 0.5f, 1f);
		this.setupTweenEngine();

		this.loadCharacter(".ghost_story/character.json");
//		this.ghost.setTexture(this.character.texture != null ? "models/" + this.character.texture : "models/ghost_texture_blue.png");
	}

	private void setUpRenderer() {
		renderer = new ModelBatchRenderer();
		renderer.setUpDefaultCamera(ModelBatchRenderer.PERSP);
	}
	@Override
	public void show() {
		super.show();
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		this.tweenManager.update(Gdx.graphics.getDeltaTime());
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		renderer.getActiveCamera().update();
		if (doneLoading()) {
			this.updateModels();
			if (this.shadows) this.renderShadows();
			renderer.setRenderables(collectModelInstances());
			controller.update(Gdx.graphics.getDeltaTime());
			renderer.render();
		}
//		logger.log();

	}

	/*
	 * Builds an Array<ModelInstance> from the model fields attached to gameModels
	 */
	private Array<ModelInstance> collectModelInstances() {
		Array<ModelInstance> modelInstances = new Array<ModelInstance>(gameModels.size);
		for (GameModel gameModel : gameModels) modelInstances.add(gameModel.model);
		return modelInstances;
	}

	@Override
	public void dispose() {
		super.dispose();
		shadowBatch.dispose();
		shadowLight.dispose();
		testShader.dispose();
	}

	/*
	 * Resizes the camera viewport to reflect the new size. Could maybe set a default zoom or layout for different sized screens.
	 * 
	 * @see com.hh.ghoststory.screens.AbstractScreen#resize(int, int)
	 */
	@Override
	public void resize(int width, int height) {
		renderer.setCameraViewport(width, height);
	}

	/*
	 * Instantiate the game models.
	 */
	private void setupGameModels() {
		this.ghost = new Ghost();
		this.gameModels.add(this.ghost);

		for (int z = 0; z < 10; z++) {
			for (int x = 0; x < 20; x++) {
				this.gameModels.add(new Tile(x, 0, z));
//				this.gameModels.add(new Tile());
			}
		}
		for (int z = 0; z < 3; z++) {
			for (int y = 0; y < 2; y++) {
				Tile tile = new Tile(9.5f,y + 0.5f,z + 5);
				tile.rotation = 90;
				tile.verticalAxis = new Vector3(0,0,1);
				this.gameModels.add(tile);
			}
		}
		for (int z = 0; z < 3; z++) {
			this.gameModels.add(new Tile(10, 2, z + 5));
		}
		for (int y = 0; y < 2; y++) {
			Tile tile = new Tile(10,y + 0.5f,7.5f);
			tile.rotation = 90;
			tile.verticalAxis = new Vector3(1,0,0);
			this.gameModels.add(tile);
		}
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
			renderer.assetManager.load(gameModel.model_resource, Model.class);
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
			controller = new AnimationController(ghost.model);
			controller.setAnimation("float", -1);
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
				new DirectionalLight().set(0.4f, 0.4f, 0.4f, -1f, -.8f, -.2f),
				fooLight,
				barLight
		};
		renderer.setUpLights(lights);
	}

	private void setupShadows() {
//			environment.add((shadowLight = new DirectionalShadowLight(Gdx.graphics.getWidth() * 4, Gdx.graphics.getHeight() * 4, 10f, 10 * ((float) Gdx.graphics.getHeight() / (float) Gdx.graphics.getWidth()), 1f, 100f)).set(0.8f, 0.8f, 0.8f, -1f, -.8f, -.2f));
			shadowLight = new DirectionalShadowLight(4096, 4096, 30f, 30f, 1f, 100f);
			shadowLight.set(0.4f, 0.4f, 0.4f, 1f, -.8f, -.2f);
//			this.environment.add((this.shadowLight = new DirectionalShadowLight(4096, 4096, 30f, 30f, 1f, 100f)).set(0.4f, 0.4f, 0.4f, -1f, -.8f, -.2f));
//		this.renderer.environment.add(shadowLight);
	}

	private void activateShadows() {
		System.out.println("activated");
		this.renderer.environment.shadowMap = this.shadowLight;
	}
	private void deactivateShadows() {
		this.renderer.environment.shadowMap = null;
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
		for (GameModel game_model : this.gameModels)
			game_model.update();
	}

	/*
	 * Renders the GameModels shadows.
	 */
	private void renderShadows() {
//		System.out.println("rendering shadows");
		this.shadowLight.begin(Vector3.Zero, this.renderer.getActiveCamera().direction);
		this.shadowBatch.begin(this.shadowLight.getCamera());

		for (GameModel game_model : this.gameModels)
			this.shadowBatch.render(game_model.model, this.renderer.environment);

		this.shadowBatch.end();
		this.shadowLight.end();
	}

	/*
	 * Registers the GameModelTweenAccessor and initializes the TweenManager.
	 */
	private void setupTweenEngine() {
		Tween.registerAccessor(Ghost.class, new GameModelTweenAccessor());
		Tween.registerAccessor(PointLight.class, new PointLightTweenAccessor());
		Tween.registerAccessor(Vector3.class, new Vector3Accessor());
		Tween.registerAccessor(Color.class, new ColorAccessor());
		Tween.setCombinedAttributesLimit(4);

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
				GameScreen.this.renderer.zoomCamera(amount);
				return false;
			}
			@Override
			public boolean keyUp (int keycode) {
				switch(keycode) {
					case Input.Keys.S:
						GameScreen.this.shadows = !GameScreen.this.shadows;
						if (GameScreen.this.shadows)
							GameScreen.this.activateShadows();
						else
							GameScreen.this.deactivateShadows();
						break;
					case Input.Keys.C:
						if (GameScreen.this.renderer.getActiveCameraType() == ModelBatchRenderer.PERSP) {
							GameScreen.this.renderer.setActiveCameraType(ModelBatchRenderer.ORTHO);
						} else if (GameScreen.this.renderer.getActiveCameraType() == ModelBatchRenderer.ORTHO) {
							GameScreen.this.renderer.setActiveCameraType(ModelBatchRenderer.PERSP);
						}
						GameScreen.this.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
						break;
					case Input.Keys.L:
						Random generator = new Random();
						float red = generator.nextFloat();
						float green = generator.nextFloat();
						float blue = generator.nextFloat();
						GameScreen.this.barLight.set(new Color(red,green,blue,1f),12,1,10,1);
						break;
				}
				return false;
			}
		};
	}

	private void tweenFaceAndMoveTo(GameModel target, float rotation, float rotDur, float x, float y, float z, float transDur) {
		Timeline.createSequence()
				.push(Tween.to(target, GameModelTweenAccessor.ROTATION, rotDur)
						.target(rotation)
						.ease(TweenEquations.easeNone))
				.push(Tween.to(target, GameModelTweenAccessor.POSITION_XYZ, transDur).
						target(x, y, z)
						.ease(TweenEquations.easeNone))
// Below rotates and translates at the same time.
//						.push(Tween.to(this.ghost, GameModelTweenAccessor.ALL, duration).
//								target(x, y, z, newAngle)
//								.ease(TweenEquations.easeNone))
				.start(tweenManager);
	}

	private Ray getPickRay(float x, float y) {
		return renderer.getActiveCamera().getPickRay(x, y);
	}
	/*
	 * Returns the GestureDetector for this screen.
	 */
	private GestureDetector getDefaultGestureDetector() {
		return new GestureDetector(new GestureDetector.GestureListener() {
			private final Plane xzPlane = new Plane(new Vector3(0, 1, 0), 0);
//			private Vector3 intersection = new Vector3();
			private Vector3 curr = new Vector3();
			private Vector2 last = new Vector2(-1, -1);
			private Vector3 delta = new Vector3();
			private Vector3 axisVec = new Vector3();
			private float initialScale = 1.0f;
			private Ray pickRay;

			@Override
			public boolean touchDown(float x, float y, int pointer, int button) {
//				this.initialScale = GameScreen.this.camera.zoom;
				return false;
			}

			@Override
			public boolean tap(float x, float y, int count, int button) {
				pickRay = GameScreen.this.getPickRay(x, y);
				Vector3 intersection = getIntersection(pickRay);
//				Intersector.intersectRayPlane(this.pickRay, this.xzPlane, this.intersection);

				Vector3 position = new Vector3();
				position = GameScreen.this.ghost.model.transform.getTranslation(position);
				float translationDuration = intersection.dst(position) / GameScreen.this.ghost.speed;
				float newAngle = MathUtils.atan2(intersection.x - position.x, intersection.z - position.z) * 180 / MathUtils.PI;

				Quaternion currentRotation = new Quaternion();
				currentRotation = GameScreen.this.ghost.model.transform.getRotation(currentRotation);
				float currentAngle = currentRotation.getYaw();

				// Get it to rotate in the direction of the shortest difference
				if (Math.abs(newAngle - currentAngle) >  180)
					newAngle += newAngle < currentAngle ? 360 : -360;

				float rotationDuration = Math.abs(currentAngle - newAngle) / 200;

//				GameScreen.this.tweenManager.killTarget(GameScreen.this.ghost);
				GameScreen.this.tweenManager.killTarget(GameScreen.this.ghost, GameModelTweenAccessor.POSITION_XYZ);
				GameScreen.this.tweenManager.killTarget(GameScreen.this.ghost, GameModelTweenAccessor.ROTATION);
				GameScreen.this.tweenFaceAndMoveTo(GameScreen.this.ghost, newAngle, rotationDuration, intersection.x, intersection.y, intersection.z, translationDuration);

				return false;
			}

			@Override
			public boolean longPress(float x, float y) {
				GameScreen.this.shadows = !GameScreen.this.shadows;
				if (GameScreen.this.shadows)
					GameScreen.this.activateShadows();
				else
					GameScreen.this.deactivateShadows();
				return false;
			}

			@Override
			public boolean fling(float velocityX, float velocityY, int button) {
				return false;
			}

			@Override
			public boolean pan(float x, float y, float deltaX, float deltaY) {
				this.pickRay = GameScreen.this.renderer.getActiveCamera().getPickRay(x, y);
				Intersector.intersectRayPlane(this.pickRay, xzPlane, curr);

				if (!(this.last.x == -1 && this.last.y == -1)) {
					this.pickRay = GameScreen.this.renderer.getActiveCamera().getPickRay(this.last.x, this.last.y);
					Intersector.intersectRayPlane(this.pickRay, xzPlane, delta);
					this.delta.sub(this.curr);
					GameScreen.this.renderer.getActiveCamera().position.add(this.delta.x, this.delta.y, this.delta.z);
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
				float zoom = distance - initialDistance;
//				float zoom = initialDistance / distance;
				float deltaTime = Gdx.graphics.getDeltaTime();
//				float speed = 0.1f * deltaTime;
				// amount fingers moved apart divided by time. should be speed of movement
				float speed = zoom / deltaTime;
				Vector3 camZoom = new Vector3();
				camZoom.set(GameScreen.this.renderer.getActiveCamera().direction.cpy());
				camZoom.nor().scl(speed * deltaTime / 100);


				if( ((GameScreen.this.renderer.getActiveCamera().position.y > 3f) && (zoom > 0)) || ((GameScreen.this.renderer.getActiveCamera().position.y < 10f) && (zoom < 0)) ) {
					GameScreen.this.renderer.getActiveCamera().translate(camZoom.x, camZoom.y, camZoom.z);
				}
//				float factor = distance / initialDistance;
//
//
//				if (initialDistance > distance && GameScreen.this.camera.fieldOfView < 120f) {
//					GameScreen.this.camera.fieldOfView += factor;
//				} else if (initialDistance < distance && GameScreen.this.camera.fieldOfView > 10f) {
//					GameScreen.this.camera.fieldOfView -= factor;
//				}
System.out.println("here " + zoom);
				return false;
			}

			@Override
			public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
//				System.out.println("pinch");
//				float ratio = initialPointer1.dst(initialPointer2) / pointer1.dst(pointer2);
////				GameScreen.this.camera.zoom = MathUtils.clamp(this.initialScale * ratio, 0.1f, 1.0f);
//				GameScreen.this.camera.fieldOfView += MathUtils.clamp(this.initialScale * ratio, 0.1f, 1.0f);
//				GameScreen.this.camera.
				return false;
			}

			private Vector3 getIntersection(Ray pickRay) {
				Vector3 intersection = new Vector3();
				Intersector.intersectRayPlane(pickRay, this.xzPlane, intersection);
				return intersection;
			}
		});
	}
}