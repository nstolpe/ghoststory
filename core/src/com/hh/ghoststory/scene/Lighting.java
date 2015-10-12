package com.hh.ghoststory.scene;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.scene.lights.core.Caster;
import com.hh.ghoststory.scene.lights.core.PointCaster;

/**
 * Created by nils on 8/24/15.
 */
public class Lighting extends Environment {
	// array if shadowcasters. see if these can be implemented as Attributes later,
	// like the lights.
	public Array<Caster> casters = new Array<Caster>();
//	public

	/*
     * Adds a Caster to casters. See note above it's declaration.
     */
	public Environment add(Caster light) {
		if (light instanceof PointLight)
			add((PointLight) light);
		if (light instanceof SpotLight)
			add((SpotLight) light);
		if (light instanceof DirectionalLight)
			add((DirectionalLight) light);

		casters.add(light);
		return this;
	}
	public Environment add(PointCaster light) {
		add((PointLight) light);
		casters.add(light);
		return this;
	}
	/*
     * Removes a ShadowCaster from casters. See note above it's declaration.
     */
	public Environment remove(Caster caster) {
		casters.removeValue(caster, true);
		remove(caster);
		return this;
	}
}
