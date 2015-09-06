package com.hh.ghoststory.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;

/**
 * Created by nils on 9/3/15.
 */
public class InstanceComponent implements Component {
    public ModelInstance instance;

    public InstanceComponent() {}

    public InstanceComponent(ModelInstance instance) {
        instance(instance);
    }

    public InstanceComponent instance(ModelInstance instance) {
        this.instance = instance;
        return this;
    }
}
