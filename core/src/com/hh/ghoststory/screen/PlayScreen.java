package com.hh.ghoststory.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.lib.MessageType;
import com.hh.ghoststory.render.renderers.ShadowRenderer;
import com.hh.ghoststory.lib.utility.TestLoader;
import com.hh.ghoststory.screen.core.DualCameraScreen;

/**
 * Created by nils on 7/14/15.
 */
public class PlayScreen extends DualCameraScreen implements Telegraph {

	private ShadowRenderer renderer = new ShadowRenderer(this);
	private Array<AnimationController> animationControllers = new Array<AnimationController>();

	public PlayScreen(GhostStory game, Camera camera) {
		super(game, camera);
	}
	public PlayScreen(GhostStory game) {
		super(game);
		setClear(0.7f, 0.1f, 1f, 1);
		// load assets. These should be pulled in through a config.
		assetManager.load("models/ghost_red.g3dj", Model.class);
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

		if (loading && assetManager.update()) {
			doneLoading();
		} else {
			// update animation controllers
			for (int i =0; i < animationControllers.size; i++) {
				animationControllers.get(i).update(delta);
			}
			MessageDispatcher.getInstance().update(delta);
		}

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		camera.update();
		renderer.render();
	}

	@Override
	public void doneLoading() {
		super.doneLoading();
		Array<ModelInstance> mobGhosts = TestLoader.getGhostModels(assetManager);
		ModelInstance scene = TestLoader.getSceneModel(assetManager);

		for (int i = 0; i < mobGhosts.size; i++) {
			AnimationController ac = new AnimationController(mobGhosts.get(i));
//			ac.setAnimation("float", -1);
			ac.setAnimation("float", 0f, -1, -1, i + 1, new AnimationController.AnimationListener() {
				@Override
				public void onEnd(AnimationController.AnimationDesc animation) {}
				@Override
				public void onLoop(AnimationController.AnimationDesc animation) {}
			});
			animationControllers.add(ac);
		}

		instances.add(scene);
		instances.addAll(mobGhosts);
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
		MessageDispatcher.getInstance().dispatchMessage(this, MessageType.UPDATE_BUFFER.val());
//		renderer.initShadowBuffer();
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}
}