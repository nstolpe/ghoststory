package com.hh.ghoststory.render.shaders;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.entity.Mappers;
import com.hh.ghoststory.entity.components.LightComponent;
import com.hh.ghoststory.lib.utility.Config;
import com.hh.ghoststory.scene.lights.core.Caster;

/**
 * Created by nils on 8/28/15.
 */
public class ShadowMapShaderProvider extends DefaultShaderProvider {
	Array<Caster> casters = new Array<Caster>();

	public ShadowMapShaderProvider() {
		super();
	}
	@Override
	protected Shader createShader(final Renderable renderable) {
		ImmutableArray<Entity> lights = Config.engine.getEntitiesFor(Family.all(LightComponent.class).get());
		for (Entity light : lights) {
			if (Mappers.light.get(light).shadowing) casters.add(Mappers.light.get(light).caster);
		}
		return new ShadowMapShader(renderable, casters);
	}
}
