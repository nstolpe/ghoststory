package com.hh.ghoststory;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.hh.ghoststory.screen.GameScreen;
import com.hh.ghoststory.lib.tween.accessors.QuaternionAccessor;
import com.hh.ghoststory.lib.tween.accessors.Vector3Accessor;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by nils on 7/8/15.
 */
public class TweenHandler {
	public TweenManager tweenManager = new TweenManager();

	private GameScreen screen;
	// move these callbacks into their own classes. Make them reusable.
	// lightCallback bounces a light between two positions, using itself as a callback to get the bounce.
	private final TweenCallback lightCallback = new TweenCallback(){
		@Override
		public void onEvent(int i, BaseTween<?> baseTween) {
			Timeline.createSequence()
					.push(Tween.to(screen.colorSwitchLight.position, Vector3Accessor.POSITION_Z, 1)
							.target(10)
							.ease(TweenEquations.easeInSine))
					.push(Tween.to(screen.colorSwitchLight.position, Vector3Accessor.POSITION_Z, 1)
							.target(0)
							.ease(TweenEquations.easeInSine))
					.setCallback(this)
					.start(tweenManager);
		}
	};
	// colorCallback cycles through random colors, using itself as a callback to keep cycling.
	private final TweenCallback colorCallback = new TweenCallback(){
		@Override
		public void onEvent(int i, BaseTween<?> baseTween) {
			Random generator = new Random();
			float red = generator.nextFloat();
			float green = generator.nextFloat();
			float blue = generator.nextFloat();
//			Timeline.createSequence()
//					.push(Tween.to(screen.travellingLight.color, ColorAccessor.COLORS, 1)
//							.target(red,green,blue)
//							.ease(TweenEquations.easeNone))
//					.setCallback(this)
//					.start(tweenManager);
		}
	};

	public TweenHandler(GameScreen screen) {
		this.screen = screen;
	}
	/*
	 * This constructor allows the updating of Tween.combinedAttrsLimit to accommodate
	 * attributes other than 3 (color uses 4 for RGBA).
	 */
	public TweenHandler(GameScreen screen, int attrLimit) {
		this.screen = screen;
		Tween.setCombinedAttributesLimit(attrLimit);
	}
	/*
	 * This constructor allows the setting of tweenaccessors on create.
	 */
	public TweenHandler(GameScreen screen, HashMap<Class, TweenAccessor> tweenAccessors) {
		this.screen = screen;
		registerAccessors(tweenAccessors);
	}
	/*
	 * This constructor allows the updating of Tween.combinedAttrsLimit to accommodate
	 * attributes other than 3 (color uses 4 for RGBA) and the setting of tweenAccessors.
	 */
	public TweenHandler(GameScreen screen, int attrLimit, HashMap<Class, TweenAccessor> tweenAccessors) {
		this.screen = screen;
		Tween.setCombinedAttributesLimit(attrLimit);
		registerAccessors(tweenAccessors);
	}

	public void update() {
		tweenManager.update(Gdx.graphics.getDeltaTime());
	}

	public void startCallbacks() {
		Tween.call(lightCallback).start(tweenManager);
		Tween.call(colorCallback).start(tweenManager);
	}

	public void registerAccessors(HashMap<Class, TweenAccessor> tweenAccessors) {
		for (Map.Entry<Class, TweenAccessor> accessor : tweenAccessors.entrySet()) {
			Tween.registerAccessor(accessor.getKey(), accessor.getValue());
		}
	}

	/*
	 * Creates a tween timeline that will rotate to face a point and then move to it.
	 * It needs a better name.
	 *
	 * @TODO Rename this and make it more abstract. Pass in an array of sequential Tweens and push each to the timeline.
	 *
	 * @param Quaternion currentRotation  The current facing rotation of the object being tweened.
	 * @param Quaternion targetRotation   The rotation the object should tween towards.
	 * @param Vector3    currentPosition  The current translation of the object being tweened.
	 * @param Vector3    targetPosition   The translation the object should tween toward.
	 * @param float      rd               The duration that the object's rotation should take to complete.
	 * @param float      td               The duration that the object's translation should take to complete.
	 */
	public void tweenFaceAndMoveTo(Quaternion currentRotation, Quaternion targetRotation, Vector3 currentPosition, Vector3 targetPosition, float rd, float td) {
		Tween rotate = Tween.to(currentRotation, QuaternionAccessor.ROTATION, rd)
				.target(targetRotation.x, targetRotation.y, targetRotation.z, targetRotation.w)
				.ease(TweenEquations.easeNone);

		Tween translate = Tween.to(currentPosition, Vector3Accessor.POSITION_XYZ, td)
				.target(targetPosition.x, targetPosition.y, targetPosition.z)
				.ease(TweenEquations.easeNone);

		Timeline.createSequence()
			.push(rotate)
			.push(translate)
			.start(tweenManager);
	}

	public Tween buildTween(Object current, float[] targetValues, int type, float duration, TweenEquation easing) {
		return Tween
				.to(current, type, duration)
				.target(targetValues)
				.ease(easing);
	}

	public void runSequence(Tween[] tweens) {
		Timeline timeline = Timeline.createSequence();

		for (int i =0; i < tweens.length; i++)  timeline.push(tweens[i]);

		timeline.start(tweenManager);
	}
}
