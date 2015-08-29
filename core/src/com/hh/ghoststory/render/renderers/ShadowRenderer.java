package com.hh.ghoststory.render.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.hh.ghoststory.lib.MessageType;
import com.hh.ghoststory.render.shaders.SceneShader;
import com.hh.ghoststory.render.shaders.SceneShaderProvider;
import com.hh.ghoststory.render.shaders.ShadowMapShaderProvider;
import com.hh.ghoststory.screen.core.DualCameraScreen;

/**
 * Created by nils on 7/23/15.
 */
public class ShadowRenderer implements Telegraph {
    public DualCameraScreen screen;
	public FrameBuffer frameBufferShadows;
	public ModelBatch modelBatch;
	public ModelBatch modelBatchShadows;
	private MessageDispatcher messageDispatcher;

    public ShadowRenderer(DualCameraScreen screen) {
	    this(screen, screen.messageDispatcher);
    }

    public ShadowRenderer(DualCameraScreen screen, MessageDispatcher messageDispatcher) {
        this.screen = screen;
	    this.messageDispatcher = messageDispatcher;
	    init();
    }

	private void init() {
		screen.messageDispatcher.addListener(this, MessageType.INIT_SHADOW_BUFFER.val());
		initShadowBuffer();
		modelBatch = new ModelBatch(new SceneShaderProvider());
		modelBatchShadows = new ModelBatch(new ShadowMapShaderProvider(screen.shadowCasters));
	}

    public void render() {
        Gdx.gl.glClearColor(screen.clearRed, screen.clearGreen, screen.clearBlue, screen.clearAlpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	    renderDepth();
	    renderShadows();
	    renderScene();
    }
	public void renderDepth() {
		for (int i = 0; i < screen.shadowCasters.size; i++)
			screen.shadowCasters.get(i).render(screen.instances);
	}
	public void renderShadows() {
		frameBufferShadows.begin();

		Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 0.4f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatchShadows.begin(screen.camera);
		modelBatchShadows.render(screen.instances);
		modelBatchShadows.end();
//		ScreenshotFactory.saveScreenshot(frameBufferShadows.getWidth(), frameBufferShadows.getHeight(), "shadows");
		frameBufferShadows.end();
	}

	public void renderScene() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		frameBufferShadows.getColorBufferTexture().bind(SceneShader.textureNum);

		modelBatch.begin(screen.camera);
		modelBatch.render(screen.instances, screen.environment);
		modelBatch.end();
	}

    public void initShadowBuffer() {
	    if (frameBufferShadows != null) frameBufferShadows.dispose();
	    System.out.println("init the buffer");
        frameBufferShadows = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

	@Override
	public boolean handleMessage(Telegram msg) {
		MessageType type = MessageType.get(msg.message);
		switch (type) {
			case INIT_SHADOW_BUFFER:
				initShadowBuffer();
				break;
			default:
				break;
		}
		return true;
	}
}