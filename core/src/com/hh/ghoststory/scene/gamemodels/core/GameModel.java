package com.hh.ghoststory.scene.gamemodels.core;

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
//		setRotation();
//		setTranslation();
//		model.transform.setToTranslation(position);
//		model.transform.rotate(verticalAxis, rotation);
	}

	abstract public void setRotation();

	abstract public void setTranslation();

	public void setModelResource(Model model_asset) {
		model = new ModelInstance(model_asset);
	}
}