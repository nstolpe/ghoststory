package com.hh.ghoststory;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Inputs;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Setters;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Simple shader for an object with a texture
 */
public class SimpleTextureShader extends BaseShader
{
	public Renderable	renderable;

	@Override
	public void end()
	{
		super.end();
	}

	public SimpleTextureShader(final Renderable renderable, final ShaderProgram shaderProgram) {
		this.renderable = renderable;
		this.program = shaderProgram;
		register(Inputs.worldTrans, Setters.worldTrans);
		register(Inputs.projViewTrans, Setters.projViewTrans);
		register(Inputs.normalMatrix, Setters.normalMatrix);
		register(Inputs.diffuseTexture, Setters.diffuseTexture);
		register(Inputs.diffuseColor, Setters.diffuseColor);
		register(Inputs.diffuseUVTransform, Setters.diffuseUVTransform);
		register(Inputs.specularColor, Setters.specularColor);
//		register(Inputs.ambientCube, new Setters.ACubemap(config.numDirectionalLights, config.numPointLights))
		register(Inputs.ambientCube, new Setters.ACubemap(1, 1));

	}

	@Override
	public void begin(final Camera camera, final RenderContext context) {
		super.begin(camera, context);
		context.setDepthTest(GL20.GL_LEQUAL);
		context.setCullFace(GL20.GL_BACK);

	}

// This might be ok to take out, nothing seems to have died
// Lighting may work better, original does something w/ lights
//	@Override
//	public void render(final Renderable renderable) {
//		if (!renderable.material.has(BlendingAttribute.Type)) {
//			context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//		} else {
//			context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
//		}
//		super.render(renderable);
//	}

	@Override
	public void init() {
		final ShaderProgram program = this.program;
		this.program = null;
		init(program, renderable);
		renderable = null;
	}

	@Override
	public int compareTo(final Shader other) {
		return 0;
	}

	@Override
	public boolean canRender(final Renderable instance) {
		return true;
	}

	@Override
	public void render(final Renderable renderable, final Attributes combinedAttributes) {
		super.render(renderable, combinedAttributes);
	}

}
