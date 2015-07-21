package com.hh.ghoststory.shadowcaster;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by nils on 7/20/15.
 */
public abstract class AbstractShadowCaster {
	public PerspectiveCamera camera = new PerspectiveCamera();
	public Vector3 position = new Vector3();
	public int depthmapsize = 1024;
	public boolean casting = true;

	public void setupCamera() {}

	public void setPosition(Vector3 position) {
		this.position = position;
	}
}