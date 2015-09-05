package com.hh.ghoststory.entity.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by nils on 9/3/15.
 */
public class IntensityComponent implements Component {
    public float intensity;

    public IntensityComponent intensity(float intensity) {
        this.intensity = intensity;
        return this;
    }
}
