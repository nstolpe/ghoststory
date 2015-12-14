package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * 2D shader program. Draws cel lines based on differences in depth.
 */
public class CelLineShaderProgram extends ShaderProgram {
	public CelLineShaderProgram() {
		super(Gdx.files.internal("shaders/cel.line.vertex.glsl"), Gdx.files.internal("shaders/cel.line.fragment.glsl"));
	}

	/**
	 * Sets the u_size uniform. This should be configurable instead of relying on Gdx.graphics all the time.
	 */
	@Override
	public void begin() {
		super.begin();
		setUniformf("u_size", Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 4);
	}
}
