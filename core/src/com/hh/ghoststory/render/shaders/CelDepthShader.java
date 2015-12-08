package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

/**
 * Created by nils on 12/6/15.
 */
public class CelDepthShader extends DepthShader {
	public final int u_near = register("u_near");
	public final int u_far = register("u_far");

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

	@Override
	public void begin (Camera camera, RenderContext context) {
		super.begin(camera, context);
		set(u_near, camera.near);
		set(u_far, camera.far);
	}
}