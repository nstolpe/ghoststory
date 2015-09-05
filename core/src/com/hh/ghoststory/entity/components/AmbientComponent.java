package com.hh.ghoststory.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

/**
 * Created by nils on 9/3/15.
 */
public class AmbientComponent implements Component {
    public ColorAttribute colorAttribute;

    public AmbientComponent colorAttribute(ColorAttribute colorAttribute) {
        this.colorAttribute = colorAttribute;
        return this;
    }
}
