package com.hh.ghoststory.scene.lights.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBufferCubemap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.hh.ghoststory.DepthMapShader;

/**
 * Created by nils on 9/3/15.
 */
public class PointCaster extends PointLight implements Disposable, Caster {
	public PerspectiveCamera camera = new PerspectiveCamera();
	public int depthmapsize = 1024;
	public boolean casting = true;
	public FrameBufferCubemap frameBuffer;
	public Cubemap depthMap;
	public ShaderProgram shaderProgram = new ShaderProgram(
			Gdx.files.internal("depth.vertex.glsl"),
			Gdx.files.internal("depth.fragment.glsl")
	);
	public ModelBatch modelBatch = new ModelBatch(new DefaultShaderProvider() {
		@Override
		protected com.badlogic.gdx.graphics.g3d.Shader createShader(final Renderable renderable) {
			return new DepthMapShader(renderable, shaderProgram);
		}
	});

	public PointCaster() {
		initCamera();
		initFrameBuffer();
	}

	/**
	 * implements overrides
	 */
	@Override
	public void initCamera() {
		camera.fieldOfView = 90f;
		camera.viewportWidth = depthmapsize;
		camera.viewportHeight = depthmapsize;
		camera.near = 1f;
		camera.far = 30;
		camera.position.set(position);
		camera.update();
	}

	@Override
	public void applyToShader(final ShaderProgram shaderProgram) {
		final int textureNum = 2;
		depthMap.bind(textureNum);
		shaderProgram.setUniformf("u_type", 2);
		shaderProgram.setUniformi("u_depthMapCube", textureNum);
		shaderProgram.setUniformf("u_cameraFar", camera.far);
		shaderProgram.setUniformf("u_lightPosition", position);
	}

	@Override
	public void initFrameBuffer() {
		frameBuffer = new FrameBufferCubemap(Pixmap.Format.RGBA8888, depthmapsize, depthmapsize, true);
	}

	@Override
	public void render(Array<ModelInstance> instances) {
		shaderProgram.begin();
		shaderProgram.setUniformf("u_cameraFar", camera.far);
		shaderProgram.setUniformf("u_lightPosition", position);
		shaderProgram.end();

		frameBuffer.begin();
		while(frameBuffer.nextSide()) {
			frameBuffer.getSide().getUp(camera.up);
			frameBuffer.getSide().getDirection(camera.direction);
			camera.update();
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
			modelBatch.begin(camera);
			modelBatch.render(instances);
			modelBatch.end();
//			Debugging. Remove when everything works.
//		    ScreenshotFactory.saveScreenshot(frameBuffer.getWidth(), frameBuffer.getHeight(), "depthmapcube");
		}
		frameBuffer.end();

		depthMap = frameBuffer.getColorBufferTexture();
	}

	@Override
	public void dispose() {
		frameBuffer.dispose();
		shaderProgram.dispose();
		depthMap.dispose();
	}
	/**
	 * extends overrides
	 * Need to update the camera position as well.
	 */
	@Override
	public PointCaster setPosition(float positionX, float positionY, float positionZ) {
		setPosition(new Vector3(positionX, positionY, positionZ));
		return this;
	}

	@Override
	public PointCaster setPosition(Vector3 position) {
		super.setPosition(position);
		camera.position.set(position);
		// is update needed here? probably
		camera.update();
		return this;
	}
}
