package com.hh.ghoststory.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by nils on 9/3/15.
 */
public class BehaviorComponent implements Component {
    public ObjectMap<String, ObjectMap<String, Object>> behaviors = new ObjectMap();

    public static int BOUNCE = 0;

    public BehaviorComponent() {}

	public BehaviorComponent(Array<ObjectMap<String, Object>> behaviors) {
        for (ObjectMap<String, Object> behavior : behaviors)
            this.behaviors.put((String) behavior.get("id"), behavior);
    }

	public BehaviorComponent behaviors(Array<ObjectMap<String, Object>> behaviors) {
        for (ObjectMap<String, Object> behavior: behaviors)
            this.behaviors.put((String) behavior.get("id"), behavior);
        return this;
    }
}