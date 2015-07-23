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
import com.hh.ghoststory.shadowcaster.PointShadowCaster;

/**
 * Created by nils on 7/15/15.
 */
public class TestLoader {
	public static PointShadowCaster pointShadowCaster = new PointShadowCaster(new PointLight().set(new Color(0.3f, 0.3f, 1f, 1f), 4, 1, 6, 1));

	public static Array<ModelInstance> getTestModels(AssetManager assetManager) {
		ModelInstance character = new ModelInstance(assetManager.get("models/ghost.g3dj", Model.class));
		character.transform.setTranslation(5,0,5);
		ModelInstance tile;
		ModelInstance scene = new ModelInstance(assetManager.get("models/scene.g3dj", Model.class));
		scene.transform.setTranslation(0,0,0);
		Array<ModelInstance> instances = new Array<ModelInstance>();

		instances.add(character);
		instances.add(scene);

		return instances;
	}
	public static void setLights(Environment environment) {
//		travellingLight = new PointLight().set(new Color(0f,1f,0f,1f),6,1,6,1);
//		colorSwitchLight = new PointLight().set(colorSwitchColor,12,1,10,1);

		BaseLight[] lights = {
				new PointLight().set(new Color(1f, 1f, 1f, 1f), 0, 1, 0, 1),
				new PointLight().set(new Color(1f, 0f, 0f, 1f), 4, 2, 4, 1),
				new PointLight().set(new Color(1f, 1f, 1f, 1f), 6, 2, 6, 1),
				pointShadowCaster.light,
//				new PointLight().set(new Color(0.3f, 0.3f, 1f, 1f), 4, 2, 6, 1),
				new PointLight().set(new Color(1f, 0.3f, 0.3f, 1f), 6, 2, 4, 1),
//				new PointLight().set(new Color(0f, 0f, 1f, 1f), 6, 1, 0, 1),
				new SpotLight().set(new Color(0.5f, 0.3f, 1f, 1f), new Vector3(3,1,3), new Vector3(-1, 0, -1), 1, 1, 1)
//				travellingLight,
//				colorSwitchLight
		};
		environment.add(lights);
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.1f, 0.1f, 0.1f, 1));
//		return environment;
	}
}
