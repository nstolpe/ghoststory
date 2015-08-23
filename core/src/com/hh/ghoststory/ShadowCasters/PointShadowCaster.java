package com.hh.ghoststory.ShadowCasters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.glutils.FrameBufferCubemap;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.hh.ghoststory.ScreenshotFactory;

/**
 * Created by nils on 7/21/15.
 */
public class PointShadowCaster extends ShadowCaster implements Disposable {
    public PointLight light = new PointLight();
    public FrameBufferCubemap frameBuffer;
    public Cubemap depthMap;

    /**
     * @param light
     */
    public PointShadowCaster(PointLight light) {
	    this.light.set(light);
	    this.position.set(light.position);
	    setupCamera();
	    setupFrameBuffer();
    }

	public PointShadowCaster(PointLight light, Vector3 position) {
		this.position.set(position);
		this.light = light.setPosition(position);
		setupCamera();
		setupFrameBuffer();
	}

	public void setupFrameBuffer() {
		frameBuffer = new FrameBufferCubemap(Pixmap.Format.RGBA8888, depthmapsize, depthmapsize, true);
	}

    @Override
    public void setupCamera() {
        camera.fieldOfView = 90f;
        camera.viewportWidth = depthmapsize;
        camera.viewportHeight = depthmapsize;
        camera.near = 1f;
        camera.far = 30;
        camera.position.set(position);
        camera.update();
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
	public void setPosition(Vector3 position) {
		super.setPosition(position);
		light.setPosition(position);
		camera.position.set(position);
		camera.update();
	}

	@Override
	public void applyToShader(final ShaderProgram sceneShaderProgram) {
		final int textureNum = 2;
		depthMap.bind(textureNum);
		sceneShaderProgram.setUniformf("u_type", 2);
		sceneShaderProgram.setUniformi("u_depthMapCube", textureNum);
		sceneShaderProgram.setUniformf("u_cameraFar", camera.far);
		sceneShaderProgram.setUniformf("u_lightPosition", position);
	}

    @Override
    public void dispose() {
        frameBuffer.dispose();
        depthMap.dispose();
    }
}
