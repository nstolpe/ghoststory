package com.hh.ghoststory.game_models;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.hh.ghoststory.game_models.core.DynamicModel;

/*
 * The ghost. Also shouldn't stay here forever.
 */
public class Ghost extends DynamicModel {
	public float speed = 2;
	public String texture = "models/ghost_texture_blue.png";
	public Quaternion rotation = new Quaternion();

	public Ghost() {
		model_resource = "models/ghost.g3dj";
	}

	public void update() {
		model.transform.setFromEulerAngles(rotation.getYaw(), rotation.getPitch(), rotation.getRoll()).setTranslation(position);
//		model.transform.setToRotation(0, 1, 0, rotation.getAngle()).setTranslation(position);
//		model.transform.rotate(rotation).setTranslation(position);
//		model.transform.setToTranslation(position).rotate(rotation);
	}

//	@Override
//	public void setModelResource(AssetManager assets) {
//		super.setModelResource(assets);
//		updateTexture();
//	}

//	@Override
//	public void setModelResource(Model model_asset) {
//		super.setModelResource(model_asset);
//		updateTexture();
//	}

	/*
	 * This or something like it needs to be added later. But should be in parent class.
	 */
	public void setTexture() {
		Texture tex = new Texture(Gdx.files.internal(texture), true);
		tex.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Nearest);
		model.getMaterial("Texture_001").set(new TextureAttribute(TextureAttribute.Diffuse, tex));
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}
}
