package com.hh.ghoststory.entity.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.hh.ghoststory.entity.Mappers;
import com.hh.ghoststory.entity.components.BoundingBoxComponent;
import com.hh.ghoststory.entity.components.InstanceComponent;

/**
 * This system just sets up the generation of BoundingBoxComponent's for entities that have not
 * yet been assigned one but have been assigned a ModelInstanceComponent.
 *
 * Controlled by the asset/doneloading chain.
 */
public class BoundingBoxSystem extends EntitySystem {
	private Engine engine;
	@Override
	public void addedToEngine(Engine engine) {
		this.engine = engine;
	}
	@Override
	public void update(float deltaTime) {
		ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(InstanceComponent.class).exclude(BoundingBoxComponent.class).get());
		for (Entity entity : entities) {
			ModelInstance instance = Mappers.instance.get(entity).instance;
			BoundingBox box = instance.calculateBoundingBox(new BoundingBox());
			engine.getEntity(entity.getId()).add(new BoundingBoxComponent(box));
		}
	}
}
