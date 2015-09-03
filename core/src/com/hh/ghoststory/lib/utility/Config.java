package com.hh.ghoststory.lib.utility;

import com.badlogic.ashley.core.Engine;
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
import com.hh.ghoststory.components.*;
import com.hh.ghoststory.scene.lights.PointShadowCaster;
import com.hh.ghoststory.scene.lights.core.ShadowCaster;

/**
 * Created by nils on 7/15/15.
 */
public class Config {
    public Config() {

    }

    public void populateEntities(Engine engine) {
        engine.addEntity(scene);
        for (Entity actor : actors)
            engine.addEntity(actor);
        for (Entity light : lights)
            engine.addEntity(light);
    }
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

    // should come from read-in config
    public Entity scene = new Entity()
            .add(new SceneComponent())
            .add(new IDComponent().id("scene"))
            .add(new NameComponent().name("Development Scene"))
            .add(new PositionComponent().position(new Vector3(0, 0, 0)))
            .add(new GeometryComponent().file("scene.g3dj"))
		    .add(new InstanceComponent())
            .add(new RenderComponent())
            .add(new AmbientComponent().colorAttribute(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f)));
	// fake values that should have been pulled in through config
	// create all entities through the config.
	// loop through entities to add models and lights to the scene.
	// behaviors can be added too, but pc behavior doesn't need to be here (aside from spawn point).
	public Array<Entity> actors = new Array<Entity>() {
		{
			// ghost 1
			add(new Entity()
                    .add(new IDComponent().id("red_ghost"))
                    .add(new NameComponent().name("Red Ghost One"))
                    .add(new GeometryComponent().file("ghost_red.g3dj"))
                    .add(new InstanceComponent())
                    .add(new PositionComponent().position(new Vector3(5, 0, 5)))
                    .add(new AnimationComponent().animations(
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
                    )
                    .add(new RenderComponent())
            );
			// ghost 2
			add(new Entity()
                    .add(new IDComponent().id("red_ghost"))
                    .add(new NameComponent().name("Red Ghost Two"))
                    .add(new GeometryComponent().file("ghost_red.g3dj"))
                    .add(new InstanceComponent())
                    .add(new PositionComponent().position(new Vector3(0, 0, 0)))
                    .add(new AnimationComponent().animations(
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
                    )
                    .add(new RenderComponent())
            );
			// ghost 3
			add(new Entity()
                    .add(new IDComponent().id("red_ghost"))
                    .add(new NameComponent().name("Red Ghost Three"))
                    .add(new GeometryComponent().file("ghost_red.g3dj"))
                    .add(new InstanceComponent())
                    .add(new PositionComponent().position(new Vector3(10, 0, 6)))
                    .add(new AnimationComponent().animations(
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
                    )
                    .add(new RenderComponent())
            );
        }
    };
    public Array<Entity> lights = new Array<Entity>() {
        {
			// lights 1
			add(new Entity()
                .add(new IDComponent().id("light"))
                .add(new NameComponent().name("Light One"))
                .add(new PositionComponent().position(new Vector3(0, 5, 0)))
                .add(new ColorComponent().color(new Color(1f, 1f, 1f, 1f)))
                .add(new IntensityComponent().intensity(10))
                .add(new LightTypeComponent().type(LightTypeComponent.POINT))
                .add(new LightingComponent())
                .add(new ShadowCastingComponent())
			);
			// lights 2
			add(new Entity()
                .add(new IDComponent().id("light"))
                .add(new NameComponent().name("Light two"))
                .add(new PositionComponent().position(new Vector3(14, 6, 6)))
                .add(new ColorComponent().color(new Color(0.3f, 0.3f, 1f, 1f)))
                .add(new IntensityComponent().intensity(10))
                .add(new LightTypeComponent().type(LightTypeComponent.POINT))
			);
			// lights 3
			add(new Entity()
                .add(new IDComponent().id("light"))
                .add(new NameComponent().name("Light three"))
                .add(new PositionComponent().position(new Vector3(6, 5, 5)))
                .add(new ColorComponent().color(new Color(1f, 0.3f, 0.3f, 1f)))
                .add(new IntensityComponent().intensity(10))
                .add(new LightTypeComponent().type(LightTypeComponent.POINT))
			);
			// lights 4
			add(new Entity()
                .add(new IDComponent().id("light"))
                .add(new NameComponent().name("Light four"))
                .add(new PositionComponent().position(new Vector3(4, 5, 4)))
                .add(new ColorComponent().color(new Color(1f, 0f, 0f, 1f)))
                .add(new IntensityComponent().intensity(10))
                .add(new LightTypeComponent().type(LightTypeComponent.POINT))
			);
			// lights 5
			add(new Entity()
                .add(new IDComponent().id("light"))
                .add(new NameComponent().name("Light five"))
                .add(new PositionComponent().position(new Vector3(6, 20, 6)))
                .add(new ColorComponent().color(new Color(1f, 1f, 1f, 1f)))
                .add(new IntensityComponent().intensity(10))
                .add(new LightTypeComponent().type(LightTypeComponent.POINT))
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
