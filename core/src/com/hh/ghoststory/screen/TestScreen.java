package com.hh.ghoststory.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.ScreenshotFactory;
import sun.java2d.pipe.OutlineTextRenderer;

import java.nio.ByteBuffer;

/**
 * Created by nils on 10/8/15.
 */
public class TestScreen extends AbstractScreen {
	private SpriteBatch spriteBatch = new SpriteBatch();
	private ModelBatch modelBatch = new ModelBatch();
	public ModelBatch overlayBatch = new ModelBatch(
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
		"    gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);\n" +
		"}"
	);
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
	private AssetManager assets = new AssetManager();
	private PerspectiveCamera mainCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	private OrthographicCamera spriteCamera = new OrthographicCamera();

	private FrameBuffer frameBuffer1;
	private FrameBuffer frameBuffer2;

	private Texture tmpTexture;
	private Array<ModelInstance> instances = new Array<ModelInstance>();

	private final CameraInputController camController;
	private TextureRegion tmpTextureRegion;

	private int activeStencilIndex = 0;

	public TestScreen(GhostStory game) {
		super(game);
		initFBOS();
		assets.load("models/ghost_blue.g3dj", Model.class);
		assets.load("models/cube.g3dj", Model.class);
		mainCamera.position.set(5, 5, 5);
		mainCamera.lookAt(0, 0, 0);
		mainCamera.near = 1;
		mainCamera.far = 1000;
		mainCamera.update();

		camController = new CameraInputController(mainCamera) {
			@Override
			public boolean touchDown (int screenX, int screenY, int pointer, int button) {
				TestScreen.this.getObject(screenX, screenY);
				return super.touchDown(screenX, screenY, pointer, button);
			}
		};
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

	private void getObject(int screenX, int screenY) {
		ByteBuffer pixels = ByteBuffer.allocateDirect(8);
		Gdx.gl.glReadPixels(screenX, Gdx.graphics.getHeight() - screenY, 1, 1, GL20.GL_STENCIL_INDEX, GL20.GL_UNSIGNED_INT, pixels);
		int stencilIndex = (int) pixels.get();
		System.out.println("Stencil: " + (stencilIndex < 0 ? stencilIndex + 256 : stencilIndex)); //-128 to 127. 0 is clear

		for (int i = 0; i < instances.size; i++) {
			StencilIndexAttribute stencilAttr = (StencilIndexAttribute) instances.get(i).getMaterial("skin").get(StencilIndexAttribute.ID);
			SelectOutlineAttribute outlineAttr = (SelectOutlineAttribute ) instances.get(i).getMaterial("skin").get(SelectOutlineAttribute.ID);
			activeStencilIndex = stencilIndex;
			if (stencilAttr.value == stencilIndex)
				outlineAttr.value = true;
			else
				outlineAttr.value = false;

		}
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
		camController.update();

		if (assets.update() && instances.size == 0) {
			ModelInstance ghost = new ModelInstance(assets.get("models/ghost_blue.g3dj", Model.class));
			ghost.transform.translate(1.0f, 0.0f, 4.0f);
			ghost.getMaterial("skin").set(new StencilIndexAttribute(1));
			ghost.getMaterial("skin").set(new SelectOutlineAttribute(false));
			ModelInstance cube = new ModelInstance(assets.get("models/cube.g3dj", Model.class));
			cube.transform.translate(2.0f, 0.0f, 2.0f);
			cube.getMaterial("skin").set(new StencilIndexAttribute(2));
			cube.getMaterial("skin").set(new SelectOutlineAttribute(false));

			instances.add(ghost);
			instances.add(cube);
		}
		if (instances != null) {
			int mode = 0;
			switch (mode) {
				// multiple frame buff
				// ers
				case 0:
					// enable stencil test. clear color, stencil, depth
					Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
					Gdx.gl.glClearColor(1, 1, 1, 1);
					Gdx.gl.glClearStencil(0x00);

					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

					// set stencil to replace only when depth and stencil pass
					Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);

					// render the instances. is it ok to begin and end the ModelBach for each object?
					// it doesn't work w/o cause you need to modify glStencilFunc and can only do that
					// correctly outside of the batch.
					for (int i = 0; i < instances.size; i++) {
						StencilIndexAttribute stencilAttr = (StencilIndexAttribute) instances.get(i).getMaterial("skin").get(StencilIndexAttribute.ID);
						// set stencil buffer to write the StencilIndexAttribute to the stencil buffer.
						Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, stencilAttr.value, 0xFF);
						// render the scene and write the stencil buffer
						modelBatch.begin(mainCamera);
							modelBatch.render(instances.get(i));
						modelBatch.end();
					}

					if (activeStencilIndex > 0) {
						// don't update the stencil buffer anymore.
						Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_KEEP);
						// only write when stencil buffer = 1 (1st instance, ghost). this should change on touch. parameterize
						Gdx.gl.glStencilFunc(GL20.GL_EQUAL, activeStencilIndex, 0xff);

						// start fbo to get draw the selected silhouette
						frameBuffer1.begin();
						Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
						// overlayBatch writes a solid color. change name.
						overlayBatch.begin(mainCamera);
						overlayBatch.render(instances);
						overlayBatch.end();
//						ScreenshotFactory.saveScreenshot(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), "stuff");
						frameBuffer1.end();

						// flip the frame buffer output since it's upside down.
						tmpTexture = frameBuffer1.getColorBufferTexture();
						tmpTextureRegion = new TextureRegion(tmpTexture);
						tmpTextureRegion.flip(false, true);

						edgeShader.setUniformf("u_screenWidth", Gdx.graphics.getWidth());
						edgeShader.setUniformf("u_screenHeight", Gdx.graphics.getHeight());

						spriteBatch.setShader(edgeShader);
						spriteBatch.begin();
						spriteBatch.draw(tmpTextureRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
						spriteBatch.end();

//					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
						// draw the silhouette w/ a sprite batch.
						spriteBatch.setShader(SpriteBatch.createDefaultShader());
						spriteBatch.begin();
						spriteBatch.draw(tmpTextureRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
						spriteBatch.end();
					}
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
//					overlayBatch.begin(mainCamera);
//					overlayBatch.render(copy);
//					overlayBatch.end();



					Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
					Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);
					Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
					Gdx.gl.glClearStencil(0x00);
//					Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 0xFF);
					Gdx.gl.glStencilMask(0xFF);

					modelBatch.begin(mainCamera);
					for (int i = 0; i < instances.size; i++) {
						Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, i + 1, -1);
						modelBatch.render(instances.get(i));
					}
					modelBatch.end();

					Gdx.gl.glStencilFunc(GL20.GL_NOTEQUAL, 1, 0xFF);

					Gdx.gl.glStencilMask(0x00);
					Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);

					overlayBatch.begin(mainCamera);
					for (ModelInstance instance : instances) {
						instance.transform.scl(1.1f);
						overlayBatch.render(instance);
						instance.transform.scl(100f / 110f);
					}
					overlayBatch.end();

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
//					overlayBatch.begin(mainCamera);
//					overlayBatch.render(instance);
//					overlayBatch.end();
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

		spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	}

	public static class StencilIndexAttribute extends Attribute {

		public final static String Alias = "StencilIndex";
		public final static long ID = register(Alias);

		public int value;

		public StencilIndexAttribute (final int value) {
			super(ID);
			this.value = value;
		}

		@Override
		public Attribute copy () {
			return new StencilIndexAttribute(value);
		}

		@Override
		protected boolean equals (Attribute other) {
			return ((StencilIndexAttribute)other).value == value;
		}

		@Override
		public int compareTo (Attribute o) {
			if (type != o.type) return type < o.type ? -1 : 1;
			int otherValue = ((StencilIndexAttribute)o).value;
			return value == otherValue ? 0 : (value < otherValue ? -1 : 1);
		}
	}

	public static class SelectOutlineAttribute extends Attribute {

		public final static String Alias = "SelectOutline";
		public final static long ID = register(Alias);

		public boolean value;

		public SelectOutlineAttribute (final boolean value) {
			super(ID);
			this.value = value;
		}

		@Override
		public Attribute copy () {
			return new SelectOutlineAttribute(value);
		}

		@Override
		protected boolean equals (Attribute other) {
			return ((SelectOutlineAttribute)other).value == value;
		}

		@Override
		public int compareTo (Attribute o) {
			if (type != o.type) return type < o.type ? -1 : 1;
			boolean otherValue = ((SelectOutlineAttribute)o).value;
			return value == otherValue ? 0 : (value ? 1 : -1);
		}
	}
}
