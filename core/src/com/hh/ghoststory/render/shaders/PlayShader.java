package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.MathUtils;
import com.hh.ghoststory.lib.utility.UserData;

/**
 * Created by nils on 8/24/15.
 */
public class PlayShader extends DefaultShader {
	public static final int textureNum = 4;
//	private static final String vertex = Gdx.files.internal("shaders/default.vertex.glsl").readString();
//	private static final String fragment = Gdx.files.internal("shaders/default.fragment.glsl").readString();
	private static final Config config = new Config(Gdx.files.internal("shaders/default.vertex.glsl").readString(), Gdx.files.internal("shaders/default.fragment.glsl").readString());
	public boolean alpha = false;

	public void alpha(boolean alpha) {
		this.alpha = alpha;
	}

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
		if (renderable.material.has(AlphaAttribute.ID)) prefix += "#define alphaFlag\n";

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

	public static class AlphaAttribute extends Attribute {

		public final static String Alias = "Alpha";
		public final static long ID = register(Alias);

		public float value;

		public AlphaAttribute (final float value) {
			super(ID);
			this.value = value;
		}

		@Override
		public Attribute copy () {
			return new AlphaAttribute(value);
		}

		@Override
		protected boolean equals (Attribute other) {
			return ((AlphaAttribute)other).value == value;
		}

		@Override
		public int compareTo (Attribute o) {
			if (type != o.type) return type < o.type ? -1 : 1;
			float otherValue = ((AlphaAttribute)o).value;
			return MathUtils.isEqual(value, otherValue) ? 0 : (value < otherValue ? -1 : 1);
		}
	}
}
