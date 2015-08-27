package com.hh.ghoststory.Renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.hh.ghoststory.Overrides.ShadowMapShader;
import com.hh.ghoststory.Overrides.GameShader;
import com.hh.ghoststory.screen.core.DualCameraScreen;
import com.hh.ghoststory.lib.utility.ShaderUtil;

/**
 * Created by nils on 7/23/15.
 */
public class ShadowRenderer {
    public DualCameraScreen screen;
	public FrameBuffer frameBufferShadows;
	public ModelBatch modelBatch;
	public ShaderProgram shaderProgramShadows;
	public ModelBatch modelBatchShadows;

    public ShadowRenderer(DualCameraScreen screen) {
        this.screen = screen;
	    init();
    }

	private void init() {
		frameBufferShadows = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		shaderProgramShadows = ShaderUtil.getShader("shadow");

		modelBatch = new ModelBatch(new DefaultShaderProvider() {
			@Override
			protected Shader createShader(final Renderable renderable) {
				return new GameShader(renderable);
			}
		});

		modelBatchShadows = new ModelBatch(new DefaultShaderProvider() {
			@Override
			protected Shader createShader(final Renderable renderable) {
				return new ShadowMapShader(renderable, shaderProgramShadows, screen.shadowCasters);
			}
		});
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

		frameBufferShadows.getColorBufferTexture().bind(GameShader.textureNum);

		modelBatch.begin(screen.camera);
		modelBatch.render(screen.instances, screen.environment);
		modelBatch.end();
	}

    public void updateShadowBuffer() {
        frameBufferShadows.dispose();
        frameBufferShadows = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

}