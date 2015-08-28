package com.hh.ghoststory.screen.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.collision.Ray;
import com.hh.ghoststory.screen.core.AbstractScreen;

/**
 * Created by nils on 8/27/15.
 */
public class CameraController implements Telegraph {
	private AbstractScreen screen;
	private PerspectiveCamera perspective;
	private OrthographicCamera orthographic;
	private Camera active;
	private MessageDispatcher messageDispatcher;

	/**
	 * Probably the best constructor to use.
	 * @param screen
	 * @param camera
	 */
	public CameraController(AbstractScreen screen, Camera camera, MessageDispatcher messageDispatcher) {
		this.screen = screen;
		this.messageDispatcher = messageDispatcher;
		setActiveCamera(camera);
	}

	/**
	 * Sets the screen and creates a new default camera (perspecive)
	 * @param screen
	 */
	public CameraController(AbstractScreen screen) {
		this(screen, new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()), screen.messageDispatcher);
	}

	/**
	 * Sets the active camera and camera of type to the camera passed in.
	 * @param camera
	 */
	public void setActiveCamera(Camera camera) {
		if (camera instanceof PerspectiveCamera)
			perspective = (PerspectiveCamera) camera;
		else if (camera instanceof OrthographicCamera)
			orthographic = (OrthographicCamera) camera;
		else
			return;

		active = camera;
	}

	/**
	 * Sets `perspective`
	 * @param camera
	 */
	public void setPerspective(PerspectiveCamera camera) {
		perspective = camera;
	}

	/**
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
	 * Activates a camera
	 * @param camera
	 */
	public void activate(Camera camera) {
		if (camera instanceof PerspectiveCamera)
			activatePerspective((PerspectiveCamera) camera);
		else if (camera instanceof OrthographicCamera)
			activateOrthographic((OrthographicCamera) camera);
		else
			return;

		active = camera;
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
		return  new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	/**
	 * Returns a default OrthographicCamera
	 * @return
	 * @TODO fix this some, config options might not be necessary.
	 */
	private OrthographicCamera defaultOrthographic() {
		OrthographicCamera camera = new OrthographicCamera();
//		camera.setToOrtho(false, 20, 20 * (Gdx.graphics.getWidth() / Gdx.graphics.getHeight()));
//		camera.near = 1;
//		camera.far = 300;
		return camera;
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}
}