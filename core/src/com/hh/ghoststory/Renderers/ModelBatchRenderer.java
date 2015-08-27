package com.hh.ghoststory.Renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.Light;
import com.hh.ghoststory.ShadowMapShader;
import com.hh.ghoststory.SimpleTextureShader;
import com.hh.ghoststory.screen.GameScreen;

import java.util.ArrayList;

/**
 * Created by nils on 1/24/15.
 */
public class ModelBatchRenderer extends AbstractRenderer {
	public Environment environment = new Environment();
	private GameScreen screen;


	private Array<ModelInstance> modelInstances;
	public AssetManager assetManager = new AssetManager();
	private ShaderProgram shaderProgram;
	private ShaderProgram shaderProgramShadows;
	private ModelBatch modelBatch;
	private ModelBatch modelBatchShadows;
	private FrameBuffer frameBufferShadows;

	public ModelBatchRenderer(GameScreen screen) {
		this.screen = screen;
		initShaders();
	}

	private void initShaders() {
		shaderProgram = setupShader("scene");
		modelBatch = new ModelBatch(new DefaultShaderProvider() {
			@Override
			protected Shader createShader(final Renderable renderable) {
				return new SimpleTextureShader(renderable, shaderProgram);
			}
		});

//		final GameScreen self = this;
		shaderProgramShadows = setupShader("shadow");
		modelBatchShadows = new ModelBatch(new DefaultShaderProvider() {
			@Override
			protected Shader createShader(final Renderable renderable) {
				return new ShadowMapShader(screen, renderable, shaderProgramShadows);
			}
		});
	}
	public ShaderProgram setupShader(String type) {
		ShaderProgram.pedantic = false;
		final ShaderProgram shaderProgram = new ShaderProgram(
				Gdx.files.internal("shaders/" + type + ".vertex.glsl"),
				Gdx.files.internal("shaders/" + type + ".fragment.glsl")
		);

		if (!shaderProgram.isCompiled()) {
			System.err.println("Error with shader " + type + ": " + shaderProgram.getLog());
			System.exit(1);
		} else {
			Gdx.app.log("init", "Shader " + type + " compiled " + shaderProgram.getLog());
		}

		return shaderProgram;
	}

	public void setModelBatch(ModelBatch modelBatch) {
		this.modelBatch = modelBatch;
	}

	public ModelBatch getModelBatch() {
		return modelBatch;
	}

	/*
	 * Change this to use the messenger system. Request the active camera...
	 */
	@Override
	public void render() {
//		modelBatch.begin(screen.getActiveCamera());
//		for (ModelInstance model : modelInstances)
////			modelBatch.render(model, environment);
//			modelBatch.render(model);
//		modelBatch.end();
		for (final Light light : screen.lights) {
			for (ModelInstance model : modelInstances) light.render(model);
		}
		renderShadows();
		renderScene();
	}

	public void renderScene() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
		shaderProgram.begin();
		final int textureNum = 4;
		frameBufferShadows.getColorBufferTexture().bind(textureNum);

		shaderProgram.setUniformi("u_shadows", textureNum);
		shaderProgram.setUniformf("u_screenWidth", Gdx.graphics.getWidth());
		shaderProgram.setUniformf("u_screenHeight", Gdx.graphics.getHeight());
		shaderProgram.end();

		modelBatch.begin(screen.getActiveCamera());
		for (ModelInstance model : modelInstances) modelBatch.render(model);
		modelBatch.end();
	}
	public void renderShadows() {
		if (frameBufferShadows == null) {
			frameBufferShadows = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		}
		frameBufferShadows.begin();

		Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 0.4f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatchShadows.begin(screen.getActiveCamera());
		for (ModelInstance model : modelInstances) modelBatchShadows.render(model);
		modelBatchShadows.end();

		frameBufferShadows.end();
	}

	@Override
	public void setRenderables(Array modelInstances) {
		this.modelInstances = modelInstances;
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		modelBatchShadows.dispose();
		frameBufferShadows.dispose();
		shaderProgram.dispose();
		shaderProgramShadows.dispose();
	}

	public void setUpLights(ArrayList<Light> lights) {
//		environment.add(lights);
	}
}