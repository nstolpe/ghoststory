package com.hh.ghoststory.GameModels;

import com.badlogic.gdx.math.Vector3;
import com.hh.ghoststory.GameModels.core.StaticModel;

/*
 * Tiles for making the ground. Shouldn't stay here forever.
 */
public class Tile extends StaticModel {
	public Tile() {
		model_resource = "models/tile.g3dj";
	}
	public Tile(Vector3 position) {
		model_resource = "models/tile.g3dj";
		position.set(position);
	}

	public Tile(float x, float y, float z) {
		model_resource = "models/tile.g3dj";
		position.set(x, y, z);
//		this.model.transform.setToTranslation(x,y,z);
		verticalAxis = new Vector3(0, 1, 0);
	}

	@Override
	public void update() {
		setRotation();
		setTranslation();
		model.transform.setToTranslation(position);
		model.transform.rotate(verticalAxis, rotation);
	}
	public void setTranslation() {
		model.transform.setToTranslation(position);
	}
}