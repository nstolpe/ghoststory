package com.hh.ghoststory.scene.gamemodels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.hh.ghoststory.scene.gamemodels.core.DynamicModel;

/*
 * The ghost. Also shouldn't stay here forever.
 */
public class Character extends DynamicModel {
	public float speed = 2;
	public String texture = "models/ghost_texture_blue.png";
	public Quaternion rotation = new Quaternion();

	public Character() {
		model_resource = "models/ghost.g3dj";
	}

	public void update() {
		model.transform.set(rotation).setTranslation(position);
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
