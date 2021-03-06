package com.hh.ghoststory.screen;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
//import com.badlogic.gdx.graphics.g3d.shadow.system.classical.ClassicalShadowSystem;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.ScreenshotFactory;
import com.hh.ghoststory.entity.EntityTypes;
import com.hh.ghoststory.entity.Mappers;
import com.hh.ghoststory.entity.components.*;
import com.hh.ghoststory.entity.systems.BehaviorSystem;
import com.hh.ghoststory.entity.systems.BoundingBoxSystem;
import com.hh.ghoststory.lib.MessageTypes;
import com.hh.ghoststory.lib.tween.Timelines;
import com.hh.ghoststory.lib.tween.accessors.ColorAccessor;
import com.hh.ghoststory.lib.tween.accessors.QuaternionAccessor;
import com.hh.ghoststory.lib.tween.accessors.Vector3Accessor;
import com.hh.ghoststory.lib.utility.Config;
import com.hh.ghoststory.lib.utility.UserData;
import com.hh.ghoststory.render.renderers.ShadowRenderer;
import com.hh.ghoststory.render.shaders.PlayShader;
import com.hh.ghoststory.scene.Lighting;
import com.hh.ghoststory.scene.lights.core.Caster;
import com.hh.ghoststory.scene.lights.core.PointCaster;
import com.hh.ghoststory.screen.input.PlayDetector;

/**
 * Created by nils on 7/14/15.
 * Screen for interaction with the game world. Not inventory, not save menus, not stats, just the gameworld.
 */
public class PlayScreen extends AbstractScreen implements Telegraph {
	protected ShadowRenderer renderer = new ShadowRenderer(this);
	protected PerspectiveCamera perspective;
	protected OrthographicCamera orthographic;
	protected Camera active;
	public AssetManager assetManager = new AssetManager();
	public Lighting lighting = new Lighting();
	private TweenManager tweenManager = new TweenManager();

	public enum CameraTypes { P, O }
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	public Array<Caster> casters = new Array<Caster>();
	public PlayDetector playDetector;

	private FPSLogger logger = new FPSLogger();

//	private ClassicalShadowSystem shadowSystem;
	private Array<ModelBatch> passBatches = new Array<ModelBatch>();
	private ModelBatch shadowModelBatch;

	/**
	 * Creates the Screen with a default camera.
	 * @param game
	 */
	public PlayScreen(final GhostStory game) {
		super(game);
		activateCamera(defaultPerspective());
		setInput();
		setUpEntities();
		frameworkDispatcher.addListener(this, MessageTypes.Framework.TOUCH_DOWN);
		frameworkDispatcher.addListener(this, MessageTypes.Framework.TAP);
		loading = true;

		// Tween setup
		Tween.setCombinedAttributesLimit(4);
		Tween.registerAccessor(Vector3.class, new Vector3Accessor());
		Tween.registerAccessor(Quaternion.class, new QuaternionAccessor());
		Tween.registerAccessor(Color.class, new ColorAccessor());

//		shadowSystem = new ClassicalShadowSystem(active, instances);
//		lighting.addListener(shadowSystem);
//
//		for (int i = 0; i < shadowSystem.getPassQuantity(); i++) {
//			passBatches.add(new ModelBatch(shadowSystem.getPassShaderProvider(i)));
//		}
//		shadowModelBatch = new ModelBatch(shadowSystem.getShaderProvider());
	}

	protected void setUpEntities() {
		// this should be somewhere else
		// assetManager.load("models/ghost_texture_blue.png", Pixmap.class);
		ImmutableArray<Entity> entities = Config.engine.getEntities();

		for (Entity entity : entities) {
			// load the Model for each Entity with a GeometryComponent
			if (Mappers.geometry.has(entity))
				assetManager.load("models/" + Mappers.geometry.get(entity).file, Model.class);
			// set ambient if it's there.
			if (Mappers.ambient.has(Config.engine.getEntitiesFor(EntityTypes.SCENE).get(0)))
				lighting.set(Mappers.ambient.get(Config.engine.getEntitiesFor(EntityTypes.SCENE).get(0)).colorAttribute);
			// lights. this is ugly now and needs to be better. Entity system.
			if (Mappers.pointLight.has(entity)) {
				PointCaster caster = Mappers.pointLight.get(entity).caster(new PointCaster(Mappers.color.get(entity).color, Mappers.position.get(entity).position, Mappers.intensity.get(entity).intensity)).caster;
				lighting.add(caster);
				if (Mappers.shadowCasting.has(entity))
					casters.add(caster);
			}
			if (Mappers.spotLight.has(entity)) {
				Caster caster = Mappers.spotLight.get(entity).caster;
				lighting.add(caster);
				if (Mappers.shadowCasting.has(entity))
					casters.add(caster);
			}
		}
	}
	/**
	 * A way for input handlers to access the active camera until camera controlling functions moved here. If they are.
	 * @return
	 */
	public Camera active() {
		return active;
	}
	/**
	 * Override this in derived classes, unless you want only mouse camera control.
	 */
	public void setInput() {
		playDetector = new PlayDetector(this, frameworkDispatcher);
		Gdx.input.setInputProcessor(playDetector);
	}

	public PlayScreen(GhostStory game, Camera camera) {
		super(game);
		activateCamera(camera);
		setInput();
	}

	/**
	 * @TODO pass in correct params to activateOrthographicCamera
	 * @param width
	 * @param height
	 */
	@Override
	public void resize(int width, int height) {
		active.position.set(active.position);
		active.viewportWidth = width;
		active.viewportHeight = height;
		active.update();

		if (active instanceof OrthographicCamera)
			activateOrthographicCamera((OrthographicCamera) active);

		renderer.initShadowBuffer(width, height);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		active.update();
		instances.clear();
		casters.clear();

		if (loading && assetManager.update()) {
			doneLoading();
		} else if (!loading){
			// use family here to gather all entities as one (light, model, both, etc). Then
			// check in ifs Mappers.animation.has(entity) to set
			ImmutableArray<Entity> renderables = Config.engine.getEntitiesFor(Family.all(InstanceComponent.class).get());
			for (Entity renderable : renderables) {
				ModelInstance instance = Mappers.instance.get(renderable).instance;

				if (Mappers.rotation.has(renderable))
					instance.transform.set(Mappers.rotation.get(renderable).rotation);

				if (Mappers.position.has(renderable))
					instance.transform.setTranslation(Mappers.position.get(renderable).position);

				if (Mappers.animation.has(renderable))
					Mappers.animation.get(renderable).controller.update(delta);

				instances.add(instance);
			}

			for (Entity light : Config.engine.getEntitiesFor(Family.all(PointLightComponent.class).get())) {
				Mappers.pointLight.get(light).caster.setPosition(Mappers.position.get(light).position);
				Mappers.pointLight.get(light).caster.setColor(Mappers.color.get(light).color);

				if (Mappers.pointLight.get(light).shadowing)
					casters.add(Mappers.pointLight.get(light).caster);
			}

			for (Entity light : Config.engine.getEntitiesFor(Family.all(SpotLightComponent.class).get())) {
				Mappers.spotLight.get(light).caster.setPosition(Mappers.position.get(light).position);
				Mappers.spotLight.get(light).caster.setColor(Mappers.color.get(light).color);

				if (Mappers.spotLight.get(light).shadowing)
					casters.add(Mappers.spotLight.get(light).caster);

				Mappers.spotLight.get(light).caster.direction.set(Mappers.direction.get(light).direction);
				Mappers.spotLight.get(light).caster.camera.direction.set(Mappers.direction.get(light).direction);

			}
		}

		frameworkDispatcher.update(delta);
		tweenManager.update(delta);
		active.update();
		playDetector.update();

		renderer.render(active, instances, casters, lighting);

//		shadowSystem.update();
//
//		for (int i = 0; i < shadowSystem.getPassQuantity(); i++) {
//			shadowSystem.begin(i);
//			Camera camera;
//			while ((camera = shadowSystem.next()) != null) {
//				passBatches.get(i).begin(camera);
//				passBatches.get(i).render(instances, lighting);
//				passBatches.get(i).end();
//			}
//			camera = null;
//			shadowSystem.end(i);
//		}
//
//		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//		Gdx.gl.glClearColor(0, 0, 0, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//
//		shadowModelBatch.begin(active);
//		shadowModelBatch.render(instances, lighting);
//		shadowModelBatch.end();
//		ScreenshotFactory.saveScreenshot(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), "scene");

		logger.log();
	}
	/**
	 * Called when the asset manager has finished updating. Associate models with game entities and start animations..
	 * @TODO Add more stuff that needs to happen after loading.
	 */
	@Override
	protected void doneLoading() {
		super.doneLoading();
		// retrieve ModelInstances from the assetManager and assign them to the renderable Entity.
		for (Entity entity : Config.engine.getEntitiesFor(Family.all(GeometryComponent.class).get())) {
			ModelInstance instance = new ModelInstance(assetManager.get("models/" + Mappers.geometry.get(entity).file, Model.class));

			if (Mappers.id.get(entity).id == "select_ghost") instance.getMaterial("skin").set(new PlayShader.AlphaAttribute(1.0f));

			// need to get an editable version of the entity since we add a component.
			Config.engine.getEntity(entity.getId()).add(new InstanceComponent(instance));

			// if the renderable Entity has a default/standing animation, set that up.
			// @TODO refactor normal/default animation selection. Check if a normal should even be played.
			if (Mappers.animation.has(entity)) {
				AnimationComponent animation = Mappers.animation.get(entity);
				animation.init(instance);

				// the normal/default/rest animation should be defined on the entity somewhere, not just default to "default".
				ObjectMap<String, Object> normal = animation.animations.get("default");

				// this casting sucks. JSON import might be good to use, should fix that.
				animation.controller.setAnimation(
					(String) normal.get("id"),
					(Float) normal.get("offset"),
					(Float) normal.get("duration"),
					(Integer) normal.get("loopcount"),
					(Float)normal.get("speed"),
					(AnimationController.AnimationListener)normal.get("listener")
				);
			}
		}
		Config.engine.addSystem(new BoundingBoxSystem());
		Config.engine.addSystem(new BehaviorSystem(tweenManager));
	}

	/** Camera Section */
	public void activateCamera(CameraTypes type) {
		if (type == CameraTypes.P)
			active = perspective;
		else if (type == CameraTypes.O)
			active = orthographic;

	}
	/**
	 * Sets up the default camera (either Perspective or Orthographic) for the screen.
	 *
	 * @param camera
	 */
	public void activateCamera(Camera camera) {
		if (camera instanceof PerspectiveCamera) {
			perspective = (PerspectiveCamera) camera;
		} else if (camera instanceof OrthographicCamera) {
			orthographic = (OrthographicCamera) camera;
		} else {
			return;
		}

		active = camera;
	}

	/**
	 * Configures the perspective camera. If one hasn't been instantiated yet, it will create one.
	 * @param fieldOfViewY
	 * @param viewportWidth
	 * @param viewportHeight
	 * @param position
	 * @param direction
	 * @param near
	 */
	public void configurePerspective(float fieldOfViewY, float viewportWidth, float viewportHeight, Vector3 position, Vector3 direction, int near) {
		if (perspective == null) perspective = defaultPerspective();
		perspective = new PerspectiveCamera(fieldOfViewY, viewportWidth, viewportHeight);
		perspective.position.set(position);
		perspective.direction.set(direction);
		perspective.near = near;
	}

	public void configureOrthographic(boolean yDown, int viewportWidth, int near, int far) {
		if (orthographic == null) orthographic = defaultOrthographic();
		orthographic.setToOrtho(yDown, viewportWidth, viewportWidth * (Gdx.graphics.getWidth() / Gdx.graphics.getHeight()));
		orthographic.near = near;
		orthographic.far = far;
	}
	/**
	 * Unused.
	 * Sets `perspective`
	 * @param camera
	 */
	public void setPerspective(PerspectiveCamera camera) {
		perspective = camera;
	}
	/**
	 * Unused
	 * Sets `orthographic`
	 * @param camera
	 */
	public void setOrthographic(OrthographicCamera camera) {
		orthographic = camera;
	}
	/**
	 * Returns `active`.
	 * @return
	 */
	public Camera getActiveCamera() {
		return active;
	}
	/**
	 * Activates the perspective camera.
	 */
	public void activatePerspective() {
		if (perspective == null) perspective = defaultPerspective();
		active = perspective;
	}

	/**
	 * Sets and activates a new PerspectiveCamera
	 * @param camera
	 */
	public void activatePerspective(PerspectiveCamera camera) {
		perspective = camera;
		active = perspective;
	}

	/**
	 * Activates the orthographic camera.
	 */
	public void activateOrthographic() {
		if (orthographic == null) orthographic = defaultOrthographic();
		active = orthographic;
	}

	/**
	 * Sets and activates a new OrthographicCamera
	 * @param camera
	 */
	public void activateOrthographic(OrthographicCamera camera) {
		orthographic = camera;
		active = orthographic;
	}
	/**
	 * Returns a pickray.
	 * @param x
	 * @param y
	 * @return
	 */
	public Ray getPickRay(float x, float y) {
		return active.getPickRay(x, y);
	}

	/**
	 * @TODO try copying rotate functionality from CameraInputController, but for perspective
	 * and ortho. Also keep ortho zoom, but make it smoother.
	 *
	 * Generic function to accept zooms. Passes it to specific handler.
	 * Not being used now. Is this needed? Route all input events to here?
	 * @param zoom
	 */
	public void zoom(double zoom) {
		if (active == perspective)
			zoomPerspective(zoom);
		else if (active == orthographic)
			zoomOrthographic(zoom);
		else return;
	}

	/**
	 * Zooms `perspective`
	 * @param amount
	 * @TODO parameterize the FoV limits.
	 */
	private void zoomPerspective(double amount) {
		//Zoom out
		if (amount > 0 && perspective.fieldOfView < 67)
			perspective.fieldOfView += 1f;
		//Zoom in
		if (amount < 0 && perspective.fieldOfView > 1)
			perspective.fieldOfView -= 1f;
	}

	/**
	 * Zooms `orthographic`
	 * @param amount
	 * @TODO parameterize amount limits.
	 */
	private void zoomOrthographic(double amount) {
		//Zoom out
		if (amount > 0 && orthographic.zoom < 1)
			orthographic.zoom += 0.1f;
		//Zoom in
		if (amount < 0 && orthographic.zoom > 0.1)
			orthographic.zoom -= 0.1f;
	}

	/**
	 * Returns a default PerspectiveCamera
	 * @return
	 */
	private PerspectiveCamera defaultPerspective() {
		PerspectiveCamera camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(0, 40, 0);
//		camera.direction.set(-1, -1, -1);
		camera.lookAt(0, 0, 0);
		camera.near = 1;
		camera.far = 300;
		return camera;
	}

	/**
	 * Returns a default OrthographicCamera
	 * @return
	 * @TODO fix this some, config options might not be necessary.
	 */
	private OrthographicCamera defaultOrthographic() {
		OrthographicCamera camera = new OrthographicCamera();
		camera.setToOrtho(false, 20, 20 * (Gdx.graphics.getWidth() / Gdx.graphics.getHeight()));
		camera.near = 1;
		camera.far = 300;
		return camera;
	}
	/*********************************************************************************************************
	 *********************************************************************************************************/
	/**
	 * Sets up an OrthographicCamera.
	 *
	 * @param camera
	 */
	public void activateOrthographicCamera(OrthographicCamera camera) {
		activateOrthographicCamera(camera, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	/**
	 * @param camera
	 * @param viewportWidth
	 * @param viewportHeight
	 */
	public void activateOrthographicCamera(OrthographicCamera camera, float viewportWidth, float viewportHeight) {
		camera.setToOrtho(false, 20, 20 * (viewportHeight / viewportWidth));
		camera.position.set(100, 100, 100);
//		camera.direction.set(-1, -1, -1);
		camera.lookAt(0, 0, 0);
		camera.near = 1;
		camera.far = 300;

//		if (orthographicCamera != camera)
//			orthographicCamera = camera;
	}

	/**
	 * @param camera
	 */
	public void activatePerspectiveCamera(PerspectiveCamera camera) {
		activatePerspectiveCamera(camera, 67);
	}

	/**
	 *
	 * @param camera
	 * @param fieldOfViewY
	 */
	public void activatePerspectiveCamera(PerspectiveCamera camera, float fieldOfViewY) {
		camera.fieldOfView = fieldOfViewY;
		camera.position.set(10, 10, 10);
		camera.direction.set(-1, -1, -1);
		camera.near = 1;
	}

	/**
	 *
	 * @return
	 */
//	public OrthographicCamera getOrthographicCamera() {
//		if (orthographicCamera == null)
//			activateOrthographicCamera(new OrthographicCamera());
//		return orthographicCamera;
//	}
//
//	/**
//	 *
//	 * @return
//	 */
//	public PerspectiveCamera getPerspectiveCamera() {
//		if (perspectiveCamera == null)
//			activatePerspectiveCamera(new PerspectiveCamera());
//		return perspectiveCamera;
//	}
	/** End Camera Section */

	@Override
	public void dispose() {
		super.dispose();
		assetManager.dispose();
		renderer.dispose();
	}
	@Override
	public boolean handleMessage(Telegram msg) {
		switch (msg.message) {
			case MessageTypes.Framework.TOUCH_DOWN:

				break;
			case MessageTypes.Framework.TAP:
				Entity pc = Config.engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).get(0);
				Quaternion rotation = Mappers.rotation.get(Config.engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).get(0)).rotation;
				Vector3 position = Mappers.position.get(pc).position;

				tweenManager.killTarget(position, Vector3Accessor.XYZ);
				tweenManager.killTarget(rotation, QuaternionAccessor.ROTATION);

				// @TODO change that 3 to a character Entity Component value.
				Timelines.faceAndGo(rotation, position, (Vector3) msg.extraInfo, 3).start(tweenManager);
				break;
			default:
				break;
		}
		return false;
	}
}