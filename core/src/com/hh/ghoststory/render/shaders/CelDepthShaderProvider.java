package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;

/**
 * Created by nils on 12/6/15.
 */
public class CelDepthShaderProvider extends BaseShaderProvider {
	@Override
	protected Shader createShader(final Renderable renderable) {
		return new CelDepthShader(renderable);
	}
}