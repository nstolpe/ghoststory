package com.hh.ghoststory.entity;

import com.badlogic.ashley.core.ComponentMapper;
import com.hh.ghoststory.entity.components.*;

/**
 * Created by nils on 9/3/15.
 */
public class Mappers {
	// Lights
    public static final ComponentMapper<AmbientComponent> ambient = ComponentMapper.getFor(AmbientComponent.class);
	public static final ComponentMapper<PointLightComponent> pointLight = ComponentMapper.getFor(PointLightComponent.class);
	public static final ComponentMapper<SpotLightComponent> spotLight = ComponentMapper.getFor(SpotLightComponent.class);
	// the next three might be overkill
	public static final ComponentMapper<IntensityComponent> intensity = ComponentMapper.getFor(IntensityComponent.class);
	public static final ComponentMapper<LightTypeComponent> lightType = ComponentMapper.getFor(LightTypeComponent.class);
	public static final ComponentMapper<ColorComponent> color = ComponentMapper.getFor(ColorComponent.class);

	// Model
	public static final ComponentMapper<GeometryComponent> geometry = ComponentMapper.getFor(GeometryComponent.class);
	public static final ComponentMapper<InstanceComponent> instance = ComponentMapper.getFor(InstanceComponent.class);
	public static final ComponentMapper<BoundingBoxComponent> boundingBox = ComponentMapper.getFor(BoundingBoxComponent.class);

	// Transform
    public static final ComponentMapper<AnimationComponent> animation = ComponentMapper.getFor(AnimationComponent.class);
    public static final ComponentMapper<BehaviorComponent> behavior = ComponentMapper.getFor(BehaviorComponent.class);

	// Meta
    public static final ComponentMapper<IDComponent> id = ComponentMapper.getFor(IDComponent.class);
    public static final ComponentMapper<NameComponent> name = ComponentMapper.getFor(NameComponent.class);
    public static final ComponentMapper<MobComponent> mob = ComponentMapper.getFor(MobComponent.class);
    public static final ComponentMapper<PlayerComponent> player = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
    public static final ComponentMapper<RotationComponent> rotation = ComponentMapper.getFor(RotationComponent.class);
    public static final ComponentMapper<DirectionComponent> direction = ComponentMapper.getFor(DirectionComponent.class);
    public static final ComponentMapper<ShadowCastingComponent> shadowCasting = ComponentMapper.getFor(ShadowCastingComponent.class);

}