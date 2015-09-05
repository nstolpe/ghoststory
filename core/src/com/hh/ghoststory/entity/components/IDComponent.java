package com.hh.ghoststory.entity.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by nils on 9/3/15.
 */
public class IDComponent implements Component {
    public String id;

    public IDComponent() {}

    public IDComponent(String id) {
        this.id = id;
    }

    public IDComponent id(String id) {
        this.id = id;
        return this;
    }
}
