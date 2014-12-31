package com.hh.ghoststory.game_models.core;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

/*
 * Basic game model class.
 */
public abstract class GameModel {
	public String model_resource;
	public String texture_resource;
	public ModelInstance model;
	public Vector3 position = new Vector3(0, 0, 0);
	public float rotation = 0;
	public Vector3 verticalAxis = new Vector3(0, 1, 0);

	public void update() {
		setRotation();
		setTranslation();
		model.transform.setToTranslation(position);
		model.transform.rotate(verticalAxis, rotation);
	}

	abstract public void setRotation();

	abstract public void setTranslation();

	/*
	 * Retrieves a model resource from an asset manager and attaches it to the game model.
	 * This is a little weird. The asset manager is in the screen.
	 * @TODO Make it not weird. Maybe override the method w/ other options? 
	 */
	public void setModelResource(AssetManager assets) {
		model = new ModelInstance(assets.get(model_resource, Model.class));
	}

	public void setModelResource(Model model_asset) {
		model = new ModelInstance(model_asset);
	}
}