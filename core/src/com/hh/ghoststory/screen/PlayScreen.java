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
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.hh.ghoststory.GhostStory;
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
import com.hh.ghoststory.render.renderers.ShadowRenderer;
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
    public PlayDetector playDetector;
    public Entity scene;
    public Entity pc;
    public ImmutableArray<Entity> mobs;
    public ImmutableArray<Entity> lights;

    private FPSLogger logger = new FPSLogger();

    /**
     * Creates the Screen with a default camera.
     * @param game
     */
    public PlayScreen(final GhostStory game) {
        super(game);
        activateCamera(defaultPerspective());
        setInput();
        setEntities();
	    frameworkDispatcher.addListener(this, MessageTypes.Framework.TOUCH_DOWN);
	    frameworkDispatcher.addListener(this, MessageTypes.Framework.TAP);
        loading = true;

        Tween.setCombinedAttributesLimit(4);
        Tween.registerAccessor(Vector3.class, new Vector3Accessor());
        Tween.registerAccessor(Quaternion.class, new QuaternionAccessor());
        Tween.registerAccessor(Color.class, new ColorAccessor());

        game.engine.addSystem(new BoundingBoxSystem());
        game.engine.addSystem(new BehaviorSystem(tweenManager));
    }

    protected void setEntities() {
		// this should be somewhere else
		// assetManager.load("models/ghost_texture_blue.png", Pixmap.class);
		// load all geometry
		ImmutableArray<Entity> geometry = game.engine.getEntitiesFor(Family.all(GeometryComponent.class).get());
		for (Entity geo : geometry)
			assetManager.load("models/" + Mappers.geometry.get(geo).file, Model.class);

        // get the scene. just model right now.
        scene = game.engine.getEntitiesFor(EntityTypes.SCENE).get(0);
        // add ambient if it's there.
        if (Mappers.ambient.has(scene))
            lighting.set(Mappers.ambient.get(scene).colorAttribute);
        // done scene.

        // pc
        pc = game.engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).get(0);
        // end pc

        // mobs
        mobs = game.engine.getEntitiesFor(EntityTypes.MOB);

        for (Entity mob : mobs)
            assetManager.load("models/" + Mappers.geometry.get(mob).file, Model.class);
        // end mobs

        //lights
        // this is ugly now and needs to be better. Entity system.
        lights = game.engine.getEntitiesFor(EntityTypes.LIGHT);
        for (Entity light : lights) {
            PointCaster caster = Mappers.lighting.get(light).caster(new PointCaster(Mappers.color.get(light).color, Mappers.position.get(light).position, Mappers.intensity.get(light).intensity)).caster;
			assetManager.load("models/" + Mappers.geometry.get(light).file, Model.class);
            lighting.add(caster);
            if (Mappers.shadowCasting.has(light))
                casters.add(caster);
        }
        // end lights
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
        active.viewportWidth = width;
        active.viewportHeight = height;

        if (active instanceof OrthographicCamera)
            activateOrthographicCamera((OrthographicCamera) active);

        renderer.initShadowBuffer();
    }

    @Override
    public void render(float delta) {
        super.render(delta);
	    // gl stuff happens in renderer, move this there probably.
//        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        instances.clear();

        if (loading && assetManager.update()) {
            doneLoading();
        } else if (!loading){
			// use family here to gather all entities as one (light, model, both, etc). Then
			// check in ifs Mappers.animation.has(entity) to set
            ImmutableArray<Entity> renderables = game.engine.getEntitiesFor(Family.all(InstanceComponent.class).get());
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

            for (Entity light : lights) {
                Mappers.lighting.get(light).caster.setPosition(Mappers.position.get(light).position);
                Mappers.lighting.get(light).caster.setColor(Mappers.color.get(light).color);
            }
        }

        frameworkDispatcher.update(delta);
        tweenManager.update(delta);
        active.update();
        playDetector.update();

        renderer.render(active, instances, casters, lighting);
        logger.log();
    }

    /**
     * Called when the asset manager has finished updating. Associate models with game entities and start animations..
     * @TODO Add more stuff that needs to happen after loading.
     */
    @Override
    protected void doneLoading() {
        super.doneLoading();
        ImmutableArray<Entity> renderables = game.engine.getEntitiesFor(Family.all(GeometryComponent.class).get());

        // retrieve ModelInstances from the assetManager and assign them to the renderable Entity.
        for (Entity renderable : renderables) {
            ModelInstance instance = new ModelInstance(assetManager.get("models/" + Mappers.geometry.get(renderable).file, Model.class));
			// need to get an editable version of the entity since we add a component.
			game.engine.getEntity(renderable.getId()).add(new InstanceComponent(instance));
            // if the renderable Entity has a default/standing animation, set that up.
            // @TODO refactor normal/default animation selection. Check if a normal should even be played.
            if (Mappers.animation.has(renderable)) {
                AnimationComponent animation = Mappers.animation.get(renderable);
                animation.init(instance);
                // the normal/default/rest animation should be defined on the entity somewhere, not just default to "normal".
                ObjectMap<String, Object> normal = animation.animations.get("normal");
                // this casting sucks. JSON import might be good to use, should fix that.
                animation.controller.setAnimation((String) normal.get("id"), (Float) normal.get("offset"), (Float) normal.get("duration"), (Integer) normal.get("loopcount"), (Float)normal.get("speed"), (AnimationController.AnimationListener)normal.get("listener"));
            }
        }
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
                Quaternion rotation = Mappers.rotation.get(pc).rotation;
                Vector3 position = Mappers.position.get(pc).position;

                tweenManager.killTarget(position, Vector3Accessor.POSITION_XYZ);
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