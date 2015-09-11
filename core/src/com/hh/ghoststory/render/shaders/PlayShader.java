package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

/**
 * Created by nils on 8/24/15.
 */
public class PlayShader extends DefaultShader {
	public static final int textureNum = 4;
	private static final String vertex = Gdx.files.internal("shaders/default.vertex.glsl").readString();
	private static final String fragment = Gdx.files.internal("shaders/default.fragment.glsl").readString();


	public PlayShader(Renderable renderable) {
		super(renderable, new Config(vertex, fragment));
	}

	@Override
	public void begin(final Camera camera, final RenderContext context) {
		super.begin(camera, context);
		program.setUniformi("u_shadows", textureNum);
		program.setUniformf("u_screenWidth", Gdx.graphics.getWidth());
		program.setUniformf("u_screenHeight", Gdx.graphics.getHeight());
	}
}
