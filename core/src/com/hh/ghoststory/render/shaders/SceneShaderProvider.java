package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;

/**
 * Created by nils on 8/28/15.
 */
public class SceneShaderProvider extends DefaultShaderProvider {
	@Override
	protected Shader createShader(final Renderable renderable) {
		return new SceneShader(renderable);
	}
}
