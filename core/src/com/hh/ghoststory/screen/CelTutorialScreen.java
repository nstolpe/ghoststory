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
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.GhostStory;
import com.hh.ghoststory.render.shaders.CelDepthShaderProvider;
import com.hh.ghoststory.render.shaders.CelLineShaderProgram;

public class CelTutorialScreen extends AbstractScreen {
	private PerspectiveCamera camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

	private AssetManager assetManager = new AssetManager();
	private Array<ModelInstance> instances = new Array<ModelInstance>();

	private FrameBuffer fbo;
	private TextureRegion textureRegion;
	private ShaderProgram lineShader = new CelLineShaderProgram();

	private SpriteBatch spriteBatch = new SpriteBatch();
	private ModelBatch modelBatch = new ModelBatch(Gdx.files.internal("shaders/cel.main.vertex.glsl").readString(), Gdx.files.internal("shaders/cel.main.fragment.glsl").readString());
	private ModelBatch depthBatch = new ModelBatch(new CelDepthShaderProvider());
	private Environment environment = new Environment();

	public CelTutorialScreen(GhostStory game) {
		super(game);

		Gdx.gl.glClearColor(1.0f, 0.0f, 1.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);

		// setup camera
		camera.position.set(5, 5, 5);
		camera.lookAt(0, 0, 0);
		camera.near = 1;
		camera.far = 1000;
		camera.update();

		// add a light
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 1.8f, -1f, -0.8f, 0.2f));

		// load our model
		assetManager.load("models/spider.g3dj", Model.class);
		loading = true;
	}
	@Override
	public void render(float delta) {
		if (loading && assetManager.update())
			doneLoading();

		camera.update();
		Gdx.gl.glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);

		// render depth map to fbo
		captureDepth();
		// draw the scene
		renderScene();
		// put fbo texture in a TextureRegion and flip it
		prepTextureRegion();
		// draw the cel outlines
		drawOutlines();
	}
	/*
	 * Draws the cel outlines using the CelLineShaderProgram
	 */
	protected void drawOutlines() {
		spriteBatch.setShader(lineShader);
		lineShader.setUniformf("u_size", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch.begin();
		spriteBatch.draw(textureRegion, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch.end();
		spriteBatch.setShader(null);
	}
	/*
	 * Stores fbo texture in a TextureRegion and flips it vertically.
	 */
	protected void prepTextureRegion() {
		textureRegion = new TextureRegion(fbo.getColorBufferTexture());
		textureRegion.flip(false, true);
	}
	/*
	 * Draws the depth pass to an fbo, using a ModelBatch created with CelDepthShaderProvider()
	 */
	protected void captureDepth() {
		fbo.begin();
		Gdx.gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
		depthBatch.begin(camera);
		depthBatch.render(instances);
		depthBatch.end();
		fbo.end();
	}
	/*
	 * Renders the scene.
	 */
	protected void renderScene() {
		Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT | GL20.GL_COLOR_BUFFER_BIT);
		modelBatch.begin(camera);
		modelBatch.render(instances, environment);
		modelBatch.end();
	}
	@Override
	protected void doneLoading() {
		loading = false;
		instances.add(new ModelInstance(assetManager.get("models/spider.g3dj", Model.class)));
	}
	/*
	 * Set camera width and height, SpriteBatch projection matrix, and reinit the FBOs
	 */
	@Override
	public void resize(int width, int height) {
		camera.position.set(camera.position);
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();

		if (fbo != null) fbo.dispose();
		fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

		spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, width, height));
	}
	@Override
	public void dispose() {
		assetManager.dispose();
		modelBatch.dispose();
		depthBatch.dispose();
		spriteBatch.dispose();
		fbo.dispose();
		lineShader.dispose();
	}
}
