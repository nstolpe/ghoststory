package com.hh.ghoststory.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.renderers.ShadowRenderer;
import com.hh.ghoststory.shadowcaster.PointShadowCaster;
import com.hh.ghoststory.utility.TestLoader;

/**
 * Created by nils on 7/14/15.
 */
public class PlayScreen extends DualCameraAbstractScreen {
	private ModelBatch modelBatch = new ModelBatch(Gdx.files.internal("shaders/default.vertex.glsl"), Gdx.files.internal("shaders/default.fragment.glsl"));
//	private ModelBatch modelBatch = new ModelBatch();

	private Environment environment = new Environment();
	private ShadowRenderer renderer = new ShadowRenderer(this);

	public PlayScreen(GhostStory game, Camera camera) {
		super(game, camera);
	}
	public PlayScreen(GhostStory game) {
		super(game);
		setClear(0.7f, 0.1f, 1f, 1);

		PointShadowCaster p = new PointShadowCaster(new PointLight());
		// load assets. These should be pulled in through a config.
		assetManager.load("models/ghost.g3dj", Model.class);
		assetManager.load("models/scene.g3dj", Model.class);
		assetManager.load("models/tile.g3dj", Model.class);

		// Test loader is for temporary data/assets/whatever
		TestLoader.setLights(environment);
		loading = true;
	}

	@Override
	public void show() {
	}



	@Override
	public void render(float delta) {
		super.render(delta);
		renderer.render();

		if (loading && assetManager.update())
			doneLoading();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// Register shadow lights and loop through them
		// for (ShadowCaster caster: shadowcasters) caster.render(instances);
		TestLoader.pointShadowCaster.render(instances);

		camera.update();
		modelBatch.begin(camera);
		modelBatch.render(instances, environment);
		modelBatch.end();
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
}
