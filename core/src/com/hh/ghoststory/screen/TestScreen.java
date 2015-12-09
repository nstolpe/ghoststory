package com.hh.ghoststory.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Json;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.ScreenshotFactory;
import com.hh.ghoststory.render.shaders.*;

import java.nio.ByteBuffer;

/**
 * Created by nils on 10/8/15.
 */
public class TestScreen extends AbstractScreen {
	private final Mesh quad;
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
	private ShaderProgram gaussianShader = new ShaderProgram(
		"attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
		"attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
		"attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +

		"uniform mat4 u_projTrans;\n" +

		"varying vec4 v_color;\n" +
		"varying vec2 v_texCoords;\n" +

		"void main() {\n" +
		"   v_color = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" +
		"   v_color.a = v_color.a * (255.0/254.0);\n" +
		"   v_texCoords = " + ShaderProgram.TEXCOORD_ATTRIBUTE + "0;\n" +
		"   gl_Position =  u_projTrans * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n" +
		"}",
		Gdx.files.internal("shaders/gaussian.fragment.glsl").readString());
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
	private ShaderProgram lineShader = new ShaderProgram(Gdx.files.internal("shaders/cel.line.vertex.glsl").readString(), Gdx.files.internal("shaders/cel.line.fragment.glsl").readString());
	private AssetManager assets = new AssetManager();
	private PerspectiveCamera mainCamera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

	private SpriteBatch spriteBatch = new SpriteBatch();
//	private ModelBatch modelBatch = new ModelBatch();
	private ModelBatch modelBatch = new ModelBatch(new PlayShaderProvider());
	private ModelBatch celDepthBatch = new ModelBatch(new CelDepthShaderProvider());
	public ModelBatch locationBatch = new ModelBatch(new LocationShaderProvider());

	private FrameBuffer overlayBuffer;
	private FrameBuffer edgeBuffer;
	private FrameBuffer frameBuffer3;
	private FrameBuffer pp1Buffer;
	private FrameBuffer pp2Buffer;

	private Array<ModelInstance> instances = new Array<ModelInstance>();

	private final CameraInputController camController;

	private Texture tmpTexture;
	private TextureRegion tmpTextureRegion;
	private Texture tmpTexture2;
	private TextureRegion tmpTextureRegion2;
	private Texture tmpTexture3;
	private TextureRegion tmpTextureRegion3;

	private Array<Array<Integer>> alphas = new Array<Array<Integer>>();
//	private Array<Array<Float>> alphas = new Array<Array<Float>>();

	private Vector3 selectMask;

	private UserData overlayData;

	private Pixmap locationMap;

	private FPSLogger logger = new FPSLogger();

	private int activeStencilIndex = 0x01;
	private ByteBuffer pixels = ByteBuffer.allocateDirect(8);

	private Vector2 inputTarget = new Vector2(-1.0f, -1.0f);
	private Environment environment = new Environment();

	private final Matrix4 projectionMatrix = new Matrix4();
	private final Matrix4 transformMatrix = new Matrix4();
	private final Matrix4 combinedMatrix = new Matrix4();

	public TestScreen(GhostStory game) {
		super(game);
		assets.load("models/ghost_blue.g3dj", Model.class);
		assets.load("models/cube.g3dj", Model.class);
		assets.load("models/spider.g3dj", Model.class);
		mainCamera.position.set(5, 5, 5);
		mainCamera.lookAt(0, 0, 0);
		mainCamera.near = 1;
		mainCamera.far = 1000;
		mainCamera.update();

		camController = new CameraInputController(mainCamera) {
			@Override
			public boolean touchDown (int screenX, int screenY, int pointer, int button) {
				TestScreen.this.activateStencilIndex(screenX, screenY);
				TestScreen.this.setInputTarget(screenX, screenY);
				return super.touchDown(screenX, screenY, pointer, button);
			}
		};

		Gdx.input.setInputProcessor(camController);

		Array<Float> values = new Array<Float>() {
			{
				add(0.0f); add(0.1f); add(0.2f); add(0.3f); add(0.4f); add(0.5f); add(0.6f); add(0.7f); add(0.8f); add(0.9f);
			}
		};
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
//		permutations(values, 3, new Array<Float>(), alphas);
		permutations(0, 19, 3, new Array<Integer>(), alphas);
		quad = createFullScreenQuad();
		Json json = new Json();
		String foo = json.prettyPrint(alphas);
		System.out.println(foo);
		System.out.println(alphas.size);
	}

	private void setInputTarget(int x, int y) {
		inputTarget.set(x, y);
	}

	public class UserData {
		public Vector3 selectMask;
		public Vector3 highlightRGB;

		public UserData(Vector3 silhouetteColor, Vector3 selectColor) {
			this.selectMask = silhouetteColor;
			this.highlightRGB = selectColor;
		}
	}

	public Mesh createFullScreenQuad(){
		float[] verts = new float[20];
		int i = 0;
		verts[i++] = 0; // x1
		verts[i++] = 0; // y1
		verts[i++] = 0;
		verts[i++] = 0; // u1
		verts[i++] = 1; // v1

		verts[i++] = 0; // x2
		verts[i++] = Gdx.graphics.getHeight(); // y2
		verts[i++] = 0;
		verts[i++] = 0; // u2
		verts[i++] = 0; // v2

		verts[i++] = Gdx.graphics.getWidth(); // x3
		verts[i++] = Gdx.graphics.getHeight(); // y2
		verts[i++] = 0;
		verts[i++] = 1; // u3
		verts[i++] = 0; // v3

		verts[i++] = Gdx.graphics.getWidth(); // x4
		verts[i++] = 0; // y4
		verts[i++] = 0;
		verts[i++] = 1; // u4
		verts[i++] = 1; // v4
		Mesh tmpMesh = new Mesh(
			true,
			4,
			0,
			new VertexAttribute(VertexAttributes.Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
			new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0")
		);
		tmpMesh.setVertices(verts);
		return tmpMesh;
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
//	public void permutations(Array<Float> values, int size, Array<Float> passes, Array<Array<Float>> output) {
//		if (passes.size >= size) {
//			output.add(new Array(passes));
//			System.out.println(passes);
//		} else {
//			Array<Float> tmp = new Array<Float>();
//			for (int i = 0; i < values.size; i++) {
//				tmp.clear();
//				tmp.addAll(passes);
//				tmp.add(values.get(i));
//				permutations(values, size, tmp, output);
//			}
//		}
//	}

	/**
	 * Returns an array containing all permutations for integer values in the inclusive range of `min` to `max` for
	 * `number` amount of sample points.
	 *
	 * @param min     The first/lowest sample number available in each permutation point.
	 * @param max     The highest/last sample number available in each permutation point.
	 * @param number  The number of points in each permutation set
	 * @param passes  An empty Array<Integer> that will be used on recursive calls to `permutations` to store already
	 *                calulated permutation sets.
	 * @param result  An empty Array<Array<Integer>> that will be populated with the full set of permutations.
	 */
	public void permutations(int min, int max, int number, Array<Integer> passes, Array<Array<Integer>> result) {
		if (passes.size >= number) {
			result.add(new Array(passes));
//			System.out.println(passes);
		} else {
			Array<Integer> tmp = new Array<Integer>();
			for (int i = min; i <= max; i++) {
				tmp.clear();
				tmp.addAll(passes);
				tmp.add(i);
				permutations(min, max, number, tmp, result);
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

			ModelInstance spider = new ModelInstance(assets.get("models/spider.g3dj", Model.class));
			spider.transform.translate(3.0f, 0.0f, 6.0f);
			spider.getMaterial("skin").set(new LocationShader.SilhouetteColorAttribute(new Vector3(1.0f, 0.0f, 0.1f)));

			instances.add(ghost);
			instances.add(cube);
			instances.add(spider);
		}

		if (instances != null) {
			int mode = 0;
			switch (mode) {
				case 0:
					Gdx.gl.glClearColor(1.0f, 0.0f, 1.0f, 0.0f);

					pp1Buffer.begin();
					Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
					celDepthBatch.begin(mainCamera);
					celDepthBatch.render(instances, environment);
					celDepthBatch.end();
//					ScreenshotFactory.saveScreenshot(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), "stuff");
					pp1Buffer.end();

					tmpTexture = pp1Buffer.getColorBufferTexture();
					tmpTextureRegion = new TextureRegion(tmpTexture);
					tmpTextureRegion.flip(false, true);

					Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);

//					modelBatch.begin(mainCamera);
//					modelBatch.render(instances, environment);
//					modelBatch.end();

//					pp1Buffer.getColorBufferTexture().bind();
//					combinedMatrix.set(projectionMatrix).mul(transformMatrix);
//					lineShader.begin();
//					lineShader.setUniformf("u_size", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//					lineShader.setUniformMatrix("u_projTrans", combinedMatrix);
//					quad.render(lineShader, GL20.GL_TRIANGLE_STRIP, 0, quad.getNumVertices());
//					lineShader.end();
					if (!lineShader.isCompiled()) throw new GdxRuntimeException("Couldn't compile shader: " + lineShader.getLog());
					spriteBatch.setShader(lineShader);
					spriteBatch.begin();
					lineShader.setUniformf("u_size", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
					spriteBatch.draw(tmpTextureRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
					spriteBatch.end();

					break;
				case 1:
					Gdx.gl.glClearColor(0, 0, 0, 0);

					/* draw silhouettes to buffer */
					overlayBuffer.begin();
					Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);

					locationBatch.begin(mainCamera);
					locationBatch.render(instances);
					locationBatch.end();

					overlayBuffer.end();
					// cache the texture
					tmpTexture = overlayBuffer.getColorBufferTexture();


					/* draw first buffer as is and then with edge detection. capture both in different buffer */
					frameBuffer3.begin();

					Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);

					spriteBatch.begin();
					spriteBatch.draw(tmpTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
					spriteBatch.end();

					spriteBatch.setShader(edgeShader);
					spriteBatch.begin();
					edgeShader.setUniformf("u_screenWidth", Gdx.graphics.getWidth());
					edgeShader.setUniformf("u_screenHeight", Gdx.graphics.getHeight());
					edgeShader.setUniformf("u_rFactor", 0.8f);
					edgeShader.setUniformf("u_gFactor", 0.0f);
					edgeShader.setUniformf("u_bFactor", 0.1f);
					spriteBatch.draw(tmpTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
					spriteBatch.end();

					frameBuffer3.end();

					// draw 10 or whatever passes for gaussian. this is slow.
					Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);

					pp1Buffer.begin();
					Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
					spriteBatch.setShader(gaussianShader);
					spriteBatch.begin();
					gaussianShader.setUniformf("dir", 1f, 0f);
					gaussianShader.setUniformf("width", Gdx.graphics.getWidth());
					gaussianShader.setUniformf("height", Gdx.graphics.getHeight());
					gaussianShader.setUniformf("radius", 1f);
					spriteBatch.draw(frameBuffer3.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
					spriteBatch.end();
					pp1Buffer.end();

					pp2Buffer.begin();
					Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
					spriteBatch.begin();
					gaussianShader.setUniformf("dir", 0f, 1f);
					spriteBatch.draw(pp1Buffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
					spriteBatch.end();
					pp2Buffer.end();




//					Gdx.gl.glEnable(GL20.GL_BLEND);
//					Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ONE);
//					Gdx.gl.glBlendEquation(GL20.GL_FUNC_ADD);
					Gdx.gl.glClearColor(1, 0, 1, 0);
					Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);

					modelBatch.begin(mainCamera);
					modelBatch.render(instances);
					modelBatch.end();

					spriteBatch.setShader(SpriteBatch.createDefaultShader());
					spriteBatch.begin();
					spriteBatch.draw(pp2Buffer.getColorBufferTexture(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
					spriteBatch.end();

					Gdx.gl.glDisable(GL20.GL_BLEND);
					break;
				case 2:
					Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);

					Gdx.gl.glClearColor(0, 0, 0, 0);
					Gdx.gl.glClearStencil(0x00);

					Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

					Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 0xff);
					Gdx.gl.glStencilMask(0xFF);

					modelBatch.begin(mainCamera);
						modelBatch.render(instances);
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
					break;
				case 3:
					frameBuffer3.begin();
						Gdx.gl.glEnable(GL20.GL_STENCIL_TEST);

						Gdx.gl.glClearColor(0, 0, 0, 0);
						Gdx.gl.glClearStencil(0x00);

						Gdx.gl.glStencilOp(GL20.GL_KEEP, GL20.GL_KEEP, GL20.GL_REPLACE);
						Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

						Gdx.gl.glStencilFunc(GL20.GL_ALWAYS, 1, 0xff);
						Gdx.gl.glStencilMask(0xFF);

						modelBatch.begin(mainCamera);
						modelBatch.render(instances);
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

						if (inputTarget.x >= 0 && inputTarget.y >= 0) {
							Gdx.gl.glReadPixels((int) inputTarget.x, Gdx.graphics.getHeight() - (int) inputTarget.y, 1, 1, GL20.GL_STENCIL_INDEX, GL20.GL_FLOAT, pixels);
							int stencilIndex = (int) pixels.get();
							System.out.println(stencilIndex);
							inputTarget.set(-1.0f, -1.0f);
						}
					frameBuffer3.end();

					tmpTexture = frameBuffer3.getColorBufferTexture();
					tmpTextureRegion = new TextureRegion(tmpTexture);
					tmpTextureRegion.flip(false,true);

					Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_STENCIL_BUFFER_BIT);

					spriteBatch.begin();
					spriteBatch.draw(tmpTextureRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
					spriteBatch.end();
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

		if (pp1Buffer != null) pp1Buffer.dispose();

		pp1Buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		if (pp2Buffer != null) pp2Buffer.dispose();

		pp2Buffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);

		spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		projectionMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
