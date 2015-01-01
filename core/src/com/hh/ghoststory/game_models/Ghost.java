package com.hh.ghoststory.game_models;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.hh.ghoststory.game_models.core.DynamicModel;

import java.util.HashMap;

/*
 * The ghost. Also shouldn't stay here forever.
 */
public class Ghost extends DynamicModel {
	private float targetRotation = 45;
	public float rotation = 45;
	private Vector3 targetPosition = new Vector3();
	private Vector3 startPosition = new Vector3();
	// y direction vector to rotate around.
	private int rotationDirection;
	private static final int NONE = 0;
	private static final int CLOCKWISE = 1;
	private static final int COUNTER_CLOCKWISE = 2;
	private float speed = 2;
	private String texture;

	public Ghost() {
		model_resource = "models/ghost.g3dj";
		setPosition(new Vector3(0, 0, 0));
		rotationDirection = NONE;
		startPosition.set(position);
	}

	/*
	 * Sets position and target position. Use for teleport, setting original position.
	 */
	public void setPosition(Vector3 position) {
		this.position.set(position);
		this.setTargetPosition(position);
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
		this.targetRotation = rotation;
	}

	/*
	 * Returns a copy so nothing happens to the position vector.
	 */
	public Vector3 getPosition() {
		return position.cpy();
	}

	public void update() {
//		setRotation();
		setTranslation();
//		model.transform.setToTranslation(position);
//		model.transform.rotate(verticalAxis, rotation);
	}

	@Override
	public void setTranslation() {
		// Only move if a targetPosition hasn't been reached or exceeded and it's not rotating.
		if (!position.epsilonEquals(targetPosition, 0f) && rotationDirection == NONE) {
//			if (position.dst(startPosition) >= targetPosition.dst(startPosition)) {
//				position.set(targetPosition);
//			} else {
				position.lerp(targetPosition, Gdx.graphics.getDeltaTime() * (speed / targetPosition.dst(position)));
//			}
		}
	}

	@Override
	public void setRotation() {
		if (rotation != targetRotation) {
			if (rotationDirection == NONE) {
				setRotationDirection();
			} else if (rotationDirection == CLOCKWISE) {
				rotateClockwise();
			} else if (rotationDirection == COUNTER_CLOCKWISE) {
				rotateCounterClockwise();
			}
		}
	}

	/*
	 * Advances the rotation towards targetRotation. Performs a check to see if the new rotation will have passed target
	 * rotation and sets targetRotation to rotation while also resetting rotationDirection if it would pass.
	 */
	private void rotateClockwise() {
		rotation += Gdx.graphics.getDeltaTime() * 100.0f;
		if (rotation >= targetRotation) {
			rotation = targetRotation > 360 ? targetRotation - 360 : targetRotation;
			targetRotation = rotation;
			rotationDirection = NONE;
		}
	}

	/*
	 * Advances the rotation towards targetRotation. Performs a check to see if the new rotation will have passed target
	 * rotation and sets targetRotation to rotation while also resetting rotationDirection if it would pass.
	 */
	private void rotateCounterClockwise() {
		rotation -= Gdx.graphics.getDeltaTime() * 100.0f;
		if (rotation <= targetRotation) {
			rotation = targetRotation < 0 ? targetRotation + 360 : targetRotation;
			targetRotation = rotation;
			rotationDirection = NONE;
		}
	}

	/*
	 * Determines whether the target rotation can be reached most quickly by turning clockwise or counterclockwise and
	 * sets the direction to it. Best explained with a graphic.
	 */
	private void setRotationDirection() {
		if (targetRotation > rotation && targetRotation - rotation < 180) {
			rotationDirection = CLOCKWISE;
		} else if (targetRotation < rotation && rotation - targetRotation < 180) {
			rotationDirection = COUNTER_CLOCKWISE;
		} else if (targetRotation > rotation && targetRotation - rotation >= 180) {
			rotation += 360;
			rotationDirection = COUNTER_CLOCKWISE;
		} else if (targetRotation < rotation && rotation - targetRotation >= 180) {
			targetRotation += 360;
			rotationDirection = CLOCKWISE;
		}
	}

	public void setTargetPosition(Vector3 point) {
		targetPosition.set(point);
	}

	public void setTargetPosition(float x, float y, float z) {
		targetPosition.set(x, y, z);
	}

	public Vector3 getTargetposition() {
		return this.targetPosition;
	}
	public void setStartPosition(Vector3 point) {
		startPosition.set(point);
	}

	public void setStartPosition(float x, float y, float z) {
		startPosition.set(x, y, z);
	}

	public void setTargetRotation(float angle) {
		resetRotation();
		this.targetRotation = angle;
	}

	private void resetRotation() {
		if (rotation > 360) rotation -= 360;
		rotationDirection = NONE;
	}

	@Override
	public void setModelResource(AssetManager assets) {
		super.setModelResource(assets);
		updateTexture();
	}

	@Override
	public void setModelResource(Model model_asset) {
		super.setModelResource(model_asset);
		updateTexture();
	}

	private void updateTexture() {
		Texture tex = new Texture(Gdx.files.internal(texture), true);
		tex.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Nearest);
		model.getMaterial("Texture_001").set(new TextureAttribute(TextureAttribute.Diffuse, tex));
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}
}
