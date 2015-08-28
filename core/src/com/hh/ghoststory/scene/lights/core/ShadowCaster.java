package com.hh.ghoststory.scene.lights.core;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.environment.BaseLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.DepthMapShader;
import com.hh.ghoststory.lib.utility.ShaderUtil;

/**
 * Created by nils on 7/20/15.
 * @TODO move some methods to interface, make this implement the interface. Or, make this
 * extend baselight, then implement specific point/directional behavior again in PointShadowCaster, etc
 * replace lighting with this, and allow shadows and lights to be turned on and off.
 *
 * public abstract class Light extends BaseLight {...add camera and universal stuff here...}
 */
public abstract class ShadowCaster {
	public PerspectiveCamera camera = new PerspectiveCamera();
	public Vector3 position = new Vector3();
	public int depthmapsize = 4096;
	public boolean casting = true;
	public BaseLight light;

	public ShaderProgram shaderProgram = ShaderUtil.getShader("depth");
	public ModelBatch modelBatch = new ModelBatch(new DefaultShaderProvider() {
		@Override
		protected com.badlogic.gdx.graphics.g3d.Shader createShader(final Renderable renderable) {
			return new DepthMapShader(renderable, shaderProgram);
		}
	});

	public abstract void setupCamera();

	public abstract void render(Array<ModelInstance> instances);

	public void setPosition(Vector3 position) {
		this.position = position;
	}

	/**
	 * Add the uniforms to the scene shader
	 *
	 * @param sceneShaderProgram
	 */
	public abstract void applyToShader(ShaderProgram sceneShaderProgram);
}