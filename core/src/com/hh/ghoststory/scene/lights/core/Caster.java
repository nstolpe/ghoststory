package com.hh.ghoststory.scene.lights.core;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;

/**
 * Created by nils on 9/3/15.
 */
public interface Caster {
	public void initCamera();
	public void applyToShader(final ShaderProgram shaderProgram);
	public void initFrameBuffer();
	public void render(Array<ModelInstance> instances);
    public void dispose();
}
