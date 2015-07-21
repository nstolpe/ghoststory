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
	public int depthmapsize = 1024;
	public boolean casting = true;

	public AbstractShadowCaster() {
		this.light = new BaseLight() {
			@Override
			public BaseLight setColor(float r, float g, float b, float a) {
				return super.setColor(r, g, b, a);
			}
		};
	}
	/**
	 *
	 * @param light
	 */
	public AbstractShadowCaster(BaseLight light) {
		this.light = light;
	}

	/**
	 * Sets the light.
	 * @param light
	 */
	public void set(BaseLight light) {
		this.light = light;
	}
}