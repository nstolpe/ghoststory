package com.hh.ghoststory.utility;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by nils on 7/15/15.
 */
public class TestLoader {
	public static Array<ModelInstance> getTestModels(AssetManager assetManager) {
		ModelInstance character = new ModelInstance(assetManager.get("models/ghost.g3dj", Model.class));
		character.transform.setTranslation(5,0,5);
		ModelInstance tile;
		Array<ModelInstance> instances = new Array<ModelInstance>();

		instances.add(character);

//		10x10 grid on the ground.
		for (int z = 0; z < 10; z++) {
			for (int x = 0; x < 20; x++) {
				tile = new ModelInstance(assetManager.get("models/tile.g3dj", Model.class));
				tile.transform.setTranslation(x,0,z);
				instances.add(tile);
			}
		}
//		builds the far side of the wall thingy, that can't be seen.
		for (int z = 0; z < 3; z++) {
			for (int y = 0; y < 2; y++) {
				tile = new ModelInstance(assetManager.get("models/tile.g3dj", Model.class));
				tile.transform.setToRotation(new Vector3(0, 0, 1), 90);
				tile.transform.setTranslation(9.5f,y + 0.5f,z + 5);
				instances.add(tile);
			}
		}
//		top of the little wall thing.
		for (int z = 0; z < 3; z++) {
			tile = new ModelInstance(assetManager.get("models/tile.g3dj", Model.class));
			tile.transform.setTranslation(10, 2, z + 5);
			instances.add(tile);
		}
//		thin front of the wall thing.
		for (int y = 0; y < 2; y++) {
			tile = new ModelInstance(assetManager.get("models/tile.g3dj", Model.class));
			tile.transform.setToRotation(new Vector3(1, 0, 0), 90);
			tile.transform.setTranslation(10, y + 0.5f, 7.5f);
			instances.add(tile);
		}
//		thin back of wall
		for (int y = 0; y < 2; y++) {
			tile = new ModelInstance(assetManager.get("models/tile.g3dj", Model.class));
			tile.transform.setToRotation(new Vector3(1, 0, 0), 270);
			tile.transform.setTranslation(10, y + 0.5f, 4.5f);
			instances.add(tile);
		}
//		side of the wall thing, the big part.
		for (int z = 0; z < 3; z++) {
			for (int y = 0; y < 2; y++) {
				tile = new ModelInstance(assetManager.get("models/tile.g3dj", Model.class));
				tile.transform.setToRotation(new Vector3(0,0,1), 270);
				tile.transform.setTranslation(10.5f, y + 0.5f, z + 5);
				instances.add(tile);
			}
		}

		return instances;
	}
	public static void setLights(Environment environment) {
//		travellingLight = new PointLight().set(new Color(0f,1f,0f,1f),6,1,6,1);
//		colorSwitchLight = new PointLight().set(colorSwitchColor,12,1,10,1);
		BaseLight[] lights = {
				new PointLight().set(new Color(1f, 1f, 1f, 1f), 0, 1, 0, 1),
				new PointLight().set(new Color(1f, 0f, 0f, 1f), 4, 2, 4, 1),
				new PointLight().set(new Color(1f, 1f, 1f, 1f), 6, 2, 6, 1),
				new PointLight().set(new Color(0.3f, 0.3f, 1f, 1f), 4, 2, 6, 1),
				new PointLight().set(new Color(1f, 0.3f, 0.3f, 1f), 6, 2, 4, 1),
				new PointLight().set(new Color(0f, 0f, 1f, 1f), 6, 1, 0, 1),
				new SpotLight().set(new Color(0.5f, 0.3f, 1f, 1f), new Vector3(3,1,3), new Vector3(-1, 0, -1), 1, 1, 1)
//				travellingLight,
//				colorSwitchLight
		};
		environment.add(lights);
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.1f, 0.1f, 0.1f, 1));
//		return environment;
	}
}
