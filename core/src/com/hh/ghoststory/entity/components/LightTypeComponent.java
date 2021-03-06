package com.hh.ghoststory.entity.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by nils on 9/3/15.
 */
public class LightTypeComponent implements Component {
    public static int DIRECTIONAL = 0;
    public static int SPOT = 1;
    public static int POINT = 2;
    public int type;

    public LightTypeComponent() {}

    public LightTypeComponent(int type) {
        this.type = type;
    }
    public LightTypeComponent type(int type) {
        this.type = type;
        return this;
    }
}
