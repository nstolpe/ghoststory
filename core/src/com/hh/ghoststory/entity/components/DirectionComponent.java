package com.hh.ghoststory.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by nils on 9/29/15.
 */
public class DirectionComponent implements Component {
	public Vector3 direction;

	public DirectionComponent() {}

	public DirectionComponent(Vector3 direction) {
		this.direction = direction;
	}

	public DirectionComponent position(Vector3 direction) {
		this.direction = direction;
		return this;
	}
}
