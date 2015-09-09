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
import com.hh.ghoststory.lib.tween.accessors.ColorAccessor;
import com.hh.ghoststory.lib.tween.accessors.Vector3Accessor;
import com.hh.ghoststory.lib.utility.Util;

import java.util.Random;

/**
 * Created by nils on 9/7/15.
 */
public class BehaviorSystem extends EntitySystem {
    // this is wack for now. maybe the tweenManger can be in the
    // entity system. it will be interacting with them
    // more than anything else. It needs to be accessible w/in the callback.
    // or maybe od something w/ lambdas http://stackoverflow.com/questions/4480334/how-to-call-a-method-stored-in-a-hashmap-java
    // Entity tweenManager = new Entity().add(new TweenManagerComponent);
    // TweenManagerComponent implements Component {
    //     public TweenManager tweenManager = new TweenManager();
    // }
    private final TweenManager tweenManager;
    public BehaviorSystem(TweenManager tweenManager) {
        this.tweenManager = tweenManager;
    }

    @Override
    public void addedToEngine(Engine engine) {
        ImmutableArray<Entity> entities = getEngine().getEntitiesFor(Family.all(BehaviorComponent.class).get());

        for (final Entity entity: entities) {
			// Don't need bounce. Or probably any of the callback style. OR maybe this is bounce?
			// tween engine is still here thought.
            Timeline.createSequence()
				.push(
					Tween.to(Mappers.position.get(entity).position, Vector3Accessor.POSITION_XYZ, 5)
						.ease(TweenEquations.easeInOutCirc)
						.target(new float[]{0f, 5f, 20f})
				)
				.push(
					Tween.to(Mappers.position.get(entity).position, Vector3Accessor.POSITION_XYZ, 5)
						.ease(TweenEquations.easeInOutCirc)
						.target(new float[]{0f, 5f, -20f})
				)
				.repeat(Tween.INFINITY, 0)
				.start(tweenManager);

			Timeline.createSequence()
				.push(
					Tween.to(Mappers.color.get(entity).color, ColorAccessor.COLORS, 1)
						.target(Util.randomGenerator.nextFloat(),Util.randomGenerator.nextFloat(),Util.randomGenerator.nextFloat())
						.ease(TweenEquations.easeNone))
				.repeat(Tween.INFINITY, 0)
				.start(tweenManager);
        }
//		Random generator = new Random();
//		float red = generator.nextFloat();
//		float green = generator.nextFloat();
//		float blue = generator.nextFloat();
//			Timeline.createSequence()
//					.push(Tween.to(Mappers.color.get(entity).color, ColorAccessor.COLORS, 1)
//							.target(red,green,blue)
//							.ease(TweenEquations.easeNone))
//					.setCallback(new TweenCallback() {
//						@Override
//						public void onEvent(int type, BaseTween<?> source) {
//
//						}
//					})
//					.start(tweenManager);
    }

	public void random() {
		Random generator = new Random();
		float red = generator.nextFloat();
		float green = generator.nextFloat();
		float blue = generator.nextFloat();

	}
}