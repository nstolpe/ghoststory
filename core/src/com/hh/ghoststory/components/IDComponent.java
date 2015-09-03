package com.hh.ghoststory.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by nils on 9/3/15.
 */
public class IDComponent implements Component {
    public String id;

    public IDComponent id(String id) {
        this.id = id;
        return this;
    }
}
