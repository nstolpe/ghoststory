package com.hh.ghoststory.lib.utility;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.hh.ghoststory.entity.components.*;

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

    // should come from read-in config
    public Entity scene = new Entity()
            .add(new SceneComponent())
            .add(new IDComponent("scene"))
            .add(new NameComponent("Development Scene"))
            .add(new PositionComponent(new Vector3(0, 0, 0)))
            .add(new GeometryComponent().file("scene.g3dj"))
            .add(new RenderComponent())
            .add(new AmbientComponent().colorAttribute(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f)));
	// fake values that should have been pulled in through config
	// create all entities through the config.
	// loop through entities to add models and lights to the scene.
	// behaviors can be added too, but pc behavior doesn't need to be here (aside from spawn point).
	public Array<Entity> actors = new Array<Entity>() {
		{
            // player character
            add(new Entity()
                .add(new IDComponent("player_character"))
                .add(new NameComponent("Mr Player"))
                .add(new NameComponent("Mr Player"))
                .add(new GeometryComponent().file("ghost_orange.g3dj"))
                .add(new PositionComponent(new Vector3(-5, 0, -5)))
                .add(new RotationComponent(new Quaternion()))
                .add(new AnimationComponent(
                                new Array<ObjectMap<String, Object>>() {
                                    {
                                        add(new ObjectMap<String, Object>() {
                                            {
                                                put("id", "normal");
                                                put("offset", 0.0f);
                                                put("duration", -1.0f);
                                                put("loopcount", -1);
                                                put("speed", 1.0f);
                                                put("listener", null);
                                            }
                                        });
                                    }
                                })
                )
                .add(new RenderComponent())
                .add(new PlayerComponent())
            );
			// ghost 1
			add(new Entity()
                    .add(new IDComponent("red_ghost"))
                    .add(new NameComponent("Red Ghost One"))
                    .add(new GeometryComponent().file("ghost_red.g3dj"))
                    .add(new PositionComponent(new Vector3(5, 0, 5)))
                    .add(new RotationComponent(new Quaternion()))
                    .add(new AnimationComponent(
				                    new Array<ObjectMap<String, Object>>() {
					                    {
						                    add(new ObjectMap<String, Object>() {
							                    {
								                    put("id", "normal");
								                    put("offset", 0.0f);
								                    put("duration", -1.0f);
								                    put("loopcount", -1);
								                    put("speed", 1.0f);
								                    put("listener", null);
							                    }
						                    });
					                    }
				                    })
                    )
                    .add(new RenderComponent())
                    .add(new MobComponent())
            );
			// ghost 2
			add(new Entity()
                    .add(new IDComponent("red_ghost"))
                    .add(new NameComponent("Red Ghost Two"))
                    .add(new GeometryComponent().file("ghost_red.g3dj"))
                    .add(new PositionComponent(new Vector3(0, 0, 0)))
                    .add(new RotationComponent(new Quaternion()))
                    .add(new AnimationComponent(
				                    new Array<ObjectMap<String, Object>>() {
					                    {
						                    add(new ObjectMap<String, Object>() {
							                    {
								                    put("id", "normal");
								                    put("offset", 0.0f);
								                    put("duration", -1.0f);
								                    put("loopcount", -1);
								                    put("speed", 1.5f);
								                    put("listener", null);
							                    }
						                    });
					                    }
				                    })
                    )
                    .add(new RenderComponent())
                    .add(new MobComponent())
            );
			// ghost 3
			add(new Entity()
                    .add(new IDComponent("red_ghost"))
                    .add(new NameComponent("Red Ghost Three"))
                    .add(new GeometryComponent().file("ghost_red.g3dj"))
                    .add(new PositionComponent(new Vector3(10, 0, 6)))
                    .add(new RotationComponent(new Quaternion()))
                    .add(new AnimationComponent(
				                    new Array<ObjectMap<String, Object>>() {
					                    {
						                    add(new ObjectMap<String, Object>() {
							                    {
								                    put("id", "normal");
								                    put("offset", 0.0f);
								                    put("duration", -1.0f);
								                    put("loopcount", -1);
								                    put("speed", 0.5f);
								                    put("listener", null);
							                    }
						                    });
					                    }
				                    })
                    )
                    .add(new RenderComponent())
                    .add(new MobComponent())
            );
        }
    };
    public Array<Entity> lights = new Array<Entity>() {
        {
			// lights 1
			add(new Entity()
                .add(new IDComponent("light"))
                .add(new NameComponent("Light One"))
                .add(new PositionComponent(new Vector3(0, 5, 0)))
                .add(new ColorComponent(new Color(1f, 1f, 1f, 1f)))
                .add(new IntensityComponent(10))
                .add(new LightTypeComponent(LightTypeComponent.POINT))
                .add(new ShadowCastingComponent())
			);
			// lights 2
			add(new Entity()
                .add(new IDComponent("light"))
                .add(new NameComponent("Light two"))
                .add(new PositionComponent(new Vector3(14, 6, 6)))
                .add(new ColorComponent(new Color(0.3f, 0.3f, 1f, 1f)))
                .add(new IntensityComponent(10))
                .add(new LightTypeComponent(LightTypeComponent.POINT))
                .add(new ShadowCastingComponent())
			);
			// lights 3
			add(new Entity()
                .add(new IDComponent("light"))
                .add(new NameComponent("Light three"))
                .add(new PositionComponent(new Vector3(6, 5, 5)))
                .add(new ColorComponent(new Color(1f, 0.3f, 0.3f, 1f)))
                .add(new IntensityComponent(10))
                .add(new LightTypeComponent(LightTypeComponent.POINT))
//                .add(new ShadowCastingComponent())
			);
			// lights 4
			add(new Entity()
                .add(new IDComponent("light"))
                .add(new NameComponent("Light four"))
                .add(new PositionComponent(new Vector3(4, 5, 4)))
                .add(new ColorComponent(new Color(1f, 0f, 0f, 1f)))
                .add(new IntensityComponent(10))
                .add(new LightTypeComponent(LightTypeComponent.POINT))
//                .add(new ShadowCastingComponent())
			);
			// lights 5
			add(new Entity()
                .add(new IDComponent("light"))
                .add(new NameComponent("Light five"))
                .add(new PositionComponent(new Vector3(6, 20, 6)))
                .add(new ColorComponent(new Color(1f, 1f, 1f, 1f)))
                .add(new IntensityComponent(10))
                .add(new LightTypeComponent(LightTypeComponent.POINT))
//                .add(new ShadowCastingComponent())
			);
		}
	};
}
