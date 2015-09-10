package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.scene.lights.core.Caster;

/**
 * Created by nils on 8/28/15.
 */
public class ShadowMapShaderProvider extends DefaultShaderProvider {
	Array<Caster> shadowCasters;

	public ShadowMapShaderProvider(Array<Caster> shadowCasters) {
		super();
		this.shadowCasters = shadowCasters;
	}
	@Override
	protected Shader createShader(final Renderable renderable) {
		return new ShadowMapShader(renderable, shadowCasters);
	}
}
