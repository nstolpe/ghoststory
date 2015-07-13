package com.hh.ghoststory;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.hh.ghoststory.screens.GameScreen;

public class MovingPointLight extends PointLight
{
	public Vector3	originalPosition	= new Vector3();
	public float	angle				= 0;
	public float	distance			= 20f;

	public MovingPointLight(final GameScreen gameScreen, final Vector3 position)
	{
		super(gameScreen, position);
		originalPosition.set(position);
	}

	@Override
	public void act(final float delta)
	{
		angle += delta / 1f;
		position.set(originalPosition.x + MathUtils.cos(angle) * distance, originalPosition.y, originalPosition.z + MathUtils.sin(angle) * distance);
		camera.position.set(position);
		needsUpdate = true;
	}

}
