package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.DirectionalLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.PointLightsAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.SpotLightsAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.utils.Array;

/**
 * Created by nils on 8/24/15.
 */
public class PlayShader extends DefaultShader {
	public static final int textureNum = 4;
//	private static final String vertex = Gdx.files.internal("shaders/default.vertex.glsl").readString();
//	private static final String fragment = Gdx.files.internal("shaders/default.fragment.glsl").readString();
	private static final Config config = new Config(Gdx.files.internal("shaders/default.vertex.glsl").readString(), Gdx.files.internal("shaders/default.fragment.glsl").readString());


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
		final Attributes attributes = combineAttributes(renderable);
		final Environment lights = renderable.environment;
		final DirectionalLightsAttribute dla = attributes.get(DirectionalLightsAttribute.class, DirectionalLightsAttribute.Type);
		final Array<DirectionalLight> dirs = dla == null ? null : dla.lights;
		final PointLightsAttribute pla = attributes.get(PointLightsAttribute.class, PointLightsAttribute.Type);
		final Array<PointLight> points = pla == null ? null : pla.lights;
		final SpotLightsAttribute sla = attributes.get(SpotLightsAttribute.class, SpotLightsAttribute.Type);
		final Array<SpotLight> spots = sla == null ? null : sla.lights;
		String prefix = DefaultShader.createPrefix(renderable, config);
		return prefix;
	}

	private final static Attributes tmpAttributes = new Attributes();
	private static final Attributes combineAttributes (final Renderable renderable) {
		tmpAttributes.clear();
		if (renderable.environment != null) tmpAttributes.set(renderable.environment);
		if (renderable.material != null) tmpAttributes.set(renderable.material);
		return tmpAttributes;
	}
}
