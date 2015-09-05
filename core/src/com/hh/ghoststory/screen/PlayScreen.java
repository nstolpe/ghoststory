package com.hh.ghoststory.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.entity.EntityTypes;
import com.hh.ghoststory.entity.Mappers;
import com.hh.ghoststory.entity.components.*;
import com.hh.ghoststory.render.renderers.ShadowRenderer;
import com.hh.ghoststory.scene.Lighting;
import com.hh.ghoststory.scene.lights.core.PointCaster;
import com.hh.ghoststory.screen.input.PlayDetector;

/**
 * Created by nils on 7/14/15.
 * Screen for interaction with the game world. Not inventory, not save menus, not stats, just the gameworld.
 */
public class PlayScreen extends AbstractScreen {
    protected ShadowRenderer renderer = new ShadowRenderer(this);
    protected PerspectiveCamera perspective;
    protected OrthographicCamera orthographic;
    protected Camera active;
    public AssetManager assetManager = new AssetManager();
    public Lighting lighting = new Lighting();
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
    public PlayScreen(GhostStory game) {
        super(game);
        activateCamera(defaultPerspective());
        setInput();
        init();
        loading = true;
    }

    protected void init() {
        // get the scene. just model right now.
        scene = game.engine.getEntitiesFor(EntityTypes.SCENE).get(0);
        // add ambient if it's there.
        if (Mappers.ambient.has(scene))
            lighting.set(Mappers.ambient.get(scene).colorAttribute);

        assetManager.load("models/" + Mappers.geometry.get(scene).file, Model.class);
        // done scene.

        // pc
        pc = game.engine.getEntitiesFor(Family.all(PlayerComponent.class).get()).get(0);
        assetManager.load("models/" + Mappers.geometry.get(pc).file, Model.class);
        assetManager.load("models/ghost_texture_blue.png", Pixmap.class);
        // end pc

        // mobs
        mobs = game.engine.getEntitiesFor(EntityTypes.MOB);

        for (Entity mob : mobs)
            assetManager.load("models/" + Mappers.geometry.get(mob).file, Model.class);
        // end mobs

        //lights
        lights = game.engine.getEntitiesFor(EntityTypes.LIGHT);
        for (Entity light : lights) {
            PointCaster caster = new PointCaster(Mappers.color.get(light).color, Mappers.position.get(light).position, Mappers.intensity.get(light).intensity);
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
        playDetector = new PlayDetector(this);
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
        Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        instances.clear();

        // asset loading has just finished, loading hasn't been updated
        if (loading && assetManager.update()) {
            doneLoading();
            // asset loading is finished and post load hooks have completed
            // maybe move to an update function
        } else if (!loading){
            ImmutableArray<Entity> renderables = game.engine.getEntitiesFor(EntityTypes.RENDERABLE_INSTANCE);
            // This could go in an entity system.
            for (Entity renderable : renderables) {
                // something should have stopped earlier if instance wasn't set on the component.
                if (Mappers.instance.get(renderable).instance != null) {
                    Vector3 position = Mappers.position.get(renderable).position;
                    ModelInstance instance = Mappers.instance.get(renderable).instance;
                    instance.transform.setTranslation(position);

                    // update entities with animation components. Maybe a 2nd loop instead of the check?
                    if (Mappers.animation.has(renderable))
                        Mappers.animation.get(renderable).controller.update(delta);

                    instances.add(instance);
                }
            }

            messageDispatcher.update(delta);
        }

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
        ImmutableArray<Entity> renderables = game.engine.getEntitiesFor(EntityTypes.RENDERABLE);

        // retrieve ModelInstances from the assetManager and assign them to the renderable Entity.
        for (Entity renderable : renderables) {
            ModelInstance instance = new ModelInstance(assetManager.get("models/" + Mappers.geometry.get(renderable).file, Model.class));
            Mappers.instance.get(renderable).instance(instance);

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
        camera.position.set(10, 10, 10);
//		camera.direction.set(-1, -1, -1);
        camera.lookAt(10,0,10);
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
}