package com.hh.ghoststory.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by nils on 9/3/15.
 */
public class LightTypeComponent implements Component {
    public static int DIRECTIONAL = 0;
    public static int SPOT = 1;
    public static int POINT = 2;
    public int type;

    public LightTypeComponent type(int type) {
        this.type = type;
        return this;
    }
}
