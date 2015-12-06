package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;

/**
 * Created by nils on 12/2/15.
 */
public class SilhouetteShader extends DefaultShader {
	public final int u_offset = register("u_offset");
	private static final Config config = new Config(Gdx.files.internal("shaders/silhouette.vertex.glsl").readString(), Gdx.files.internal("shaders/silhouette.fragment.glsl").readString());

	public SilhouetteShader(Renderable renderable) {
		super(renderable, config);
	}

	@Override
	public void render(final Renderable renderable) {
		set(u_offset, 0.5f);
		super.render(renderable);
	}
}