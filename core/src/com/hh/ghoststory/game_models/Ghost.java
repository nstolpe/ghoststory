package com.hh.ghoststory.game_models;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.hh.ghoststory.game_models.core.DynamicModel;

import java.util.HashMap;

/*
 * The ghost. Also shouldn't stay here forever.
 */
public class Ghost extends DynamicModel {
	public float speed = 2;
	public String texture = "models/ghost_texture_blue.png";

	public Ghost() {
		model_resource = "models/ghost.g3dj";
	}

	public void update() {
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
