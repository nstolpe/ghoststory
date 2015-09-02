package com.hh.ghoststory.screen.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.scene.Lighting;
import com.hh.ghoststory.screen.input.PlayDetector;

/**
 * Created by nils on 7/17/15.
 */
public abstract class DualCameraScreen extends AbstractScreen {
	protected PerspectiveCamera perspective;
	protected OrthographicCamera orthographic;
	protected Camera active;
	protected AssetManager assetManager = new AssetManager();
	public Array<ModelInstance> instances = new Array<ModelInstance>();
    protected boolean loading;
	public Environment environment = new Lighting();

	public enum CameraTypes { P, O }


	public PlayDetector playDetector;

	/**
	 * Creates the Screen with a default camera.
	 * @param game
	 */
	public DualCameraScreen(GhostStory game) {
		super(game);
		activateCamera(defaultPerspective());
		setInput();
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

	public DualCameraScreen(GhostStory game, Camera camera) {
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
	}

	/**
	 * Called when the asset manager has finished updating. Make models here.
	 * @TODO Add more stuff that needs to happen after loading.
	 */
	public void doneLoading() {
		loading = false;
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
}