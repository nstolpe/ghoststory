package com.hh.ghoststory.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.ScreenshotFactory;

import java.nio.IntBuffer;

/**
 * Created by nils on 10/8/15.
 */
public class TestScreen extends AbstractScreen {
	private SpriteBatch spriteBatch = new SpriteBatch();
	private ModelBatch modelBatch = new ModelBatch();
	public ModelBatch outlineBatch = new ModelBatch(
		"attribute vec3 a_position;\n" +
		"\n" +
		"uniform mat4 u_worldTrans;\n" +
		"uniform mat4 u_projViewTrans;\n" +
		"\n" +
		"\n" +
		"void main() {\n" +
		"    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);\n" +
		"}",
		"#ifdef GL_ES \n" +
		"precision mediump float;\n" +
		"#endif\n" +
		"\n" +
		"\n" +
		"void main() {\n" +
		"    gl_FragColor = vec4(0.04, 0.28, 0.26, 1.0);\n" +
		"}"
	);

	private AssetManager assets = new AssetManager();
	private PerspectiveCamera mainCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	private OrthographicCamera spriteCamera = new OrthographicCamera();

	private FrameBuffer frameBuffer1;
	private FrameBuffer frameBuffer2;

	private Texture tmpTexture;
	private Array<ModelInstance> instances = new Array<ModelInstance>();

	private final CameraInputController camController;

	public TestScreen(GhostStory game) {
		super(game);
		initFBOS();
		assets.load("models/ghost_blue.g3dj", Model.class);
		assets.load("models/cube.g3dj", Model.class);
		mainCamera.position.set(5, 5, 5);
		mainCamera.lookAt(0, 0, 0);
		mainCamera.near = 1;
		mainCamera.far = 300;
		mainCamera.update();

		camController = new CameraInputController(mainCamera);
		Gdx.input.setInputProcessor(camController);
		Array<Array<Float>> output = new Array<Array<Float>>();
		Array<Float> values = new Array<Float>() {
			{
				add(0.0f); add(0.1f); add(0.2f); add(0.3f); add(0.4f); add(0.5f); add(0.6f); add(0.7f); add(0.8f); add(0.9f);
			}
		};
		perm(values, 3, new Array<Float>(), output);
		System.out.println(output.size);
	}
	public void perm(Array<Float> values, int size, Array<Float> initialStuff, Array<Array<Float>> output) {
		if (initialStuff.size>= size) {
			output.add(initialStuff);
			System.out.println(initialStuff);
		} else {
			Array<Float> tmp = new Array<Float>();
			for (int i = 0; i < values.size; ++i) {
				tmp.clear();
				tmp.addAll(initialStuff);
				tmp.add(values.get(i));
				perm(values, size, tmp, output);
			}
		}
	}
	@Override
	public void render(float delta) {
		mainCamera.update();

		if (assets.update() && instances.size == 0) {
			ModelInstance ghost = new ModelInstance(assets.get("models/ghost_blue.g3dj", Model.class));
			ghost.transform.translate(1.0f, 0.0f, 4.0f);
			ModelInstance cube = new ModelInstance(assets.get("models/cube.g3dj", Model.class));
			cube.transform.translate(2.0f, 0.0f, 2.0f);
			instances.add(ghost);
			instances.add(cube);
		}
		if (instances != null) {
			int mode = 1;
			switch (mode) {
				// multiple frame buffers
				case 0:
					frameBuffer1.begin();
Gdx.gl.glReadPixels();
					Gdx.gl.glClearColor(1, 0, 0, 1);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

					modelBatch.begin(mainCamera);
					modelBatch.render(instances);
					modelBatch.end();

					frameBuffer1.end();

					tmpTexture = frameBuffer1.getColorBufferTexture();

					spriteCamera.setToOrtho(false, frameBuffer1.getWidth(), frameBuffer1.getWidth());
					spriteBatch.setProjectionMatrix(spriteCamera.combined);
					spriteBatch.begin();

					Gdx.gl.glClearColor(0, 0, 1, 1);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

					spriteBatch.draw(tmpTexture, 0, 0, 200, 200);
					spriteBatch.end();
					break;
				// stencil
				case 1:
					Gdx.gl.glClearColor(1, 0, 0, 1);
//
//					Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
//					Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
//					Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 1);
//					Gdx.gl.glStencilMask(1);
//					Gdx.gl.glClearStencil(0);
//					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
//
//					modelBatch.begin(mainCamera);
//					modelBatch.render(instance);
//					modelBatch.end();
//
//					Gdx.gl.glStencilFunc(GL20.GL_NOTEQUAL, 0, 1);
//					Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_KEEP);
//					Gdx.gl.glStencilMask(0x00);
//
//					ModelInstance copy = instance.copy();
//					copy.transform.scl(1.1f);
//					outlineBatch.begin(mainCamera);
//					outlineBatch.render(copy);
//					outlineBatch.end();



					Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
					Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
					Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

					Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 0xFF);
					Gdx.gl.glStencilMask(0xFF);

					modelBatch.begin(mainCamera);
					for (ModelInstance instance : instances) modelBatch.render(instance);
					modelBatch.end();

					Gdx.gl.glStencilFunc(GL20.GL_NOTEQUAL, 1, 0xFF);
					Gdx.gl.glStencilMask(0x00);
					Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

					outlineBatch.begin(mainCamera);
					for (ModelInstance instance : instances) {
						instance.transform.scl(1.1f);
						outlineBatch.render(instance);
						instance.transform.scl(100f / 110f);
					}
					outlineBatch.end();

					Gdx.gl.glStencilMask(0xFF);
					Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);


//					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
//
//					Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
//					Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 1);
//					Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
//					Gdx.gl.glStencilMask(0x00);
//					Gdx.gl.glClear(GL20.GL_STENCIL_BUFFER_BIT);
//
//					modelBatch.begin(mainCamera);
//					modelBatch.render(instance);
//					modelBatch.end();
//
//					Gdx.gl.glStencilFunc(GL20.GL_EQUAL, 0, 1);
//					Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_KEEP);
//					Gdx.gl.glStencilMask(0x00);
//
//					instance.transform.scl(1.2f);
//					outlineBatch.begin(mainCamera);
//					outlineBatch.render(instance);
//					outlineBatch.end();
//					instance.transform.scl(100f / 120f);

//					Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
					break;
				default:
					break;
			}

		}
	}

	@Override
	public void resize(int width, int height) {
		initFBOS();
		mainCamera.position.set(mainCamera.position);
		mainCamera.viewportWidth = width;
		mainCamera.viewportHeight = height;
		mainCamera.update();
	}

	@Override
	public void dispose() {
		frameBuffer1.dispose();
		frameBuffer2.dispose();
		spriteBatch.dispose();
		modelBatch.dispose();
		tmpTexture.dispose();
		assets.dispose();
	}

	private void initFBOS() {
		if (frameBuffer1 != null) frameBuffer1.dispose();
			frameBuffer1 = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		if (frameBuffer2 != null) frameBuffer2.dispose();
			frameBuffer2 = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	}
}
