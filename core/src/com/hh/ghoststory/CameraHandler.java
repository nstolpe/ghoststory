package com.hh.ghoststory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.collision.Ray;
import com.hh.ghoststory.screens.GameScreen;

/**
 * Created by nils on 7/7/15.
 */
public class CameraHandler {
	public static final int PERSPECTIVE = 0;
	public static final int ORTHOGRAPHIC = 1;
	private GameScreen screen;

	private PerspectiveCamera pCamera;
	private OrthographicCamera oCamera;
	private int activeCameraType;

	public CameraHandler(GameScreen screen) {

	}

	public int getActiveCameraType() {
		return activeCameraType;
	}
	/*
	 * Made public right now because it's used by input manager. Should change.
	 * Used by the InputManager. This is probably weird.
	 */
	public Ray getPickRay(float x, float y) {
		return getActiveCamera().getPickRay(x, y);
	}

	public void setCameraViewport(int width, int height) {
		getActiveCamera().viewportWidth = width;
		getActiveCamera().viewportHeight= height;

		if (getActiveCamera() instanceof OrthographicCamera) {
			oCamera.setToOrtho(false, 20, 20 * ((float) height / (float) width));
			oCamera.position.set(100, 100, 100);
			oCamera.direction.set(-1, -1, -1);
			oCamera.near = 1;
			oCamera.far = 300;
		}
	}

	public void setUpDefaultCamera(int type) {
		switch (type) {
			case PERSPECTIVE:
				setUpPerspectiveCamera();
				setActiveCameraType(PERSPECTIVE);
				break;
			case ORTHOGRAPHIC:
				setUpOrthographicCamera();
				setActiveCameraType(ORTHOGRAPHIC);
				break;
			default:
				break;
		}
	}
//    public void setUpDefaultCamera(Camera camera) {
//        if (camera instanceof PerspectiveCamera) {
//
//        }
//        switch (type) {
//            case PERSP:
//                setUpPerspectiveCamera();
//                setActiveCameraType(PERSP);
//                break;
//            case ORTHO:
//                setUpOrthographicCamera();
//                setActiveCameraType(ORTHO);
//                break;
//            default:
//                break;
//        }
//    }

	public void zoomCamera(double amount) {
		if (getActiveCamera() instanceof PerspectiveCamera)
			zoomPerspective(amount);
		else if (getActiveCamera() instanceof OrthographicCamera)
			zoomOrthographic(amount);
		else
			return;
	}

	private void zoomPerspective(double amount) {
		//Zoom out
		if (amount > 0 && pCamera.fieldOfView < 67)
			pCamera.fieldOfView += 1f;
		//Zoom in
		if (amount < 0 && pCamera.fieldOfView > 1)
			pCamera.fieldOfView -= 1f;
	}
	private void zoomOrthographic(double amount) {
		//Zoom out
		if (amount > 0 && oCamera.zoom < 1)
			oCamera.zoom += 0.1f;
		//Zoom in
		if (amount < 0 && oCamera.zoom > 0.1)
			oCamera.zoom -= 0.1f;
	}
	/*
	 * Sets up the perspective camera for entire screen/window.
	 * @TODO parameterize more of this.
	 */
	public void setUpPerspectiveCamera() {
		setUpPerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	/*
	 * Sets up the perspective camera.
	 * @TODO parameterize more of this.
	 */
	public void setUpPerspectiveCamera(float fieldOfViewY, float viewportWidth, float viewportHeight) {
		pCamera = new PerspectiveCamera(fieldOfViewY, viewportWidth, viewportHeight);
		pCamera.position.set(10, 10, 10);
		pCamera.direction.set(-1, -1, -1);
		pCamera.near = 1;
	}
	/*
	 * @TODO: Make not suck
	 */
	public void setUpOrthographicCamera() {
		setUpOrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}
	/*
	 * @TODO: Make not suck
	 */
	public void setUpOrthographicCamera(float viewportWidth, float viewportHeight) {
		oCamera = new OrthographicCamera();
		oCamera.setToOrtho(false, 20, 20 * (viewportHeight / viewportWidth));
		oCamera.position.set(100, 100, 100);
		oCamera.direction.set(-1, -1, -1);
		oCamera.near = 1;
		oCamera.far = 300;
	}

	/*
	 * Sets the activeCameraType field. This camera will be used for all rendering.
	 */
	public void setActiveCameraType(int type) {
		this.activeCameraType = type;
	}
	/*
	 * Returns the active camera.
	 */
	public Camera getActiveCamera() {
		Camera camera = null;
		switch (activeCameraType) {
			case PERSPECTIVE:
				camera = getPerspectiveCamera();
				break;
			case ORTHOGRAPHIC:
				camera = getOrthograhcicCamera();
				break;
			default:
				break;
		}
		return camera;
	}

	public PerspectiveCamera getPerspectiveCamera() {
		return pCamera;
	}

	public OrthographicCamera getOrthograhcicCamera() {
		return oCamera;
	}
}
