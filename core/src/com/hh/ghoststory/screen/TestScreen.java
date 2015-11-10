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
import com.badlogic.gdx.utils.ScreenUtils;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.render.shaders.LocationShader;
import com.hh.ghoststory.render.shaders.LocationShaderProvider;

import java.nio.ByteBuffer;

/**
 * Created by nils on 10/8/15.
 */
public class TestScreen extends AbstractScreen {
	private ShaderProgram silhouetteShader = new ShaderProgram(
		"attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
		"attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
		"attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
		"uniform mat4 u_projTrans;\n" +
		"varying vec4 v_color;\n" +
		"varying vec2 v_texCoords;\n" +
		"\n" +
		"void main()\n" +
		"{\n" +
		"   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
		"   v_color.a = v_color.a * (255.0/254.0);\n" +
		"   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
		"   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
		"}\n",
		"#ifdef GL_ES\n" +
		"#define LOWP lowp\n" +
		"precision mediump float;\n" +
		"#else\n" +
		"#define LOWP \n" +
		"#endif\n" +
		"varying LOWP vec4 v_color;\n" +
		"varying vec2 v_texCoords;\n" +
		"uniform vec3 u_color;\n" +
		"uniform float u_alpha;\n" +
		"uniform sampler2D u_texture;\n" +
		"void main()\n " +
		"{\n" +
		"  vec4 black = vec4(0.0, 0.0, 0.0, 1.0);\n" +
		"  vec4 sample = texture2D(u_texture, v_texCoords).rgba;\n" +
		"  if (sample == black) {\n" +
		"    gl_FragColor = vec4(u_color, u_alpha);\n" +
		"  } else {\n" +
		"    gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);\n" +
		"  }\n" +
		"}");
	private ShaderProgram edgeShader = new ShaderProgram(
		"attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
		"attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
		"attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
		"uniform mat4 u_projTrans;\n" +
		"varying vec4 v_color;\n" +
		"varying vec2 v_texCoords;\n" +
		"\n" +
		"void main()\n" +
		"{\n" +
		"   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
		"   v_color.a = v_color.a * (255.0/254.0);\n" +
		"   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
		"   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
		"}\n",
		Gdx.files.internal("shaders/edge.fragment.glsl").readString()
	);
	private AssetManager assets = new AssetManager();
	private PerspectiveCamera mainCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

	private SpriteBatch spriteBatch = new SpriteBatch();
	private ModelBatch modelBatch = new ModelBatch();
	public ModelBatch locationBatch = new ModelBatch(new LocationShaderProvider());

	private FrameBuffer overlayBuffer;
	private FrameBuffer edgeBuffer;
	private FrameBuffer frameBuffer3;

	private Array<ModelInstance> instances = new Array<ModelInstance>();

	private final CameraInputController camController;

	private Texture tmpTexture;
	private TextureRegion tmpTextureRegion;
	private Texture tmpTexture2;
	private TextureRegion tmpTextureRegion2;
	private Texture tmpTexture3;
	private TextureRegion tmpTextureRegion3;

	private Array<Array<Float>> alphas = new Array<Array<Float>>();

	private Vector3 selectMask;

	private UserData overlayData;

	private Pixmap locationMap;

	private FPSLogger logger = new FPSLogger();

	private int activeStencilIndex = 1;
	private ByteBuffer pixels = ByteBuffer.allocateDirect(8);

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
				TestScreen.this.activateStencilIndex(screenX, screenY);
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

	public class UserData {
		public Vector3 selectMask;
		public Vector3 highlightRGB;

		public UserData(Vector3 silhouetteColor, Vector3 selectColor) {
			this.selectMask = silhouetteColor;
			this.highlightRGB = selectColor;
		}
	}

	private void activateStencilIndex(int screenX, int screenY) {
		Gdx.gl.glReadPixels(screenX, Gdx.graphics.getHeight() - screenY, 1, 1, GL20.GL_STENCIL_INDEX, GL20.GL_UNSIGNED_INT, pixels);
		int stencilIndex = (int) pixels.get();
		activeStencilIndex = stencilIndex < 0 ? stencilIndex + 256 : stencilIndex;
		pixels.clear();
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
//			System.out.println(passes.items);
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
			ghost.getMaterial("skin").set(new LocationShader.SilhouetteColorAttribute(new Vector3(0.8f, 0.0f, 0.1f)));
			ghost.getMaterial("skin").set(new StencilIndexAttribute(1));

			ModelInstance cube = new ModelInstance(assets.get("models/cube.g3dj", Model.class));
			cube.transform.translate(2.0f, 0.0f, 2.0f);
			cube.getMaterial("skin").set(new LocationShader.SilhouetteColorAttribute(new Vector3(0.0f, 0.9f, 0.2f)));
			cube.getMaterial("skin").set(new StencilIndexAttribute(200));

			instances.add(ghost);
			instances.add(cube);
		}

		if (instances != null) {
			int mode = 0;
			switch (mode) {
				case 0:
					Gdx.gl.glClearColor(0, 0, 0, 0);
					Gdx.gl.glClearStencil(0x00);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);
					// enable stencil test and clear stencil buffer
					Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);

					// disable color and depth writes. might not need depth
					Gdx.gl.glColorMask(false, false, false, false);
					Gdx.gl.glDepthMask(false);


					// only write to stencil buffer when depth and stencil pass
					Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
					// draw entire scene only to stencil buffer
					// write stencil bit for each object
					// limit 255 objects
					for (int i = 0; i < instances.size; i++) {
						// set stencil buffer to write the StencilIndexAttribute.value to the stencil buffer.
						StencilIndexAttribute stencilAttr = (StencilIndexAttribute) instances.get(i).getMaterial("skin").get(StencilIndexAttribute.ID);
						Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, stencilAttr.value, 0xFF);

						modelBatch.begin(mainCamera);
							modelBatch.render(instances.get(i));
						modelBatch.end();
					}
					// clear depth buffer
					Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);

					// set stencil op to always keep, we don't want to update the buffer anymore.
					// enable writing to color and depth. depth might not be needed, if it doesn't need to go off up above.
					Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_KEEP);
					Gdx.gl.glColorMask(true, true, true, true);
					Gdx.gl.glDepthMask(true);

					if (activeStencilIndex > 0) {

						Gdx.gl.glStencilFunc(GL20.GL_EQUAL, activeStencilIndex, 0xFF);
						// draw black silhouette w/ stencil test on.
						for (int i = 0; i < instances.size; i++) {
							instances.get(i).getMaterial("skin").set(new LocationShader.SilhouetteAttribute(1));
							locationBatch.begin(mainCamera);
								locationBatch.render(instances.get(i));
							locationBatch.end();
							instances.get(i).getMaterial("skin").remove(LocationShader.SilhouetteAttribute.ID);
						}
						tmpTextureRegion = ScreenUtils.getFrameBufferTexture();
						Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, activeStencilIndex, 0xFF);
						Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
					}

					// draw scene to color buffer.
					modelBatch.begin(mainCamera);
						modelBatch.render(instances);
					modelBatch.end();

//					Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
					if (activeStencilIndex > 0) {
						Vector3 drawColor = new Vector3();
						for (int i = 0; i < instances.size; i++) {
							StencilIndexAttribute stencilAttr = (StencilIndexAttribute) instances.get(i).getMaterial("skin").get(StencilIndexAttribute.ID);
							if (stencilAttr.value == activeStencilIndex) {
								LocationShader.SilhouetteColorAttribute color = (LocationShader.SilhouetteColorAttribute) instances.get(i).getMaterial("skin").get(LocationShader.SilhouetteColorAttribute.ID);
								drawColor = color.value;
							}
						}

						// pass silhouette as sample to spritebatch shader
						// render the silhouetted pixels as the highlight at the alpha passed in (u_alpha)
						spriteBatch.setShader(silhouetteShader);
						spriteBatch.begin();
							silhouetteShader.setUniformf("u_color", drawColor);
							silhouetteShader.setUniformf("u_alpha", 0.6f);
							spriteBatch.draw(tmpTextureRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
						spriteBatch.end();

						// create the outline passing the silhouette to the spritebatch shader.
						spriteBatch.setShader(edgeShader);
						spriteBatch.begin();
							edgeShader.setUniformf("u_screenWidth", Gdx.graphics.getWidth());
							edgeShader.setUniformf("u_screenHeight", Gdx.graphics.getHeight());
							edgeShader.setUniformf("u_rFactor", drawColor.x);
							edgeShader.setUniformf("u_gFactor", drawColor.y);
							edgeShader.setUniformf("u_bFactor", drawColor.z);
							spriteBatch.draw(tmpTextureRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
						spriteBatch.end();

						tmpTextureRegion.getTexture().dispose();
						tmpTextureRegion = null;
					}
					break;
				case 1:
					// set clear color to transparent black. clear color, stencil, depth
					Gdx.gl.glClearColor(0, 0, 0, 0);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

					// draw a single color silhouette for each object. it will be sampled, then cleared and drawn over.
					locationBatch.begin(mainCamera);
					locationBatch.render(instances);
					locationBatch.end();

					// cache the output here, a framebuffer won't work
					// https://code.google.com/p/libgdx/issues/detail?id=1626
					if (locationMap != null) locationMap.dispose();
					locationMap = ScreenUtils.getFrameBufferPixmap(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

					// begin the real batch, where the scene is rendered.
					modelBatch.begin(mainCamera);
					modelBatch.render(instances);
					modelBatch.end();

					// something has been selected, draw the overlay and outline.
					if (selectMask != null) {
						// use temp objects to extract only the silhouette of the active object
						tmpTexture = new Texture(locationMap);
						tmpTextureRegion = new TextureRegion(tmpTexture);
						tmpTextureRegion.flip(false, true);

//						overlayBuffer.begin();
							spriteBatch.setShader(silhouetteShader);
							spriteBatch.begin();
								silhouetteShader.setUniformf("u_color", overlayData.highlightRGB);
								silhouetteShader.setUniformf("u_alpha", 1.0f);
								spriteBatch.draw(tmpTextureRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
							spriteBatch.end();
//							ScreenshotFactory.saveScreenshot(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), "stuff");
//						overlayBuffer.end();

						tmpTexture.dispose();
					}
					break;
				// stencil
				case 2:
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

		logger.log();
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
		locationMap.dispose();
		overlayBuffer.dispose();
		edgeBuffer.dispose();
		frameBuffer3.dispose();
		spriteBatch.dispose();
		modelBatch.dispose();
		tmpTexture.dispose();
		assets.dispose();
	}

	private void initFBOS() {
		if (overlayBuffer != null) overlayBuffer.dispose();

		overlayBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		if (edgeBuffer != null) edgeBuffer.dispose();

		edgeBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		if (frameBuffer3 != null) frameBuffer3.dispose();

		frameBuffer3 = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	}

	public static class SelectableAttribute extends Attribute {

		public final static String Alias = "Selectable";
		public final static long ID = register(Alias);

		public boolean on;
		public Vector3 color;

		public SelectableAttribute(final boolean on, Vector3 color) {
			super(ID);
			this.on = on;
			this.color = color;
		}

		@Override
		public Attribute copy () {
			return new SelectableAttribute(on, color);
		}

		@Override
		protected boolean equals (Attribute other) {
			return ((SelectableAttribute)other).on == on && ((SelectableAttribute)other).color == color;
		}

		@Override
		public int compareTo (Attribute o) {
			if (type != o.type) return type < o.type ? -1 : 1;
			Vector3 otherColor = ((SelectableAttribute)o).color;
			return color == otherColor ? 0 : (color.x + color.y + color .z < otherColor.x + otherColor.y + otherColor.z ? -1 : 1);
		}
	}


	public static class StencilIndexAttribute extends Attribute {

		public final static String Alias = "StencilIndex";
		public final static long ID = register(Alias);

		public int value;

		public StencilIndexAttribute(final int value) {
			super(ID);
			this.value = value;
		}

		@Override
		public Attribute copy () {
			return new StencilIndexAttribute(value);
		}

		@Override
		protected boolean equals (Attribute other) {
			return ((StencilIndexAttribute)other).value == value && ((StencilIndexAttribute)other).value == value;
		}

		@Override
		public int compareTo (Attribute o) {
			if (type != o.type) return type < o.type ? -1 : 1;
			int otherValue = ((StencilIndexAttribute)o).value;
			return value == otherValue ? 0 : value < otherValue ? -1 : 1;
		}
	}

}
