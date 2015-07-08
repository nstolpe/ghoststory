package com.hh.ghoststory.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.screens.GameScreen;

/**
 * Created by nils on 1/24/15.
 */
public class ModelBatchRenderer extends AbstractRenderer {
	public Environment environment = new Environment();
	private ModelBatch modelBatch;
	private GameScreen screen;


	private Array<ModelInstance> modelInstances;
	public AssetManager assetManager = new AssetManager();

	public ModelBatchRenderer(GameScreen screen) {
		this.screen = screen;
		setModelBatch(new ModelBatch(Gdx.files.internal("shaders/default.vertex.glsl"), Gdx.files.internal("shaders/default.fragment.glsl")));
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
		modelBatch.begin(screen.cameraHandler.getActiveCamera());
		for (ModelInstance model : modelInstances)
			modelBatch.render(model, environment);
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