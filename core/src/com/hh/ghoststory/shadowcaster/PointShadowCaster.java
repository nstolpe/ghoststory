package com.hh.ghoststory.shadowcaster;

import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by nils on 7/21/15.
 */
public class PointShadowCaster extends AbstractShadowCaster {
    public PointLight light;
    /**
     * @param light
     */
    public PointShadowCaster(PointLight light) {
	    this.light = light;
	    this.position = light.position;
        setupCamera();
    }

    @Override
    public void setupCamera() {
        camera.fieldOfView = 90f;
        camera.viewportWidth = depthmapsize;
        camera.viewportHeight = depthmapsize;
        camera.near = 4f;
        camera.far = 70;
        camera.position.set(position);
        camera.update();
    }

	@Override
	public void setPosition(Vector3 position) {
		super.setPosition(position);
		light.setPosition(position);
		camera.position.set(position);
		camera.update();
	}
}
