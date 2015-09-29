package com.hh.ghoststory.entity.systems;

import aurelienribon.tweenengine.*;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.hh.ghoststory.entity.Mappers;
import com.hh.ghoststory.entity.components.BehaviorComponent;
import com.hh.ghoststory.lib.tween.Tweens;
import com.hh.ghoststory.lib.tween.accessors.ColorAccessor;
import com.hh.ghoststory.lib.tween.accessors.Vector3Accessor;
import com.hh.ghoststory.lib.utility.Util;

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

		for (int i = 0; i < entities.size(); i++) {
			switch (i) {
				case(0):
					bounce(entities.get(i));
					break;
				case(1):
					colorCycle(entities.get(i));
					followPath(entities.get(i));
					break;
				case(2):
					break;
				default:
					break;

			}
		}

	}
	public void bounce(Entity entity) {
		// Don't need bounce. Or probably any of the callback style. OR maybe this is bounce?
		// tween engine is still here thought.
		Timeline.createSequence()
			.push(
					Tween.to(Mappers.position.get(entity).position, Vector3Accessor.XYZ, 5)
							.ease(TweenEquations.easeInOutCubic)
							.target(new float[]{ 0f, 6f, 20f })
			)
			.push(
					Tween.to(Mappers.position.get(entity).position, Vector3Accessor.XYZ, 5)
							.ease(TweenEquations.easeInOutCubic)
							.target(new float[]{ 0f, 6f, -20f })
			)
			.repeat(Tween.INFINITY, 0)
			.start(tweenManager);
	}
    public void colorCycle(final Entity entity) {
		TweenCallback callback = new TweenCallback() {
			@Override
			public void onEvent(int type, BaseTween<?> source) {
				Timeline.createSequence()
					.push(
						Tween.to(Mappers.color.get(entity).color, ColorAccessor.COLORS, 1)
							.target(Util.randomGenerator.nextFloat(), Util.randomGenerator.nextFloat(), Util.randomGenerator.nextFloat())
							.ease(TweenEquations.easeNone)
					)
					.setCallback(this)
					.start(tweenManager);
			}
		};
		Tween.call(callback).start(tweenManager);
    }

	public void followPath(final Entity entity) {
		Timeline.createSequence()
			.push(
				Tween.to(Mappers.position.get(entity).position, Vector3Accessor.XYZ, 6)
					.ease(TweenEquations.easeNone)
					.target(new float[]{-20f, 10f, 20f})
			)
			.push(
				Tween.to(Mappers.direction.get(entity).direction, Vector3Accessor.XYZ, 3)
					.ease(TweenEquations.easeNone)
					.target(new float[]{0.8f, -1f, 0f})
			)
			.push(
				Tween.to(Mappers.position.get(entity).position, Vector3Accessor.XYZ, 6)
					.ease(TweenEquations.easeNone)
					.target(new float[]{-20f, 10f, -20f})
			)
			.push(
				Tween.to(Mappers.direction.get(entity).direction, Vector3Accessor.XYZ, 3)
					.ease(TweenEquations.easeNone)
					.target(new float[]{0f, -1f, 0.8f})
			)
			.push(
				Tween.to(Mappers.position.get(entity).position, Vector3Accessor.XYZ, 6)
					.ease(TweenEquations.easeNone)
					.target(new float[]{20f, 10f, -20f})
			)
			.push(
				Tween.to(Mappers.direction.get(entity).direction, Vector3Accessor.XYZ, 3)
					.ease(TweenEquations.easeNone)
					.target(new float[]{-0.8f, -1f, 0f})
			)
			.push(
				Tween.to(Mappers.position.get(entity).position, Vector3Accessor.XYZ, 6)
					.ease(TweenEquations.easeNone)
					.target(new float[]{20f, 10f, 20f})
			)
			.push(
				Tween.to(Mappers.direction.get(entity).direction, Vector3Accessor.XYZ, 3)
					.ease(TweenEquations.easeNone)
					.target(new float[]{0f, -1f, -0.8f})
			)
			.repeat(Tween.INFINITY, 0)
			.start(tweenManager);
	}
}