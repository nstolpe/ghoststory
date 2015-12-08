package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;

/**
 * Created by nils on 12/6/15.
 */
public class CelDepthShader extends DepthShader {
	public static class Config extends DepthShader.Config {
		private static String defaultVertexShader = Gdx.files.internal("shaders/cel.depth.vertex.glsl").readString();
		private static String defaultFragmentShader = Gdx.files.internal("shaders/cel.depth.fragment.glsl").readString();

		public Config () {
			super(defaultVertexShader, defaultFragmentShader);
			defaultCullFace = GL20.GL_BACK;
		}
	}

	public CelDepthShader(Renderable renderable) {
		super(renderable, new Config());
	}
}