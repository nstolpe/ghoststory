package com.hh.ghoststory.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by nils on 9/3/15.
 */
public class GeometryComponent implements Component {
    public String file;

    public GeometryComponent file(String file) {
        this.file = file;
        return this;
    }
}
