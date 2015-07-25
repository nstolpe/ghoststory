package com.hh.ghoststory.Renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by nils on 1/24/15.
 */
public abstract class AbstractRenderer implements Renderer, Disposable {
//	private Array<Camera> cameras;
	private Environment environment;
	protected float CLEAR_R = 0f;
	protected float CLEAR_G = 0f;
	protected float CLEAR_B = 0f;
	protected float CLEAR_A = 1f;

	public void render() {
		Gdx.gl.glClearColor(this.CLEAR_R, this.CLEAR_G, this.CLEAR_B, this.CLEAR_A);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//		for (Camera camera : cameras) {
//
//		}
	}

//	public void setCameras(Array<Camera> cameras) {
//		this.cameras = cameras;
//	}
//
//	public void addCamera(Camera camera) {
//		this.cameras.add(camera);
//	}
//
//	public void dropCamera(Camera camera) {
//		this.cameras.removeValue(camera, true);
//	}
//
//	public void dropCamera(int index) {
//		this.cameras.removeIndex(index);
//	}
//
//	public void addCameras(Array<Camera> cameras) {
//		this.cameras.addAll(cameras);
//	}
//
//	public void dropCameras(Array<Camera> cameras) {
//		this.cameras.removeAll(cameras, true);
//	}
//
//	public Array<Camera> getCameras() {
//		return this.cameras;
//	}
//
//	public Camera getCamera(int index) {
//		return cameras.get(index);
//	}
}
