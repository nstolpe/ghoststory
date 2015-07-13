package com.hh.ghoststory.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.ShadowMapShader;
import com.hh.ghoststory.SimpleTextureShader;
import com.hh.ghoststory.screens.GameScreen;

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
	private ShaderProgram setupShader(String type) {
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
		modelBatch.begin(screen.getActiveCamera());
		for (ModelInstance model : modelInstances)
//			modelBatch.render(model, environment);
			modelBatch.render(model);
		modelBatch.end();
	}

	@Override
	public void setRenderables(Array modelInstances) {
		this.modelInstances = modelInstances;
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
	}

	public void setUpLights(BaseLight[] lights) {
		environment.add(lights);
	}
}