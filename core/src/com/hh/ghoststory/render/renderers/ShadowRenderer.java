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
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.hh.ghoststory.lib.MessageTypes;
import com.hh.ghoststory.render.shaders.SceneShader;
import com.hh.ghoststory.render.shaders.SceneShaderProvider;
import com.hh.ghoststory.render.shaders.ShadowMapShaderProvider;
import com.hh.ghoststory.scene.Lighting;
import com.hh.ghoststory.scene.lights.core.Caster;
import com.hh.ghoststory.screen.PlayScreen;

/**
 * Created by nils on 7/23/15.
 */
public class ShadowRenderer implements Telegraph, Disposable {
    public PlayScreen screen;
	public FrameBuffer frameBufferShadows;
	public ModelBatch modelBatch;
	public ModelBatch modelBatchShadows;
	private MessageDispatcher messageDispatcher;

    public ShadowRenderer(PlayScreen screen) {
	    this(screen, screen.messageDispatcher);
    }

    public ShadowRenderer(PlayScreen screen, MessageDispatcher messageDispatcher) {
        this.screen = screen;
	    this.messageDispatcher = messageDispatcher;
	    init();
    }

	private void init() {
		screen.messageDispatcher.addListener(this, MessageTypes.Screen.INIT_SHADOW_BUFFER);
		initShadowBuffer();
		modelBatch = new ModelBatch(new SceneShaderProvider());
		modelBatchShadows = new ModelBatch(new ShadowMapShaderProvider(screen.casters));
	}

    public void render(Camera camera, Array<ModelInstance> instances, Array<Caster> shadowCasters, Lighting environment) {
        Gdx.gl.glClearColor(screen.clearRed, screen.clearGreen, screen.clearBlue, screen.clearAlpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	    renderDepth(shadowCasters, instances);
	    renderShadows(camera, instances);
	    renderScene(camera, instances, environment);
    }
	public void renderDepth(Array<Caster> shadowCasters, Array<ModelInstance> instances) {
		for (int i = 0; i < shadowCasters.size; i++)
			shadowCasters.get(i).render(instances);
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

		frameBufferShadows.getColorBufferTexture().bind(SceneShader.textureNum);

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
	public boolean handleMessage(Telegram msg) {
		switch (msg.message) {
			case MessageTypes.Screen.INIT_SHADOW_BUFFER:
				initShadowBuffer();
				break;
			default:
				break;
		}
		// should also be able to return false.
		return true;
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		modelBatchShadows.dispose();
		frameBufferShadows.dispose();
	}
}