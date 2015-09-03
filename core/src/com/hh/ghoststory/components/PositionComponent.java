package com.hh.ghoststory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by nils on 9/3/15.
 */
public class PositionComponent implements Component {
    public Vector3 position;

    public PositionComponent position(Vector3 position) {
        this.position = position;
        return this;
    }
}
