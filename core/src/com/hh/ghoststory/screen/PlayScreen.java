package com.hh.ghoststory.screen;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.components.*;
import com.hh.ghoststory.render.renderers.ShadowRenderer;
import com.hh.ghoststory.screen.core.DualCameraScreen;

/**
 * Created by nils on 7/14/15.
 * Screen for interaction with the game world. Not inventory, not save menus, not stats, just the gameworld.
 */
public class PlayScreen extends DualCameraScreen {
	private InputMultiplexer multiplexer = new InputMultiplexer();

	public PlayScreen(GhostStory game, Camera camera) {
		super(game, camera);
	}
	public PlayScreen(GhostStory game) {
		super(game);
		setClear(0.7f, 0.1f, 1f, 1);
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		instances.clear();

		// asset loading has just finished, loading hasn't been updated
		if (loading && assetManager.update()) {
			doneLoading();
		// asset loading is finished and post load hooks have completed
		// maybe move to an update function
		} else if (!loading){
			ImmutableArray<Entity> renderables = game.engine.getEntitiesFor(Family.all(GeometryComponent.class, RenderComponent.class, PositionComponent.class, InstanceComponent.class).get());
			// This could go in an entity system.
			for (Entity renderable : renderables) {
				// something should have stopped earlier if instance wasn't set on the component.
				if (Mappers.instance.get(renderable).instance != null) {
					Vector3 position = Mappers.position.get(renderable).position;
					ModelInstance instance = Mappers.instance.get(renderable).instance;
					instance.transform.setTranslation(position);

					// update entities with animation components. Maybe a 2nd loop instead of the check?
					if (Mappers.animation.has(renderable))
						Mappers.animation.get(renderable).controller.update(delta);

					instances.add(instance);
				}
			}

			messageDispatcher.update(delta);
		}

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		active.update();
		playDetector.update();

		renderer.render(active, instances, shadowCasters, lighting);
	}

	@Override
	public void hide() {
		super.hide();
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}

	@Override
	public void dispose() {
		super.dispose();
	}
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		renderer.initShadowBuffer();
	}
}