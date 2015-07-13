package com.hh.ghoststory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.microbasic.sm.tools.FrameBufferCubeMap;
import com.microbasic.sm.tools.ScreenshotFactory;

public class PointLight extends Light
{

	public FrameBufferCubeMap	frameBuffer;
	public Cubemap				depthMap;

	public PointLight(final MainScreen mainScreen, final Vector3 position)
	{
		super(mainScreen);
		this.position = position;
		init();
	}

	@Override
	public void applyToShader(final ShaderProgram sceneShaderProgram)
	{
		final int textureNum = 2;
		depthMap.bind(textureNum);
		sceneShaderProgram.setUniformf("u_type", 2);
		sceneShaderProgram.setUniformi("u_depthMapCube", textureNum);
		sceneShaderProgram.setUniformf("u_cameraFar", camera.far);
		sceneShaderProgram.setUniformf("u_lightPosition", position);
	}

	@Override
	public void init()
	{
		super.init();

		camera = new PerspectiveCamera(90f, MainScreen.DEPTHMAPSIZE, MainScreen.DEPTHMAPSIZE);
		camera.near = 4f;
		camera.far = 70;
		camera.position.set(position);
		camera.update();
	}

	@Override
	public void render(final ModelInstance modelInstance)
	{
		if (!needsUpdate)
		{
			return;
		}
		needsUpdate = false;

		if (frameBuffer == null)
		{
			frameBuffer = new FrameBufferCubeMap(Format.RGBA8888, MainScreen.DEPTHMAPSIZE, true);
		}

		shaderProgram.begin();
		shaderProgram.setUniformf("u_cameraFar", camera.far);
		shaderProgram.setUniformf("u_lightPosition", position);
		shaderProgram.end();

		for (int s = 0; s <= 5; s++)
		{
			final Cubemap.CubemapSide side = Cubemap.CubemapSide.values()[s];
			frameBuffer.begin(side, camera);
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

			modelBatch.begin(camera);
			modelBatch.render(modelInstance);
			modelBatch.end();

//			if (mainScreen.takeScreenshots)
//			{
//				ScreenshotFactory.saveScreenshot(frameBuffer.getWidth(), frameBuffer.getHeight(), "depthmapcube");
//			}

		}

		frameBuffer.end();
		depthMap = frameBuffer.getColorBufferTexture();
	}

	@Override
	public void act(final float delta)
	{
		// TODO Auto-generated method stub

	}
}
