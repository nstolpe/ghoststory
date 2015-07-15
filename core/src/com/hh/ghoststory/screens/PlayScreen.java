package com.hh.ghoststory.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.utility.ClassFunction;

/**
 * Created by nils on 7/14/15.
 */
public class PlayScreen extends AbstractScreen {
	private PerspectiveCamera perspectiveCamera;
	private OrthographicCamera orthographicCamera;
	private final Class[] cameraTypes = { OrthographicCamera.class, PerspectiveCamera.class };
	private Class cameraType;

	public PlayScreen(GhostStory game) {
		super(game);
		setUpDefaultCamera(new OrthographicCamera());
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
	/*
	 * Camera Section
	 */

	/**
	 * Sets up the default camera (either Perspective or Orthographic) for the screen. Uses
	 * reflection to pass the camera to the proper setup method based on its type.
	 * @param camera
	 */
	public void setUpDefaultCamera(Camera camera) {
		ClassFunction.call(this, "setUp" + camera.getClass().getSimpleName(), camera);
	}

	/**
	 * Sets up an OrthographicCamera.
	 * @param camera
	 */
	public void setUpOrthographicCamera(OrthographicCamera camera) {
		System.out.println("Ortho setup");
		cameraType = OrthographicCamera.class;
		setUpOrthographicCamera(camera, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	public void setUpOrthographicCamera(OrthographicCamera camera, float viewportWidth, float viewportHeight) {
		camera.setToOrtho(false, 20, 20 * (viewportHeight / viewportWidth));
		camera.position.set(100, 100, 100);
		camera.direction.set(-1, -1, -1);
		camera.near = 1;
		camera.far = 300;
		this.orthographicCamera = camera;
	}
	public void setUpPerspectiveCamera(PerspectiveCamera camera) {
		System.out.println("Perspective setup");
		setUpPerspectiveCamera(camera, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	private void setUpPerspectiveCamera(PerspectiveCamera perspective, int viewportWidth, int viewportHeight) {
	}
	/*
	 * End Camera Section
	 */
}
