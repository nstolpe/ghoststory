package com.hh.ghoststory.scene;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.scene.lights.PointShadowCaster;
import com.hh.ghoststory.scene.lights.core.ShadowCaster;

/**
 * Created by nils on 8/24/15.
 */
public class Lighting extends Environment {
	// array if shadowcasters. see if these can be implemented as Attributes later,
	// like the lights.
	public Array<ShadowCaster> shadowCasters = new Array<ShadowCaster>();

	/*
     * Adds a ShadowCaster to casters. See note above it's declaration.
     */
	public Environment add (ShadowCaster shadowCaster) {
		if (shadowCaster instanceof PointShadowCaster) {

		}
		shadowCasters.add(shadowCaster);
		add(shadowCaster.light);
		return this;
	}

	/*
     * Removes a ShadowCaster from casters. See note above it's declaration.
     */
	public Environment remove(ShadowCaster shadowCaster) {
		shadowCasters.removeValue(shadowCaster, true);
		remove(shadowCaster.light);
		return this;
	}
}
