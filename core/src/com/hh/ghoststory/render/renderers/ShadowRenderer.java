package com.hh.ghoststory.render.renderers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.hh.ghoststory.ScreenshotFactory;
import com.hh.ghoststory.lib.MessageTypes;
import com.hh.ghoststory.render.shaders.PlayShader;
import com.hh.ghoststory.render.shaders.PlayShaderProvider;
import com.hh.ghoststory.render.shaders.ShadowMapShaderProvider;
import com.hh.ghoststory.scene.Lighting;
import com.hh.ghoststory.scene.lights.core.Caster;
import com.hh.ghoststory.screen.PlayScreen;

/**
 * Created by nils on 7/23/15.
 */
public class ShadowRenderer implements Telegraph, Disposable {
    public PlayScreen screen;
	public FrameBuffer frameBufferShadows;
	private FrameBuffer outlinedBuffer;
	private FrameBuffer edgeBuffer;
	private OrthographicCamera edgeCamera = new OrthographicCamera();
	public ModelBatch modelBatch;
	public ModelBatch modelBatchShadows;
	private ModelBatch barBatch = new ModelBatch(
		Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/shadow/system/classical/main.vertex.glsl").readString(),
		"void main()\n" +
		"{\n" +
		"    gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);\n" +
		"}");
	private ShaderProgram edgeShader = new ShaderProgram(
			"attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
					+ "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
					+ "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
					+ "uniform mat4 u_projTrans;\n" //
					+ "varying vec4 v_color;\n" //
					+ "varying vec2 v_texCoords;\n" //
					+ "\n" //
					+ "void main()\n" //
					+ "{\n" //
					+ "   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" //
					+ "   v_color.a = v_color.a * (255.0/254.0);\n" //
					+ "   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" //
					+ "   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" //
					+ "}\n",
			Gdx.files.internal("shaders/edge.fragment.glsl").readString()
//			Gdx.files.classpath("com/hh/ghoststory/render/shaders/edge.fragment.glsl").readString()
			);
	private SpriteBatch edgeBatch = new SpriteBatch();
	private MessageDispatcher frameworkDispatcher;
	private Texture tmpTexture;

	public ShadowRenderer(PlayScreen screen) {
	    this(screen, screen.frameworkDispatcher);
    }

    public ShadowRenderer(PlayScreen screen, MessageDispatcher frameworkDispatcher) {
        this.screen = screen;
	    this.frameworkDispatcher = frameworkDispatcher;
		frameworkDispatcher.addListener(this, MessageTypes.Framework.INIT_SHADOW_BUFFER);
		modelBatch = new ModelBatch(new PlayShaderProvider());
		modelBatchShadows = new ModelBatch(new ShadowMapShaderProvider());
		initShadowBuffer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void render(Camera camera, Array<ModelInstance> instances, Array<Caster> casters, Lighting environment) {
	    // @TODO next two from Screen. Maybe pass in or set width for the Renderer, as well as clear colors?
	    // Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
	    Gdx.gl.glClearColor(screen.clearRed, screen.clearGreen, screen.clearBlue, screen.clearAlpha);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

	    renderDepth(casters, instances);
	    renderShadows(camera, instances);
	    renderScene(camera, instances, environment);
    }
	public void renderDepth(Array<Caster> caster, Array<ModelInstance> instances) {
		for (int i = 0; i < caster.size; i++)
			caster.get(i).render(instances);
	}

	public void renderShadows(Camera camera, Array<ModelInstance> instances) {
		frameBufferShadows.begin();

		Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 0.4f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatchShadows.begin(camera);
		modelBatchShadows.render(instances);
		modelBatchShadows.end();
//		ScreenshotFactory.saveScreenshot(frameBufferShadows.getWidth(), frameBufferShadows.getHeight(), "shadows");
		frameBufferShadows.end();
	}

	public void renderScene(Camera camera, Array<ModelInstance> instances, Lighting environment) {
//		Gdx.gl.glClearColor(1, 1, 1, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
		if (instances.size >= 4) {
			outlinedBuffer.begin();
			Gdx.gl.glClearColor(1, 0, 1, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			barBatch.begin(camera);
			barBatch.render(instances.get(3));
			barBatch.end();
//			ScreenshotFactory.saveScreenshot(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), "edge");
			outlinedBuffer.end();
		}

		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		frameBufferShadows.getColorBufferTexture().bind(PlayShader.textureNum);
		modelBatch.begin(camera);
		for (ModelInstance instance : instances) {
			if (instances.size >= 4) {
				if (instance == instances.get(3)) PlayShader.alphaInstance = instance;
			}
			modelBatch.render(instance, environment);
		}
//		modelBatch.render(instances, environment);
		modelBatch.end();

		if (instances.size >= 4) {
			tmpTexture = outlinedBuffer.getColorBufferTexture();

			TextureRegion textureRegion = new TextureRegion(tmpTexture);
			textureRegion.flip(false, true);

//			edgeBuffer.begin();
//			Gdx.gl.glClearColor(1, 0, 0, 1);
//			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


			edgeBatch.setShader(edgeShader);

			edgeBatch.begin();
			// set uniforms here, shader is bound when batch begins.
			edgeShader.setUniformf("u_screenWidth", edgeBuffer.getWidth());
			edgeShader.setUniformf("u_screenHeight", edgeBuffer.getHeight());
			edgeBatch.draw(textureRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			edgeBatch.end();
//			ScreenshotFactory.saveScreenshot(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), "edge");
//			edgeBuffer.end();
		}


	}

    public void initShadowBuffer(int width, int height) {
	    if (frameBufferShadows != null) frameBufferShadows.dispose();
            frameBufferShadows = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);

	    if (outlinedBuffer != null) outlinedBuffer.dispose();
            outlinedBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);

	    if (edgeBuffer != null) edgeBuffer.dispose();
            edgeBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);

	    // this should prolly be a separate camera instead of the matrix. need this so outline stays in the right place
	    edgeBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

	@Override
	public void dispose() {
		modelBatch.dispose();
		modelBatchShadows.dispose();
		frameBufferShadows.dispose();
	}

	@Override
	public boolean handleMessage(Telegram msg) {
		switch (msg.message) {
			case MessageTypes.Framework.INIT_SHADOW_BUFFER:
				initShadowBuffer(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
				break;
			default:
				break;
		}
		// should also be able to return false.
		return true;
	}

}