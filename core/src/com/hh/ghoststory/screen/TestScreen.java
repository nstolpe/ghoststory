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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.ScreenshotFactory;
import com.hh.ghoststory.render.shaders.LocationShader;
import com.hh.ghoststory.render.shaders.LocationShaderProvider;

import java.nio.ByteBuffer;

/**
 * Created by nils on 10/8/15.
 */
public class TestScreen extends AbstractScreen {
	private SpriteBatch spriteBatch = new SpriteBatch();
	private ModelBatch modelBatch = new ModelBatch();
	public ModelBatch locationBatch = new ModelBatch(new LocationShaderProvider());
//	public ModelBatch locationBatch = new ModelBatch(
//		"attribute vec3 a_position;\n" +
//		"\n" +
//		"uniform mat4 u_worldTrans;\n" +
//		"uniform mat4 u_projViewTrans;\n" +
//		"\n" +
//		"\n" +
//		"void main() {\n" +
//		"    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);\n" +
//		"}",
//		"#ifdef GL_ES \n" +
//		"precision mediump float;\n" +
//		"#endif\n" +
//		"\n" +
//		"\n" +
//		"void main() {\n" +
//		"    gl_FragColor = vec4(1.0, 0.0, 0.0, 0.5);\n" +
//		"}"
//	);
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

	private FrameBuffer locationBuffer;
	private FrameBuffer frameBuffer2;
	private FrameBuffer frameBuffer3;

	private Array<ModelInstance> instances = new Array<ModelInstance>();

	private final CameraInputController camController;
	private Texture tmpTexture;
	private TextureRegion tmpTextureRegion;
	private Texture tmpTexture2;
	private TextureRegion tmpTextureRegion2;
	private Texture tmpTexture3;
	private TextureRegion tmpTextureRegion3;
	private int activeStencilIndex = 0;
	private static TextureRegion tmpRegion;
	private Array<Array<Float>> alphas = new Array<Array<Float>>();


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
		Array<Float> values = new Array<Float>() {
			{
				add(0.0f); add(0.1f); add(0.2f); add(0.3f); add(0.4f); add(0.5f); add(0.6f); add(0.7f); add(0.8f); add(0.9f);
			}
		};
		permutations(values, 3, new Array<Float>(), alphas);
	}

	private void getObject(int screenX, int screenY) {
		ByteBuffer pixels = ByteBuffer.allocateDirect(8);
		Gdx.gl.glReadPixels(screenX, Gdx.graphics.getHeight() - screenY, 1, 1, GL20.GL_STENCIL_INDEX, GL20.GL_UNSIGNED_INT, pixels);
		int stencilIndex = (int) pixels.get();
		System.out.println("Stencil: " + stencilIndex + " " + (stencilIndex < 0 ? stencilIndex + 256 : stencilIndex)); //-128 to 127. 0 is clear

		for (int i = 0; i < instances.size; i++) {
			SelectableAttribute stencilAttr = (SelectableAttribute) instances.get(i).getMaterial("skin").get(SelectableAttribute.ID);
			activeStencilIndex = stencilIndex;
		}
	}

	/**
	 * Fills an array with `values.size` permutations covering `size` values.
	 * `output` will be modified and hold the permutations.
	 * `passes` is disposable.
	 *
	 * Array<Array<Float>> output = new Array<Array<Float>>();
	 * Array<Float> values = new Array<Float>() {
	 *     {
	 *         add(0.0f); add(0.1f); add(0.2f); add(0.3f); add(0.4f); add(0.5f); add(0.6f); add(0.7f); add(0.8f); add(0.9f);
	 *     }
	 * };
	 * permutations(values, 3, new Array<Float>(), output);
	 */
	public void permutations(Array<Float> values, int size, Array<Float> passes, Array<Array<Float>> output) {
		if (passes.size >= size) {
			output.add(new Array(passes));
			System.out.println(passes.items);
		} else {
			Array<Float> tmp = new Array<Float>();
			for (int i = 0; i < values.size; ++i) {
				tmp.clear();
				tmp.addAll(passes);
				tmp.add(values.get(i));
				permutations(values, size, tmp, output);
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
			ghost.getMaterial("skin").set(new LocationShader.SilhouetteColorAttribute(new Vector3(alphas.get(0).get(0), alphas.get(0).get(1), alphas.get(0).get(2))));
			ModelInstance cube = new ModelInstance(assets.get("models/cube.g3dj", Model.class));
			cube.transform.translate(2.0f, 0.0f, 2.0f);
			cube.getMaterial("skin").set(new LocationShader.SilhouetteColorAttribute(new Vector3(alphas.get(1).get(0), alphas.get(1).get(1), alphas.get(1).get(2))));
			instances.add(ghost);
			instances.add(cube);
		}
		if (instances != null) {
			int mode = 0;
			switch (mode) {
				// multiple frame buff
				// ers
				case 0:
					// enable stencil test. set clear color to transparent black. clear color, stencil, depth
					Gdx.gl.glClearColor(0, 0, 0, 0);

					// render the instances. is it ok to begin and end the ModelBach for each object?
					// it doesn't work w/o cause you need to modify glStencilFunc and can only do that
					// correctly outside of the batch.
//					frameBuffer3.begin();
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

					modelBatch.begin(mainCamera);
						modelBatch.render(instances);
					modelBatch.end();

					tmpTexture3 = frameBuffer3.getColorBufferTexture();
					tmpTextureRegion3 = new TextureRegion(tmpTexture3);
					tmpTextureRegion3.flip(false, true);
					// do the overlay part of highlight, if activeStencilIndex is above 0
					// overlay for inactive is rendered to the FBO, and considered when calculating the outline
					// but it is not drawn over the inactive object. Which is weird.
					if (activeStencilIndex >= 0) {
						// don't update the stencil buffer anymore.
//						Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_KEEP);
						// only write pixels when stencil buffer = activeStencilIndex
//						Gdx.gl.glStencilFunc(GL20.GL_EQUAL, activeStencilIndex, 0xFF);

						// start fbo to draw the selected silhouette. both objects are being drawn, should only be the one that matches
						// the stencil value in activeStencilIndex.
						locationBuffer.begin();
							Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
							// locationBatch writes a solid color.
							locationBatch.begin(mainCamera);
								locationBatch.render(instances);
							locationBatch.end();
//							ScreenshotFactory.saveScreenshot(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), "location");
						locationBuffer.end();

						// draw edges around the activeIndex object.
						frameBuffer2.begin();
							Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
							spriteBatch.setShader(edgeShader);
							spriteBatch.begin();
								edgeShader.setUniformf("u_screenWidth", Gdx.graphics.getWidth());
								edgeShader.setUniformf("u_screenHeight", Gdx.graphics.getHeight());
								spriteBatch.draw(makeFlippedRegion(locationBuffer.getColorBufferTexture()), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
							spriteBatch.end();
//							ScreenshotFactory.saveScreenshot(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), "stuff");
						frameBuffer2.end();

						// Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//						Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
						// draw the silhouette w/ a sprite batch.
						spriteBatch.setShader(SpriteBatch.createDefaultShader());
						spriteBatch.begin();
//							spriteBatch.draw(tmpTextureRegion3, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//							spriteBatch.draw(makeFlippedRegion(locationBuffer.getColorBufferTexture()), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
							spriteBatch.draw(makeFlippedRegion(frameBuffer2.getColorBufferTexture()), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
//					locationBatch.begin(mainCamera);
//					locationBatch.render(copy);
//					locationBatch.end();



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

					locationBatch.begin(mainCamera);
					for (ModelInstance instance : instances) {
						instance.transform.scl(1.1f);
						locationBatch.render(instance);
						instance.transform.scl(100f / 110f);
					}
					locationBatch.end();

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
//					locationBatch.begin(mainCamera);
//					locationBatch.render(instance);
//					locationBatch.end();
//					instance.transform.scl(100f / 120f);

//					Gdx.gl.glDisable(GL20.GL_STENCIL_TEST);
					break;
				default:
					break;
			}

		}
	}

	/*
	 * Takes a texture, puts it in a TextureRegion, flips the TextureRegion on y
	 * and returns the TextureRegion. Use this to make FrameBuffer output and the like
	 * match the screen.
	 */
	private TextureRegion makeFlippedRegion(Texture texture) {
		tmpRegion = new TextureRegion(texture);
		tmpRegion.flip(false, true);
		return tmpRegion;
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
		locationBuffer.dispose();
		frameBuffer2.dispose();
		frameBuffer3.dispose();
		spriteBatch.dispose();
		modelBatch.dispose();
		tmpTexture.dispose();
		assets.dispose();
	}

	private void initFBOS() {
		if (locationBuffer != null) locationBuffer.dispose();

		locationBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		if (frameBuffer2 != null) frameBuffer2.dispose();

		frameBuffer2 = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		if (frameBuffer3 != null) frameBuffer3.dispose();

		frameBuffer3 = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	}

	public static class SelectableAttribute extends Attribute {

		public final static String Alias = "Selectable";
		public final static long ID = register(Alias);

		public boolean on;
		public Vector3 color;

		public SelectableAttribute(final boolean on) {
			super(ID);
			this.on = on;
		}

		@Override
		public Attribute copy () {
			return new SelectableAttribute(on);
		}

		@Override
		protected boolean equals (Attribute other) {
			return ((SelectableAttribute)other).on == on;
		}

		@Override
		public int compareTo (Attribute o) {
			if (type != o.type) return type < o.type ? -1 : 1;
			Vector3 otherColor = ((SelectableAttribute)o).color;
			return color == otherColor ? 0 : (color.x + color.y + color .z < otherColor.x +otherColor.y + otherColor.z ? -1 : 1);
		}
	}

}
