package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

/**
 * Created by nils on 10/8/15.
 */
public class EdgeShader extends DefaultShader {
	public EdgeShader(Renderable renderable) {
		super(renderable);
	}

	@Override
	public void begin(final Camera camera, final RenderContext context) {
//		program.setUniformi("tex", textureNum);
//		program.setUniformf("width", Gdx.graphics.getWidth());
//		program.setUniformf("height", Gdx.graphics.getHeight());
	}
}
