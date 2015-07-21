package com.hh.ghoststory.shadowcaster;

import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;

/**
 * Created by nils on 7/21/15.
 */
public class PointShadowCaster extends AbstractShadowCaster {
    /**
     * @param light
     */
    public PointShadowCaster(PointLight light) {
        super(light);
    }
}
