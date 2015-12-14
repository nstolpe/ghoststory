package com.hh.ghoststory.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.render.shaders.CelColorShaderProgram;
import com.hh.ghoststory.render.shaders.CelDepthShaderProvider;
import com.hh.ghoststory.render.shaders.CelLineShaderProgram;

/**
 * Created by nils on 12/12/15.
 */
public class CelShaderScreen extends AbstractScreen {
	/**
	 * defaultBatch      Draws all models w/ the default LibGDX shader.
	 * celDepthBatch     Draws the scene depth w/ some calculations from camera near/far. Captured in FBO for drawing
	 *                   cel lines in post processing pass
	 *
	 * defaultFbo        FBO to capture the output of `defaultBatch`
	 * celDepthFbo       FBO to capture the output of `celDepthBatch`
	 *
	 * fbos              An array of `FrameBuffer` objects. For performing batch operations (resizing...)
	 *
	 * celLineShader     Draws draws the cel lines by sampling `defaultFbo`
	 * celColorShader    Draws the cel colors by sampling `celDepthFbo`
	 */
	private ModelBatch defaultBatch = new ModelBatch();
	private ModelBatch celDepthBatch = new ModelBatch(new CelDepthShaderProvider());
	private Array<ModelBatch> modelBatches = new Array<ModelBatch>();

	private FrameBuffer defaultFbo;
	private FrameBuffer celDepthFbo;
	private Array<FrameBuffer> frameBuffers = new Array<FrameBuffer>();

	private ShaderProgram celLineShader = new CelLineShaderProgram();
	private ShaderProgram celColorShader = new CelColorShaderProgram();

	private PerspectiveCamera camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

	private Environment environment = new Environment();

	private SpriteBatch spriteBatch = new SpriteBatch();

	private AssetManager assetManager = new AssetManager();

	private Array<String> modelAssets = new Array<String>(Gdx.files.internal("config/cel_models.txt").readString().split("\n"));

	private Array<ModelInstance> modelInstances = new Array<ModelInstance>();

	private CameraInputController camController;

	public CelShaderScreen(GhostStory game) {
		super(game);
		init();
		loadModels();

		camera.position.set(5, 5, 5);
		camera.lookAt(0, 0, 0);
		camera.near = 1;
		camera.far = 1000;
		camera.update();

		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

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
			Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
			Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
			defaultBatch.begin(camera);
			defaultBatch.render(modelInstances, environment);
			defaultBatch.end();
		}
	}

	@Override
	protected void doneLoading() {
		for (String modelAsset : modelAssets)
			modelInstances.add(new ModelInstance(assetManager.get("models/" + modelAsset, Model.class)));
		super.doneLoading();
	}
	@Override
	public void resize(int width, int height) {
		initFrameBuffers(width, height);
		initCamera(width, height);
	}

	@Override
	public void dispose() {
		for (FrameBuffer frameBuffer : frameBuffers) frameBuffer.dispose();
		for (ModelBatch modelBatch : modelBatches) modelBatch.dispose();
		spriteBatch.dispose();
		assetManager.dispose();
	}

	private void initFrameBuffers(int width, int height) {
		for (FrameBuffer frameBuffer : frameBuffers) {
			if (frameBuffer!= null) frameBuffer.dispose();
			frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
		}
	}

	private void initCamera(int width, int height) {
		camera.position.set(camera.position);
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}

	private void init() {
		frameBuffers.add(defaultFbo);
		frameBuffers.add(celDepthFbo);
		modelBatches.add(defaultBatch);
		modelBatches.add(celDepthBatch);
	}

	private void loadModels() {
		for (String modelAsset : modelAssets)
			assetManager.load("models/" + modelAsset, Model.class);
		loading = true;
	}
}
