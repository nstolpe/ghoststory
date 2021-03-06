package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.ScreenshotFactory;
import com.hh.ghoststory.scene.lights.core.Caster;

/**
 * Created by nils on 8/25/15.
 * Shader used to render multiple shadows on the main scene.
 * This shader will render the scene multiple times, adding shadows for one light at a time
 *
 * Needs access to `screen` so it's here. Should be moved somewhere else.
 */
public class ShadowMapShader extends BaseShader {
	private final Array<Caster> casters;
	public Renderable renderable;

	public ShadowMapShader(final Renderable renderable, Array<Caster> casters) {
		this.renderable = renderable;
		program = new ShaderProgram(
			Gdx.files.internal("shaders/shadow.vertex.glsl"),
			Gdx.files.internal("shaders/shadow.fragment.glsl")
		);
		this.casters = casters;
		register(DefaultShader.Inputs.worldTrans, DefaultShader.Setters.worldTrans);
		register(DefaultShader.Inputs.projViewTrans, DefaultShader.Setters.projViewTrans);
		register(DefaultShader.Inputs.normalMatrix, DefaultShader.Setters.normalMatrix);

	}

	@Override
	public void init() {
		final ShaderProgram program = this.program;
		this.program = null;
		init(program, renderable);
		renderable = null;
	}

	@Override
	public void begin(final Camera camera, final RenderContext context) {
		super.begin(camera, context);
		context.setDepthTest(GL20.GL_LEQUAL);
		context.setCullFace(GL20.GL_BACK);
	}

	@Override
	public void end() {
		super.end();
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
	public void render(final Renderable renderable) {
		if (!renderable.material.has(BlendingAttribute.Type))
			context.setBlending(false, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		else
			context.setBlending(true, GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

		super.render(renderable);
	}

	@Override
	public void render(final Renderable renderable, final Attributes combinedAttributes) {
		boolean firstCall = true;
		for (final Caster caster : casters) {
			caster.applyToShader(program);
			if (firstCall) {
				// Classic depth test
				context.setDepthTest(GL20.GL_LEQUAL);
				// Deactivate blending on first pass
				context.setBlending(false, GL20.GL_ONE, GL20.GL_ONE);
				super.render(renderable, combinedAttributes);
				firstCall = false;
			} else {
				// We could use the classic depth test (less or equal), but strict equality works fine on next passes as depth buffer already contains our scene
				context.setDepthTest(GL20.GL_EQUAL);
				// Activate additive blending
				context.setBlending(true, GL20.GL_ONE, GL20.GL_ONE);
				// Render the mesh again
				renderable.meshPart.render(program);
			}
		}
	}
}
