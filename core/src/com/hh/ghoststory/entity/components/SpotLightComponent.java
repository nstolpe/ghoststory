package com.hh.ghoststory.entity.components;

import com.badlogic.ashley.core.Component;
import com.hh.ghoststory.scene.lights.core.SpotCaster;

/**
 * Created by nils on 9/3/15.
 */
public class SpotLightComponent implements Component {
    public SpotCaster caster = new SpotCaster();
	public boolean lighting;
	public boolean shadowing;

    public SpotLightComponent() {}

    public SpotLightComponent(SpotCaster caster, boolean lighting, boolean shadowing) {
		this.caster = caster;
		this.lighting = lighting;
		this.shadowing = shadowing;
    }

    public SpotLightComponent caster(SpotCaster caster) {
        this.caster = caster;
        return this;
    }

	public SpotLightComponent lighting(boolean lighting) {
		this.lighting = lighting;
		return this;
	}

	public SpotLightComponent shadowing(boolean shadowing) {
		this.shadowing = shadowing;
		return this;
	}
}
