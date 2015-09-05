package com.hh.ghoststory.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

/**
 * Created by nils on 9/3/15.
 */
public class ColorComponent implements Component {
    public Color color;

    public ColorComponent color(Color color) {
        this.color = color;
        return this;
    }
}
