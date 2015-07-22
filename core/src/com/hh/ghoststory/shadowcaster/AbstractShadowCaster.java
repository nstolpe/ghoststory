package com.hh.ghoststory.shadowcaster;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by nils on 7/20/15.
 * @TODO move some methods to interface, make this implement the interface.
 */
public abstract class AbstractShadowCaster {
	public PerspectiveCamera camera = new PerspectiveCamera();
	public Vector3 position = new Vector3();
	public int depthmapsize = 1024;
	public boolean casting = true;
	protected ModelBatch modelBatch ;
//	public Array<ModelInstance> instances = new Array<ModelInstance>();

	public abstract void setupCamera();

	public abstract void render(Array<ModelInstance> instances, Environment environment);

	public void setPosition(Vector3 position) {
		this.position = position;
	}
}