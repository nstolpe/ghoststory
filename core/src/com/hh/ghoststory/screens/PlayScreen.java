package com.hh.ghoststory.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.utility.ClassFunction;
import com.hh.ghoststory.utility.TestLoader;

/**
 * Created by nils on 7/14/15.
 */
public class PlayScreen extends AbstractScreen {
	private Camera camera;
	private PerspectiveCamera perspectiveCamera;
	private OrthographicCamera orthographicCamera;

	private AssetManager assetManager = new AssetManager();
	private ModelBatch modelBatch = new ModelBatch();
	public Array<ModelInstance> instances = new Array<ModelInstance>();
	private boolean loading;

	public PlayScreen(GhostStory game) {
		super(game);
		setUpDefaultCamera(new OrthographicCamera());
		// make the background purple so we know something is happening.
		setClear(0.7f, 0.1f, 1f, 1);
		assetManager.load("models/ghost.g3dj", Model.class);
		assetManager.load("models/tile.g3dj", Model.class);

		loading = true;
	}

	/**
	 * Called when the asset manager has finished updating. Make models here.
	 * @TODO Add more stuff that needs to happen after loading.
	 */
	public void doneLoading() {
		instances = TestLoader.getTestModels(assetManager);

		loading = false;
	}
	@Override
	public void show() {
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void render(float delta) {
		super.render(delta);

		if (loading && assetManager.update())
			doneLoading();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		orthographicCamera.update();
		modelBatch.begin(orthographicCamera);
		modelBatch.render(instances);
		modelBatch.end();
	}

	@Override
	public void hide() {
		super.hide();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	/** Camera Section */
	/**
	 * Returns the class name of the currently active camera.
	 * @return The string name of the PlayScreen.camera class.
	 */
	public Class getActiveCameraType() {
		return camera.getClass();
	}
	/**
	 * Sets up the default camera (either Perspective or Orthographic) for the screen. Uses
	 * reflection to pass the camera to the proper setup method based on its type.
	 * @param camera
	 */
	public void setUpDefaultCamera(Camera camera) {
		this.camera = camera;
		ClassFunction.call(this, "setUp" + camera.getClass().getSimpleName(), camera);
	}

	/**
	 * Sets up an OrthographicCamera.
	 * @param camera
	 */
	public void setUpOrthographicCamera(OrthographicCamera camera) {
		setUpOrthographicCamera(camera, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	/**
	 *
	 * @param camera
	 * @param viewportWidth
	 * @param viewportHeight
	 */
	public void setUpOrthographicCamera(OrthographicCamera camera, float viewportWidth, float viewportHeight) {
		camera.setToOrtho(false, 20, 20 * (viewportHeight / viewportWidth));
		camera.position.set(100, 100, 100);
//		camera.direction.set(-1, -1, -1);
		camera.lookAt(0,0,0);
		camera.near = 1;
		camera.far = 300;
		this.orthographicCamera = camera;
	}

	/**
	 *
	 * @param camera
	 */
	public void setUpPerspectiveCamera(PerspectiveCamera camera) {
		setUpPerspectiveCamera(camera, 67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	private void setUpPerspectiveCamera(PerspectiveCamera camera, float fieldOfViewY, float viewportWidth, float viewportHeight) {
		camera.fieldOfView = fieldOfViewY;
		camera.position.set(10, 10, 10);
		camera.direction.set(-1, -1, -1);
		camera.near = 1;
	}
	/** End Camera Section */
}
