package com.hh.ghoststory.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.utility.TestLoader;

/**
 * Created by nils on 7/14/15.
 */
public class PlayScreen extends DualCameraAbstractScreen {
	private AssetManager assetManager = new AssetManager();
	private ModelBatch modelBatch = new ModelBatch(Gdx.files.internal("shaders/default.vertex.glsl"), Gdx.files.internal("shaders/default.fragment.glsl"));
	public Array<ModelInstance> instances = new Array<>();
	private boolean loading;

	private Environment environment = new Environment();

	public PlayScreen(GhostStory game) {
		super(game);
		setActiveCamera(new PerspectiveCamera());
		camController = new CameraInputController(camera);
		Gdx.input.setInputProcessor(camController);
		// make the background purple so we know something is happening.
		setClear(0.7f, 0.1f, 1f, 1);

		// load assets. These should be pulled in through a config.
		assetManager.load("models/ghost.g3dj", Model.class);
		assetManager.load("models/tile.g3dj", Model.class);

		TestLoader.setLights(environment);
		loading = true;
	}

	/**
	 * Called when the asset manager has finished updating. Make models here.
	 * @TODO Add more stuff that needs to happen after loading.
	 */
	public void doneLoading() {
		instances = TestLoader.getTestModels(assetManager);

		loading = false;
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
