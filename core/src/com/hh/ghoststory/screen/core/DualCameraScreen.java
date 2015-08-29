package com.hh.ghoststory.screen.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.lib.utility.ClassFunction;
import com.hh.ghoststory.scene.Lighting;

/**
 * Created by nils on 7/17/15.
 */
public abstract class DualCameraScreen extends AbstractScreen {
	protected PerspectiveCamera perspective;
	protected OrthographicCamera orthographic;
	public Camera active;
	protected AssetManager assetManager = new AssetManager();
	public Array<ModelInstance> instances = new Array<ModelInstance>();
    protected boolean loading;
	public Environment environment = new Lighting();

	CameraInputController camController;

	public DualCameraScreen(GhostStory game) {
		this(game, new PerspectiveCamera());
	}

	public DualCameraScreen(GhostStory game, Camera camera) {
		super(game);
		activateCamera(camera);
	}

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
	/**
	 * Sets up the default camera (either Perspective or Orthographic) for the screen. Uses
	 * reflection to pass the camera to the proper setup method based on its type.
	 *
	 * @param camera
	 */
	public void activateCamera(Camera camera) {
		if (camera instanceof PerspectiveCamera)
			perspective = (PerspectiveCamera) camera;
		else if (camera instanceof OrthographicCamera)
			orthographic = (OrthographicCamera) camera;
		else
			return;

		active = camera;

		camController = new CameraInputController(this.active);
		Gdx.input.setInputProcessor(camController);
	}

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