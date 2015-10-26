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
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.hh.ghoststory.ScreenshotFactory;
import com.hh.ghoststory.lib.MessageTypes;
import com.hh.ghoststory.render.shaders.*;
import com.hh.ghoststory.scene.Lighting;
import com.hh.ghoststory.scene.lights.core.Caster;
import com.hh.ghoststory.screen.PlayScreen;

/**
 * Created by nils on 7/23/15.
 */
public class ShadowRenderer implements Telegraph, Disposable {
    public PlayScreen screen;
	public FrameBuffer frameBufferShadows;
	private FrameBuffer sceneBuffer;
	private FrameBuffer depthBuffer;
	private FrameBuffer edgeBuffer;
	public ModelBatch modelBatch;
	public ModelBatch modelBatchShadows;
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
			);
	private ShaderProgram addAlphaShader = new ShaderProgram(
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
		"#ifdef GL_ES\n" //
		+ "#define LOWP lowp\n" //
		+ "precision mediump float;\n" //
		+ "#else\n" //
		+ "#define LOWP \n" //
		+ "#endif\n" //
		+ "varying LOWP vec4 v_color;\n" //
		+ "varying vec2 v_texCoords;\n" //
		+ "uniform sampler2D u_texture;\n" //
		+ "void main()\n"//
		+ "{\n" //
		+ "  gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\n" //
		+ "  gl_FragColor.a = 1.0;\n" //
		+ "}");
	private Mesh quad;
	private SpriteBatch spriteBatch = new SpriteBatch();
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
		quad = createFullScreenQuad();
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
		frameBufferShadows.getColorBufferTexture().bind(PlayShader.textureNum);
	}

	public void renderScene(Camera camera, Array<ModelInstance> instances, Lighting environment) {
//		Gdx.gl.glClearColor(1, 1, 1, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		// capture scene in buffer. highlighted meshes will have an empty alpha.
		sceneBuffer.begin();
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		modelBatch.begin(camera);
		modelBatch.render(instances, environment);
		modelBatch.end();
//		ScreenshotFactory.saveScreenshot(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), "edge");
		sceneBuffer.end();

		tmpTexture = sceneBuffer.getColorBufferTexture();
		TextureRegion textureRegion = new TextureRegion(tmpTexture);
		textureRegion.flip(false, true);

		Gdx.gl.glClearColor(1, 0, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
		// draw the scene with alpha set back to 1.
		spriteBatch.setShader(addAlphaShader);
		spriteBatch.begin();
		spriteBatch.draw(textureRegion, 0, 0);
		spriteBatch.end();

		// get ready for the edges
		spriteBatch.setShader(edgeShader);
		spriteBatch.begin();
		// do the edge drawing overtop
		// set uniforms here, shader is bound when batch begins.
		edgeShader.setUniformf("u_screenWidth", Gdx.graphics.getWidth());
		edgeShader.setUniformf("u_screenHeight", Gdx.graphics.getHeight());
		spriteBatch.draw(textureRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch.end();
//		ScreenshotFactory.saveScreenshot(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), "edge");
	}

    public void initShadowBuffer(int width, int height) {
	    if (frameBufferShadows != null) frameBufferShadows.dispose();
            frameBufferShadows = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);

	    if (sceneBuffer != null) sceneBuffer.dispose();
            sceneBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);

	    if (edgeBuffer != null) edgeBuffer.dispose();
            edgeBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, false);

	    if (depthBuffer != null) depthBuffer.dispose();
            depthBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);

	    // this should prolly be a separate camera instead of the matrix. need this so outline stays in the right place
	    spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }
	public Mesh createFullScreenQuad(){
		float[] verts = new float[16];
		int i = 0;
		verts[i++] = -1.f; // x1
		verts[i++] = -1.f; // y1
		verts[i++] =  0.f; // u1
		verts[i++] =  0.f; // v1
		verts[i++] =  1.f; // x2
		verts[i++] = -1.f; // y2
		verts[i++] =  1.f; // u2
		verts[i++] =  0.f; // v2
		verts[i++] =  1.f; // x3
		verts[i++] =  1.f; // y2
		verts[i++] =  1.f; // u3
		verts[i++] =  1.f; // v3
		verts[i++] = -1.f; // x4
		verts[i++] =  1.f; // y4
		verts[i++] =  0.f; // u4
		verts[i++] =  1.f; // v4
		Mesh tmpMesh = new Mesh(true, 4, 0
			, new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position")
			, new VertexAttribute(VertexAttributes.Usage.TextureCoordinates
			, 2, "a_texCoord0"));
		tmpMesh.setVertices(verts);
		return tmpMesh;
	}

	public Mesh createFullScreenTri(){
		float[] verts = new float[12];
		int i = 0;
		verts[i++] = -1.f; // x1
		verts[i++] = -1.f; // y1
		verts[i++] =  0.f; // u1
		verts[i++] =  0.f; // v1
		verts[i++] = -1.f; // x2
		verts[i++] = -3.f; // y2
		verts[i++] =  0.f; // u2
		verts[i++] =  2.f; // v2
		verts[i++] =  3.f; // x3
		verts[i++] = -1.f; // y2
		verts[i++] =  2.f; // u3
		verts[i++] =  0.f; // v3

		Mesh tmpMesh = new Mesh(true, 3, 0
			, new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position")
			, new VertexAttribute(VertexAttributes.Usage.TextureCoordinates
			, 2, "a_texCoord0"));
		tmpMesh.setVertices(verts);
		return tmpMesh;
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