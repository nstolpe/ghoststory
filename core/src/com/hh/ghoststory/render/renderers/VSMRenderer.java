package com.hh.ghoststory.render.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.hh.ghoststory.lib.MessageTypes;
import com.hh.ghoststory.render.shaders.PlayShader;
import com.hh.ghoststory.render.shaders.PlayShaderProvider;
import com.hh.ghoststory.render.shaders.ShadowMapShaderProvider;
import com.hh.ghoststory.scene.Lighting;
import com.hh.ghoststory.scene.lights.core.Caster;
import com.hh.ghoststory.screen.PlayScreen;

/**
 * Created by nils on 7/23/15.
 */
public class VSMRenderer implements Telegraph, Disposable {
	public PlayScreen screen;
	public FrameBuffer frameBufferShadows;
	public ModelBatch modelBatch;
	public ModelBatch modelBatchShadows;
	private MessageDispatcher frameworkDispatcher;

	public VSMRenderer(PlayScreen screen) {
		this(screen, screen.frameworkDispatcher);
	}

	public VSMRenderer(PlayScreen screen, MessageDispatcher frameworkDispatcher) {
		this.screen = screen;
		this.frameworkDispatcher = frameworkDispatcher;
		frameworkDispatcher.addListener(this, MessageTypes.Framework.INIT_SHADOW_BUFFER);
		modelBatch = new ModelBatch(new DefaultShaderProvider() {
			protected Shader createShader(final Renderable renderable) {
				return new PlayShader(renderable);
			}
		});
		modelBatchShadows = new ModelBatch(new ShadowMapShaderProvider());
		initShadowBuffer();
	}

	public void render(Camera camera, Array<ModelInstance> instances, Array<Caster> casters, Lighting environment) {
		// @TODO next two from Screen. Maybe pass in or set width for the Renderer, as well as clear colors?
		// Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClearColor(screen.clearRed, screen.clearGreen, screen.clearBlue, screen.clearAlpha);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		renderDepth(casters, instances);
		renderShadows(camera, instances);
		renderScene(camera, instances, environment);
	}
	public void renderDepth(Array<Caster> caster, Array<ModelInstance> instances) {
		for (int i = 0; i < caster.size; i++)
			caster.get(i).render(instances);
	}

	public void renderShadows(Camera camera, Array<ModelInstance> instances) {
		frameBufferShadows.begin();

		Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 0.4f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatchShadows.begin(camera);
		modelBatchShadows.render(instances);
		modelBatchShadows.end();
//		ScreenshotFactory.saveScreenshot(frameBufferShadows.getWidth(), frameBufferShadows.getHeight(), "shadows");
		frameBufferShadows.end();
	}

	public void renderScene(Camera camera, Array<ModelInstance> instances, Lighting environment) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		frameBufferShadows.getColorBufferTexture().bind(PlayShader.textureNum);

		modelBatch.begin(camera);
		modelBatch.render(instances, environment);
		modelBatch.end();
//		ScreenshotFactory.saveScreenshot(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), "scene");
	}

	public void initShadowBuffer() {
		if (frameBufferShadows != null) frameBufferShadows.dispose();
		frameBufferShadows = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		modelBatchShadows.dispose();
		frameBufferShadows.dispose();
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		switch (msg.message) {
			case MessageTypes.Framework.INIT_SHADOW_BUFFER:
				initShadowBuffer();
				break;
			default:
				break;
		}
		// should also be able to return false.
		return true;
	}

}