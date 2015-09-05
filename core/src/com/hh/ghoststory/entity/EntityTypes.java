package com.hh.ghoststory.entity;

import com.badlogic.ashley.core.Family;
import com.hh.ghoststory.entity.components.*;

/**
 * Created by nils on 9/5/15.
 */
public class EntityTypes {
    public static final Family RENDERABLE_INSTANCE = Family.all(GeometryComponent.class, RenderComponent.class, PositionComponent.class, InstanceComponent.class).get();
    public static final Family RENDERABLE = Family.all(GeometryComponent.class, RenderComponent.class, PositionComponent.class).get();
    public static final Family SCENE = Family.all(SceneComponent.class).get();
    public static final Family MOB = Family.all(GeometryComponent.class, PositionComponent.class, MobComponent.class).get();
    public static final Family LIGHT = Family.all(LightTypeComponent.class, PositionComponent.class).get();
}
