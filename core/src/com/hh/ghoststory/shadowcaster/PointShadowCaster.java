package com.hh.ghoststory.shadowcaster;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.glutils.FrameBufferCubemap;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.hh.ghoststory.ScreenshotFactory;

/**
 * Created by nils on 7/21/15.
 */
public class PointShadowCaster extends AbstractShadowCaster implements Disposable {
    public PointLight light = new PointLight();
    public FrameBufferCubemap frameBuffer;
    public Cubemap depthMap;

    /**
     * @param light
     */
    public PointShadowCaster(PointLight light) {
	    this.light.set(light);
	    this.position.set(light.position);
	    modelBatch = new ModelBatch(Gdx.files.internal("shaders/default.vertex.glsl"), Gdx.files.internal("shaders/default.fragment.glsl"));
	    setupCamera();
	    setupFrameBuffer();
    }

	public PointShadowCaster(PointLight light, Vector3 position) {
		this.position.set(position);
		this.light = light.setPosition(position);
		modelBatch = new ModelBatch(Gdx.files.internal("shaders/default.vertex.glsl"), Gdx.files.internal("shaders/default.fragment.glsl"));
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
        camera.near = 4f;
        camera.far = 70;
        camera.position.set(position);
        camera.update();
    }

    @Override
    public void render(Array<ModelInstance> instances, Environment environment) {
	    frameBuffer.begin();
	    while( frameBuffer.nextSide() ) {
		    frameBuffer.getSide().getUp(camera.up);
		    frameBuffer.getSide().getDirection(camera.direction);
		    camera.update();
		    Gdx.gl.glClearColor(0, 0, 0, 1);
		    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		    modelBatch.begin(camera);
//		    modelBatch.render(instances, environment);
		    modelBatch.render(instances);
		    modelBatch.end();
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
    public void dispose() {
        frameBuffer.dispose();
        depthMap.dispose();
    }
}
