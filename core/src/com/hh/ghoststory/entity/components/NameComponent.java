package com.hh.ghoststory.entity.components;

import com.badlogic.ashley.core.Component;

/**
 * Created by nils on 9/3/15.
 */
public class NameComponent implements Component {
    public String name;

    public NameComponent name(String name) {
        this.name = name;
        return this;
    }
}
