package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by nils on 12/13/15.
 */
public class CelColorShaderProgram extends ShaderProgram {
	// using the cel line shaders right now. cause color doesn't exist.
	public CelColorShaderProgram() {
		super(Gdx.files.internal("shaders/cel.line.vertex.glsl"), Gdx.files.internal("shaders/cel.line.fragment.glsl"));
	}
}
