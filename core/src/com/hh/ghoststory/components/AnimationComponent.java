package com.hh.ghoststory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by nils on 9/3/15.
 */
public class AnimationComponent implements Component {
    public ObjectMap<String, ObjectMap<String, Object>> animations = new ObjectMap();
    public AnimationController controller;

    public AnimationComponent animations(Array<ObjectMap<String, Object>> animations) {
        for (ObjectMap<String, Object> animation : animations)
            this.animations.put((String) animation.get("id"), animation);
        return this;
    }
    public AnimationComponent controller(AnimationController controller) {
        this.controller = controller;
        return this;
    }

    public void init(ModelInstance instance) {
        controller = new AnimationController(instance);
    }
}
