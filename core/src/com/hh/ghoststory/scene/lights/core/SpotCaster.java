package com.hh.ghoststory.scene.lights.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.hh.ghoststory.DepthMapShader;
import com.hh.ghoststory.ScreenshotFactory;

/**
 * Created by nils on 9/3/15.
 */
public class SpotCaster extends SpotLight implements Caster {
	public PerspectiveCamera camera = new PerspectiveCamera();
	// @TODO depthMapSize should be configurable, has a big influence on FPS
    public int depthMapSize = 1024;
	public FrameBuffer frameBuffer;
	public Texture depthMap;
	public ShaderProgram shaderProgram;
	public ModelBatch modelBatch;

	public SpotCaster() {
		initCamera();
	}

    public SpotCaster(final Color color, final Vector3 position, final Vector3 direction, final float intensity,
					  final float cutoffAngle, final float exponent ) {
        set(color, position, direction, intensity, cutoffAngle, exponent);
		initCamera();
    }

    protected void setCameraPosition(Vector3 position) {
        camera.position.set(position);
        camera.update();
    }
	/**
	 * @TODO near and far should be settable. Maybe use intensity
	 */
	@Override
	public void initCamera() {
		camera.fieldOfView = 120f;
		camera.viewportWidth = depthMapSize;
		camera.viewportHeight = depthMapSize;
		camera.near = 0.5f;
		camera.far = 30f;
		camera.position.set(position);
		camera.lookAt(0,0,0);
//		camera.direction.set(direction);
		camera.update();
	}

	/**
	 * It binds the depthmap to the shader and sets some normals.
	 * @TODO This is called from ShadowMapShader.applyToShader(). It's different for other light types, so it makes sense,
	 * but it might be better to handle it in the shader w/ some createprefix thing. Since Shadow might go into Play,
	 * then that logic could go there too. Maybe.
	 * @param shaderProgram
	 *
	 * For each light: light position, camera.far
	 * need depthmap size (i think)
	 * and the sampler (u_shadows, already have).
	 *
	 * use all that to process the pcf and interpolation and let's see how it works.
	 *
	 * For each light w/ casting enabled,
	 * set a light position array (if it's directional or spot we need facing vec too, but do that later)
	 *
	 * Look at shadow.fragment.glsl for handling of point vs spot lights. The handling of PCF/VSM might be better done there,
	 * then sample it here like usual.
	 */
	@Override
	public void applyToShader(final ShaderProgram shaderProgram) {
		final int textureNum = 2;
		depthMap.bind(textureNum);
		shaderProgram.setUniformf("u_type", 1);
		shaderProgram.setUniformi("u_depthMapCube", textureNum);
		shaderProgram.setUniformf("u_cameraFar", camera.far);
		shaderProgram.setUniformf("u_lightPosition", position);
//		shaderProgram.setUniformf("depthMapSize", depthMapSize);
	}

	@Override
	public void initFrameBuffer() {
		if (frameBuffer != null) frameBuffer.dispose();

		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, depthMapSize, depthMapSize, true);
	}

	private void initModelBatch() {
		modelBatch = new ModelBatch(new DefaultShaderProvider() {
			@Override
			protected com.badlogic.gdx.graphics.g3d.Shader createShader(final Renderable renderable) {
				return new DepthMapShader(renderable, shaderProgram);
			}
		});
	}

	private void initShaderProgram() {
		shaderProgram = new ShaderProgram(
			Gdx.files.internal("shaders/depth.vertex.glsl"),
			Gdx.files.internal("shaders/depth.fragment.glsl")
		);
	}

	@Override
	public void render(Array<ModelInstance> instances) {
		if (frameBuffer == null) initFrameBuffer();
		if (modelBatch == null) initModelBatch();
		if (shaderProgram == null) initShaderProgram();

		frameBuffer.begin();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		shaderProgram.begin();
		shaderProgram.setUniformf("u_cameraFar", camera.far);
		shaderProgram.setUniformf("u_lightPosition", position);
		shaderProgram.end();

		modelBatch.begin(camera);
		modelBatch.render(instances);
		modelBatch.end();
		ScreenshotFactory.saveScreenshot(frameBuffer.getWidth(), frameBuffer.getHeight(), "depthmap");
		frameBuffer.end();

		depthMap = frameBuffer.getColorBufferTexture();
	}

    /**
     * @TODO called in AbstractScreen now. Maybe Lights should handle it?
     */
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
	public SpotCaster setPosition(float positionX, float positionY, float positionZ) {
		setPosition(new Vector3(positionX, positionY, positionZ));
		return this;
	}

	@Override
	public SpotCaster setPosition(Vector3 position) {
        super.setPosition(position);
		setCameraPosition(position);
		return this;
	}

//    @Override
//    public SpotLight set (final SpotLight copyFrom) {
//        return set(copyFrom.color, copyFrom.position, copyFrom.intensity);
//    }

//    @Override
//    public SpotLight set (final Color color, final Vector3 position, final float intensity) {
//        super.set(color,position, intensity);
//        setCameraPosition(position);
//        return this;
//    }

//    @Override
//    public SpotLight set (final float r, final float g, final float b, final Vector3 position, final float intensity) {
//        return set(new Color(r,g,b, 1f), position, intensity);
//    }

//    @Override
//    public SpotLight set (final Color color, final float x, final float y, final float z, final float intensity) {
//        return set(color, new Vector3(x, y, z), intensity);
//    }

//    @Override
//    public SpotLight set (final float r, final float g, final float b, final float x, final float y, final float z, final float intensity) {
//        return set(new Color(r,g,b, 1f), new Vector3(x, y, z), intensity);
//    }

    @Override
    public boolean equals (Object obj) {
        return (obj instanceof SpotLight) ? equals((SpotLight)obj) : false;
    }

    @Override
    public boolean equals (SpotLight other) {
        return (other != null && (other == this || (color.equals(other.color) && position.equals(other.position) && intensity == other.intensity)));
    }
}
