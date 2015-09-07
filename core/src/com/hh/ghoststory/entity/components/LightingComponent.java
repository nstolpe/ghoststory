package com.hh.ghoststory.entity.components;

import com.badlogic.ashley.core.Component;
import com.hh.ghoststory.scene.lights.core.Caster;
import com.hh.ghoststory.scene.lights.core.PointCaster;

/**
 * Created by nils on 9/3/15.
 */
public class LightingComponent implements Component {
    public PointCaster caster = new PointCaster();

    public LightingComponent() {}

    public LightingComponent(PointCaster caster) {
        this.caster = caster;
    }

    public LightingComponent caster(PointCaster caster) {
        this.caster = caster;
        return this;
    }
}
