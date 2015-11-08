package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;

/**
 * Created by nils on 11/8/15.
 */
public class LocationShaderProvider extends DefaultShaderProvider {
		@Override
		protected Shader createShader(final Renderable renderable) {
			return new LocationShader(renderable);
		}
	}
