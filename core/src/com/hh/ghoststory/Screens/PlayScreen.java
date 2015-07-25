package com.hh.ghoststory.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.Renderers.ShadowRenderer;
import com.hh.ghoststory.Utility.TestLoader;

/**
 * Created by nils on 7/14/15.
 */
public class PlayScreen extends DualCameraAbstractScreen {

	private ShadowRenderer renderer = new ShadowRenderer(this);

	public PlayScreen(GhostStory game, Camera camera) {
		super(game, camera);
	}
	public PlayScreen(GhostStory game) {
		super(game);
		setClear(0.7f, 0.1f, 1f, 1);

		// load assets. These should be pulled in through a config.
		assetManager.load("models/ghost.g3dj", Model.class);
		assetManager.load("models/scene.g3dj", Model.class);
		assetManager.load("models/tile.g3dj", Model.class);

		// TestLoader is for temporary data/assets/whatever. Only used for testing
		// this setup, move stuff to more permanent locations once it's all working.
		TestLoader.setLights(environment);
		shadowCasters.addAll(TestLoader.pointShadowCasters);

		loading = true;
	}

	@Override
	public void show() {
	}



	@Override
	public void render(float delta) {
		super.render(delta);


		if (loading && assetManager.update())
			doneLoading();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


		camera.update();
//		modelBatch.begin(camera);
//		modelBatch.render(instances, environment);
//		modelBatch.end();
		renderer.render();
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
