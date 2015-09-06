package com.hh.ghoststory.entity.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * Created by nils on 9/5/15.
 */
public class BoundingBoxComponent implements Component {
	public BoundingBox box;

	public BoundingBoxComponent() {}

	public BoundingBoxComponent(BoundingBox box) {
		box(box);
	}

	public BoundingBoxComponent box(BoundingBox box) {
		this.box = box;
		return this;
	}
}
