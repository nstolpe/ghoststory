package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;

/**
 * Created by nils on 11/2/15.
 */
public class LocationShader extends DefaultShader {
	public final int u_color = register("u_color");
	public LocationShader(Renderable renderable) {
		super(renderable);
	}
	private static final Config config = new Config(Gdx.files.internal("shaders/default.vertex.glsl").readString(), Gdx.files.internal("shaders/default.fragment.glsl").readString());

	@Override
	public void render(final Renderable renderable) {
//		set(u_color, renderable.material.g)
	}
}
