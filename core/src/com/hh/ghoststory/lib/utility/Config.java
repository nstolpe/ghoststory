package com.hh.ghoststory.lib.utility;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.hh.ghoststory.components.Components;
import com.hh.ghoststory.scene.lights.PointShadowCaster;
import com.hh.ghoststory.scene.lights.core.ShadowCaster;

/**
 * Created by nils on 7/15/15.
 */
public class Config {
	public static PointShadowCaster[] pointShadowCasters = new PointShadowCaster[]{
			new PointShadowCaster(new PointLight().set(new Color(0.3f, 0.3f, 1f, 1f), 14, 6, 6, 10)),
			new PointShadowCaster(new PointLight().set(new Color(1f, 1f, 1f, 1f), 0, 5, 0, 10)),
//			new PointShadowCaster(new PointLight().set(new color(1f, 0f, 0f, 1f), 4, 5, 4, 1)),
//			new PointShadowCaster(new PointLight().set(new color(1f, 1f, 1f, 1f), 6, 20, 6, 1)),
			new PointShadowCaster(new PointLight().set(new Color(1f, 0.3f, 0.3f, 1f), 6, 5, 4, 10))
	};
	public static Vector3[] characterPositions = new Vector3[]{
			new Vector3(5,0,5),
			new Vector3(0,0,0),
			new Vector3(10,0,6),
	};

	// fake values that should have been pulled in through config
	// create all entities through the config.
	// loop through entities to add models and lights to the scene.
	// behaviors can be added too, but pc behavior doesn't need to be here (aside from spawn point).
	public Array<Entity> entities = new Array<Entity>() {
		{
			// scene
			add(new Entity()
				.add(new Components.IDComp().id("scene"))
				.add(new Components.NameComp().name("Development Scene"))
				.add(new Components.GeometryComp().file("scene.g3dj"))
				.add(new Components.AmbientComp().colorAttribute(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f))));
			// ghost 1
			add(new Entity()
				.add(new Components.IDComp().id("red_ghost"))
				.add(new Components.NameComp().name("Red Ghost One"))
				.add(new Components.GeometryComp().file("ghost_red.g3dj"))
				.add(new Components.PositionComp().position(new Vector3(5, 0, 5)))
				.add(new Components.AnimationComp().animations(
                                new Array<ObjectMap<String, Object>>() {
                                    {
                                        add(new ObjectMap<String, Object>() {
                                            {
                                                put("id", "float");
                                                put("offset", 0);
                                                put("duration", -1);
                                                put("loopcount", -1);
                                                put("speed", 1.0);
                                                put("listener", null);
                                            }
                                        });
                                    }
                                })
				));
			// ghost 2
			add(new Entity()
				.add(new Components.IDComp().id("red_ghost"))
				.add(new Components.NameComp().name("Red Ghost Two"))
				.add(new Components.GeometryComp().file("ghost_red.g3dj"))
				.add(new Components.PositionComp().position(new Vector3(5, 0, 5)))
				.add(new Components.AnimationComp().animations(
                                new Array<ObjectMap<String, Object>>() {
                                    {
                                        add(new ObjectMap<String, Object>() {
                                            {
                                                put("id", "float");
                                                put("offset", 0);
                                                put("duration", -1);
                                                put("loopcount", -1);
                                                put("speed", 1.5);
                                                put("listener", null);
                                            }
                                        });
                                    }
                                })
				));
			// ghost 3
			add(new Entity()
				.add(new Components.IDComp().id("red_ghost"))
				.add(new Components.NameComp().name("Red Ghost Three"))
				.add(new Components.GeometryComp().file("ghost_red.g3dj"))
				.add(new Components.PositionComp().position(new Vector3(5, 0, 5)))
				.add(new Components.AnimationComp().animations(
                                new Array<ObjectMap<String, Object>>() {
                                    {
                                        add(new ObjectMap<String, Object>() {
                                            {
                                                put("id", "float");
                                                put("offset", 0);
                                                put("duration", -1);
                                                put("loopcount", -1);
                                                put("speed", 0.5);
                                                put("listener", null);
                                            }
                                        });
                                    }
                                })
				));
			// lights 1
			add(new Entity()
                .add(new Components.IDComp().id("light"))
                .add(new Components.NameComp().name("Light One"))
                .add(new Components.PositionComp().position(new Vector3(0, 5, 0)))
                .add(new Components.ColorComp().color(new Color(1f, 1f, 1f, 1f)))
                .add(new Components.IntensityComp().intensity(10))
			);
			// lights 2
			add(new Entity()
                .add(new Components.IDComp().id("light"))
                .add(new Components.NameComp().name("Light two"))
                .add(new Components.PositionComp().position(new Vector3()))
                .add(new Components.ColorComp().color(new Color(0.3f, 0.3f, 1f, 1f)))
                .add(new Components.IntensityComp().intensity(10))
			);
			// lights 3
			add(new Entity()
                .add(new Components.IDComp().id("light"))
                .add(new Components.NameComp().name("Light three"))
                .add(new Components.PositionComp().position(new Vector3(6, 5, 5)))
                .add(new Components.ColorComp().color(new Color(1f, 0.3f, 0.3f, 1f)))
                .add(new Components.IntensityComp().intensity(10))
			);
			// lights 4
			add(new Entity()
                .add(new Components.IDComp().id("light"))
                .add(new Components.NameComp().name("Light four"))
                .add(new Components.PositionComp().position(new Vector3(4, 5, 4)))
                .add(new Components.ColorComp().color(new Color(1f, 0f, 0f, 1f)))
                .add(new Components.IntensityComp().intensity(10))
			);
			// lights 5
			add(new Entity()
                .add(new Components.IDComp().id("light"))
                .add(new Components.NameComp().name("Light five"))
                .add(new Components.PositionComp().position(new Vector3(6, 20, 6)))
                .add(new Components.ColorComp().color(new Color(1f, 1f, 1f, 1f)))
                .add(new Components.IntensityComp().intensity(10))
			);
		}
	};

	public static Array<ModelInstance> getTestModels(AssetManager assetManager) {
		Array<ModelInstance> instances = new Array<ModelInstance>();

		ModelInstance scene = new ModelInstance(assetManager.get("models/scene.g3dj", Model.class));
		scene.transform.setTranslation(0,0,0);
		instances.add(scene);

		for (int i =0; i < characterPositions.length; i++) {
			ModelInstance character = new ModelInstance(assetManager.get("models/ghost_red.g3dj", Model.class));
			character.transform.setTranslation(characterPositions[i]);
			instances.add(character);
		}

		return instances;
	}
	public static Array<ModelInstance> getGhostModels(AssetManager assetManager) {
		Array<ModelInstance> instances = new Array<ModelInstance>();
		for (int i =0; i < characterPositions.length; i++) {
			ModelInstance character = new ModelInstance(assetManager.get("models/ghost_red.g3dj", Model.class));
			character.transform.setTranslation(characterPositions[i]);
			instances.add(character);
		}
		return instances;
	}
	public static ModelInstance getSceneModel(AssetManager assetManager) {
		ModelInstance scene = new ModelInstance(assetManager.get("models/scene.g3dj", Model.class));
		scene.transform.setTranslation(0,0,0);
		return scene;
	}
	public static void setLights(Environment environment) {
//		travellingLight = new PointLight().set(new Color(0f,1f,0f,1f),6,1,6,1);
//		colorSwitchLight = new PointLight().set(colorSwitchColor,12,1,10,1);
		BaseLight[] sources = new BaseLight[pointShadowCasters.length];
		ShadowCaster[] shadowCasters = new ShadowCaster[pointShadowCasters.length];

		for (int i = 0; i < sources.length; i++) sources[i] = pointShadowCasters[i].light;
		for (int i = 0; i < sources.length; i++) shadowCasters[i] = pointShadowCasters[i];
//		environment.addShadowCasters(shadowCasters);
		environment.add(sources);
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
//		return environment;
	}
}
