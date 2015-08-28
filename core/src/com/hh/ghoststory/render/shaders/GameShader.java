package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

/**
 * Created by nils on 8/24/15.
 */
public class GameShader extends DefaultShader {
	public static final int textureNum = 4;

	public GameShader(Renderable renderable) {
		super(
			renderable,
			new Config(
				Gdx.files.internal("shaders/default.vertex.glsl").readString(),
				Gdx.files.internal("shaders/default.fragment.glsl").readString()
			)
		);
	}

	@Override
	public void begin(final Camera camera, final RenderContext context) {
		super.begin(camera, context);
		program.setUniformi("u_shadows", textureNum);
		program.setUniformf("u_screenWidth", Gdx.graphics.getWidth());
		program.setUniformf("u_screenHeight", Gdx.graphics.getHeight());
	}
}