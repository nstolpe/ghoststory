package com.hh.ghoststory.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.lib.utility.GameObject;
import com.hh.ghoststory.render.shaders.CelColorShaderProgram;
import com.hh.ghoststory.render.shaders.CelDepthShaderProvider;
import com.hh.ghoststory.render.shaders.CelLineShaderProgram;

/**
 * Created by nils on 12/12/15.
 */
public class CelShaderScreen extends AbstractScreen {
	/**
	 * mainBatch      Draws all models w/ the default LibGDX shader.
	 * celDepthBatch     Draws the scene depth w/ some calculations from camera near/far. Captured in FBO for drawing
	 *                   cel lines in post processing pass
	 *
	 * defaultFbo        FBO to capture the output of `mainBatch`
	 * celDepthFbo       FBO to capture the output of `celDepthBatch`
	 *
	 * fbos              An array of `FrameBuffer` objects. For performing batch operations (resizing...)
	 *
	 * celLineShader     Draws draws the cel lines by sampling `defaultFbo`
	 * celColorShader    Draws the cel colors by sampling `celDepthFbo`
	 */
//	private ModelBatch mainBatch = new ModelBatch(Gdx.files.internal("shaders/cel.main.vertex.glsl").readString(), Gdx.files.internal("shaders/cel.main.fragment.glsl").readString());
	private ModelBatch mainBatch = new ModelBatch(Gdx.files.classpath("com/badlogic/gdx/graphics/g3d/shaders/default.vertex.glsl").readString(), Gdx.files.internal("shaders/cel.main.fragment.glsl").readString());
//	private ModelBatch mainBatch = new ModelBatch(new PlayShaderProvider());
	private ModelBatch celDepthBatch = new ModelBatch(new CelDepthShaderProvider());
	private Array<ModelBatch> modelBatches = new Array<ModelBatch>();

	private FrameBuffer defaultFbo;
	private FrameBuffer celDepthFbo;

	private TextureRegion depthTextureRegion;
	private TextureRegion defaultTextureRegion;

	private ShaderProgram celLineShader = new CelLineShaderProgram();
	private ShaderProgram celColorShader = new CelColorShaderProgram();

	private PerspectiveCamera camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

	private Environment environment = new Environment();

	private SpriteBatch spriteBatch = new SpriteBatch();

	private AssetManager assetManager = new AssetManager();

	private Array<ModelInstance> modelInstances = new Array<ModelInstance>();

	private CameraInputController camController;

	private final Array<GameObject> gameObjects;
	private String configJson =  Gdx.files.internal("config/cel_models.json").readString();
	private Json json = new Json();

	public CelShaderScreen(GhostStory game) {
		super(game);
		gameObjects = json.fromJson(Array.class, configJson);
		loadModels();

		camera.position.set(5, 5, 5);
		camera.lookAt(0, 0, 0);
		camera.near = 1;
		camera.far = 1000;
		camera.update();

		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 1.8f, -1f, -0.8f, 0.2f));

		camController = new CameraInputController(camera);
		Gdx.input.setInputProcessor(camController);
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
	}

	@Override
	public void render(float delta) {
		camera.update();
		camController.update();

		if (loading && assetManager.update()) {
			doneLoading();
		} else {
			Gdx.gl.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
			Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);

			celDepthFbo.begin();
			Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
			Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
			celDepthBatch.begin(camera);
			celDepthBatch.render(modelInstances);
			celDepthBatch.end();
			celDepthFbo.end();

			depthTextureRegion = new TextureRegion(celDepthFbo.getColorBufferTexture());
			depthTextureRegion.flip(false, true);

//			defaultFbo.begin();
			Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
			mainBatch.begin(camera);
			mainBatch.render(modelInstances, environment);
			mainBatch.end();
//			defaultFbo.end();

			defaultTextureRegion = new TextureRegion(defaultFbo.getColorBufferTexture());
			defaultTextureRegion.flip(false, true);

			if (!celColorShader.isCompiled()) Gdx.app.log("shader", celColorShader.getLog());

//			Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);

//			spriteBatch.setShader(celColorShader);
//			spriteBatch.begin();
//			spriteBatch.draw(defaultTextureRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//			spriteBatch.end();

			spriteBatch.setShader(celLineShader);
			celLineShader.setUniformf("u_size", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			spriteBatch.begin();
			spriteBatch.draw(depthTextureRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			spriteBatch.end();

			spriteBatch.setShader(null);
		}
	}

	@Override
	protected void doneLoading() {
		for (GameObject gameObject : gameObjects) {
			gameObject.modelInstance(new ModelInstance(assetManager.get("models/" + gameObject.modelAsset(), Model.class)));
			gameObject.modelInstance().transform.translate(gameObject.position());
			modelInstances.add(gameObject.modelInstance());
		}

		super.doneLoading();
	}
	@Override
	public void resize(int width, int height) {
		initFrameBuffers(width, height);
		initCamera(width, height);
		spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, width, height));
	}

	@Override
	public void dispose() {
		for (ModelBatch modelBatch : modelBatches) modelBatch.dispose();
		defaultFbo.dispose();
		celDepthFbo.dispose();
		spriteBatch.dispose();
		assetManager.dispose();
	}

	private void initFrameBuffers(int width, int height) {
		if (defaultFbo != null) defaultFbo.dispose();
		defaultFbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		if (celDepthFbo != null) celDepthFbo.dispose();
		celDepthFbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	}

	private void initCamera(int width, int height) {
		camera.position.set(camera.position);
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}

	private void loadModels() {
		for (GameObject gameObject : gameObjects)
			assetManager.load("models/" + gameObject.modelAsset(), Model.class);

		loading = true;
	}
}
