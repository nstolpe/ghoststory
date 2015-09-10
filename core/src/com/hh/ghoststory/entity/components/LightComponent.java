package com.hh.ghoststory.entity.components;

import com.badlogic.ashley.core.Component;
import com.hh.ghoststory.scene.lights.core.PointCaster;

/**
 * Created by nils on 9/3/15.
 */
public class LightComponent implements Component {
    public PointCaster caster = new PointCaster();
	public boolean lighting;
	public boolean shadowing;

    public LightComponent() {}

    public LightComponent(PointCaster caster, boolean lighting, boolean shadowing) {
		this.caster = caster;
		this.lighting = lighting;
		this.shadowing = shadowing;
    }

    public LightComponent caster(PointCaster caster) {
        this.caster = caster;
        return this;
    }

	public LightComponent lighting(boolean lighting) {
		this.lighting = lighting;
		return this;
	}

	public LightComponent shadowing(boolean shadowing) {
		this.shadowing = shadowing;
		return this;
	}
}
