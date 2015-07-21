package com.hh.ghoststory.shadowcaster;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by nils on 7/20/15.
 */
public abstract class AbstractShadowCaster {
	protected BaseLight light;
	protected Camera camera;
	protected Vector3 position;
	protected int depthmapsize = 1024;

	/**
	 *
	 * @param light
	 * @param position
	 */
	public AbstractShadowCaster(BaseLight light, Vector3 position) {
		this.light = light;
		this.position = position;
	}

	/**
	 *
	 * @param light
	 * @param position
	 * @param depthmapsize
	 */
	public AbstractShadowCaster(BaseLight light, Vector3 position, int depthmapsize) {
		this(light, position);
		this.depthmapsize = depthmapsize;
	}
}
