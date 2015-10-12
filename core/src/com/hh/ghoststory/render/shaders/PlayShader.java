package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

/**
 * Created by nils on 8/24/15.
 */
public class PlayShader extends DefaultShader {
	public static final int textureNum = 4;
//	private static final String vertex = Gdx.files.internal("shaders/default.vertex.glsl").readString();
//	private static final String fragment = Gdx.files.internal("shaders/default.fragment.glsl").readString();
	private static final Config config = new Config(Gdx.files.internal("shaders/default.vertex.glsl").readString(), Gdx.files.internal("shaders/default.fragment.glsl").readString());
	public static ModelInstance alphaInstance;

	public PlayShader(Renderable renderable) {
		super(renderable, config, createPrefix(renderable, config));
	}

	@Override
	public void begin(final Camera camera, final RenderContext context) {
		super.begin(camera, context);
		program.setUniformi("u_shadows", textureNum);
		program.setUniformf("u_screenWidth", Gdx.graphics.getWidth());
		program.setUniformf("u_screenHeight", Gdx.graphics.getHeight());
	}

	@Override
	public boolean canRender (final Renderable renderable) {
		boolean val = super.canRender(renderable);

//		if (!val) return val;
//
//		// val is true if it passes above.
//		if (alphaInstance != null) {
//			Renderable ren = new Renderable();
//			alphaInstance.getRenderable(ren);
//			if (ren != renderable) val = false;
//			// reset until set in render loop again.
//			else alphaInstance = null;
//		}

		return val;
	}
	public static String createPrefix(final Renderable renderable, final Config config) {
//		final Attributes attributes = combineAttributes(renderable);
//		final Environment lights = renderable.environment;
//		final DirectionalLightsAttribute dla = attributes.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
//		final Array<DirectionalLight> dirs = dla == null ? null : dla.lights;
//		final PointLightsAttribute pla = attributes.get(PointLightsAttribute.class, PointLightsAttribute.Type);
//		final Array<PointLight> points = pla == null ? null : pla.lights;
//		final SpotLightsAttribute sla = attributes.get(SpotLightsAttribute.class, SpotLightsAttribute.Type);
//		final Array<SpotLight> spots = sla == null ? null : sla.lights;
		String prefix = DefaultShader.createPrefix(renderable, config);
		// For each light: light position, camera.far
		// need depthmap size (i think)
		// and the sampler (u_shadows, already have).
		//
		// use all that to process the pcf and interpolation and let's see how it works.
		//
		// For each light w/ casting enabled,
		// set a light position array (if it's directional or spot we need facing vec too, but do that later)
		//
		// Look at shadow.fragment.glsl for handling of point vs spot lights. The handling of PCF/VSM might be better done there,
		// then sample it here like usual.
//		if (alphaInstance != null) {
//			Renderable ren = new Renderable();
//			alphaInstance.getRenderable(ren);
//			if (ren == renderable) prefix += "#define alphaFlag\n";
//		}
		return prefix;
	}

	private final static Attributes tmpAttributes = new Attributes();
	private static final Attributes combineAttributes (final Renderable renderable) {
		tmpAttributes.clear();
		if (renderable.environment != null) tmpAttributes.set(renderable.environment);
		if (renderable.material != null) tmpAttributes.set(renderable.material);
		return tmpAttributes;
	}

	public static class Config extends DefaultShader.Config {
		public Config(final String vertexShader, final String fragmentShader) {
			super(vertexShader, fragmentShader);
			numSpotLights = 5;
		}
	}
}
