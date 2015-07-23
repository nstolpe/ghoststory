package com.hh.ghoststory.shadowcaster;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.DepthMapShader;
import com.hh.ghoststory.utility.Shader;

/**
 * Created by nils on 7/20/15.
 * @TODO move some methods to interface, make this implement the interface.
 */
public abstract class AbstractShadowCaster {
	public PerspectiveCamera camera = new PerspectiveCamera();
	public Vector3 position = new Vector3();
	public int depthmapsize = 1024;
	public boolean casting = true;

	public ShaderProgram shaderProgram = Shader.getShader("depth");
	public ModelBatch modelBatch = new ModelBatch(new DefaultShaderProvider() {
		@Override
		protected com.badlogic.gdx.graphics.g3d.Shader createShader(final Renderable renderable) {
			return new DepthMapShader(renderable, shaderProgram);
		}
	});

	public abstract void setupCamera();

	public abstract void render(Array<ModelInstance> instances, Environment environment);

	public void setPosition(Vector3 position) {
		this.position = position;
	}
}