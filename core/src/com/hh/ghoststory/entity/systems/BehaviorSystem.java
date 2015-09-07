package com.hh.ghoststory.entity.systems;

import aurelienribon.tweenengine.*;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector3;
import com.hh.ghoststory.entity.Mappers;
import com.hh.ghoststory.entity.components.BehaviorComponent;
import com.hh.ghoststory.entity.components.PlayerComponent;
import com.hh.ghoststory.lib.tween.Timelines;
import com.hh.ghoststory.lib.tween.accessors.Vector3Accessor;

/**
 * Created by nils on 9/7/15.
 */
public class BehaviorSystem extends EntitySystem {
    // this is wack for now. maybe the tweenManger can be in the
    // entity system. it will be interacting with them
    // more than anything else.
    private final TweenManager tweenManager;
    public BehaviorSystem(TweenManager tweenManager) {
        this.tweenManager = tweenManager;
    }

    @Override
    public void addedToEngine(Engine engine) {
        ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(BehaviorComponent.class).get());

        for (final Entity entity: entities) {
            final Timeline bounce = Timelines.bounce(Mappers.position.get(entity).position, Vector3Accessor.POSITION_XYZ, 5, TweenEquations.easeInSine, new float[]{0f, 5f, -20f}, new float[]{0f, 5f, 20f});

            Tween.call(new TweenCallback() {
                @Override
                public void onEvent(int type, BaseTween<?> source) {
                    Timelines.bounce(Mappers.position.get(entity).position, Vector3Accessor.POSITION_XYZ, 5, TweenEquations.easeInSine, new float[]{0f, 5f, -20f}, new float[]{0f, 5f, 20f}).setCallback(this).start(tweenManager);
                }
            }).start(tweenManager);
        }
    }
}
