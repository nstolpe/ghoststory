package com.hh.ghoststory.Renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.hh.ghoststory.Shaders.ShadowShader;
import com.hh.ghoststory.SimpleTextureShader;
import com.hh.ghoststory.Screens.DualCameraAbstractScreen;
import com.hh.ghoststory.ShadowCasters.ShadowCaster;
import com.hh.ghoststory.Utility.ShaderUtil;

/**
 * Created by nils on 7/23/15.
 */
public class ShadowRenderer {
    public DualCameraAbstractScreen screen;
	public FrameBuffer frameBufferShadows = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	public ShaderProgram shaderProgram = ShaderUtil.getShader("scene");
	public ShaderProgram newShaderProgram = ShaderUtil.getShader("new");
	public ModelBatch modelBatch = new ModelBatch(new DefaultShaderProvider() {
		@Override
		protected Shader createShader(final Renderable renderable) {
//			return new ShadowShader(renderable, shaderProgram);
            ShaderProgram.pedantic = false;
//			return new ShadowShader(renderable, new ShadowShader.Config());
            // use SimpleTextureShader to show shadows. shaderProgram needs to use scene though.
            return new SimpleTextureShader(renderable, shaderProgram);
		}
	});
	public ShaderProgram shaderProgramShadows = ShaderUtil.getShader("shadow");
	public ModelBatch  modelBatchShadows = new ModelBatch(new DefaultShaderProvider() {
		@Override
		protected Shader createShader(final Renderable renderable) {
			return new ShadowMapShader(renderable, shaderProgramShadows);
		}
	});

    public ShadowRenderer(DualCameraAbstractScreen screen) {
        this.screen = screen;
    }

    public void render() {
        Gdx.gl.glClearColor(screen.clearRed, screen.clearGreen, screen.clearBlue, screen.clearAlpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    for (int i = 0; i < screen.shadowCasters.size; i++) {
		    screen.shadowCasters.get(i).render(screen.instances);
	    }
	    renderShadows();
	    renderScene();
    }

	public void renderShadows() {
		frameBufferShadows.begin();

		Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 0.4f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatchShadows.begin(screen.camera);
		modelBatchShadows.render(screen.instances);
		modelBatchShadows.end();
		frameBufferShadows.end();
	}

	public void renderScene() {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
		shaderProgram.begin();
		final int textureNum = 4;
		frameBufferShadows.getColorBufferTexture().bind(textureNum);

		shaderProgram.setUniformi("u_shadows", textureNum);
		shaderProgram.setUniformf("u_screenWidth", Gdx.graphics.getWidth());
		shaderProgram.setUniformf("u_screenHeight", Gdx.graphics.getHeight());
		shaderProgram.end();

//		modelBatch = new ModelBatch(Gdx.files.internal("shaders/again.vertex.glsl"), Gdx.files.internal("shaders/again.fragment.glsl"));
		modelBatch.begin(screen.camera);
		modelBatch.render(screen.instances, screen.environment);
		modelBatch.end();
	}

    public void updateShadowBuffer() {
        frameBufferShadows.dispose();
        frameBufferShadows = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    /**
     * Shader used to render multiple shadows on the main scene.
     * This shader will render the scene multiple times, adding shadows for one light at a time
     */
    public class ShadowMapShader extends BaseShader {
        public Renderable renderable;

        @Override
        public void end() {
            super.end();
        }

        public ShadowMapShader(final Renderable renderable, final ShaderProgram program) {
            this.renderable = renderable;
            this.program = program;
            register(DefaultShader.Inputs.worldTrans, DefaultShader.Setters.worldTrans);
            register(DefaultShader.Inputs.projViewTrans, DefaultShader.Setters.projViewTrans);
            register(DefaultShader.Inputs.normalMatrix, DefaultShader.Setters.normalMatrix);
        }

        @Override
        public void begin(final Camera camera, final RenderContext context) {
            super.begin(camera, context);
            context.setDepthTest(GL20.GL_LEQUAL);
            context.setCullFace(GL20.GL_BACK);
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
            boolean firstCall = true;
            for (final ShadowCaster shadowCaster : screen.shadowCasters) {
	            shadowCaster.applyToShader(program);
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
                    renderable.mesh.render(program, renderable.primitiveType, renderable.meshPartOffset, renderable.meshPartSize, false);
                }
            }
        }
    }
}