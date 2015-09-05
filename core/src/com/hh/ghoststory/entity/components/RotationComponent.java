package com.hh.ghoststory.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by nils on 9/5/15.
 */
public class RotationComponent implements Component {
    public Quaternion rotation;

    public RotationComponent() {}

    public RotationComponent(Quaternion rotation) {
        this.rotation = rotation;
    }

    public RotationComponent position(Quaternion rotation) {
        this.rotation = rotation;
        return this;
    }
}
