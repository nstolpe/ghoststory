package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.shaders.DepthShader;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;

/**
 * Created by nils on 10/26/15.
 */
public class FrontFaceDepthShaderProvider extends BaseShaderProvider {
	public final DepthShader.Config config;

	public FrontFaceDepthShaderProvider (final DepthShader.Config config) {
		this.config = (config == null) ? new DepthShader.Config() : config;
	}

	public FrontFaceDepthShaderProvider (final String vertexShader, final String fragmentShader) {
		this(new DepthShader.Config(vertexShader, fragmentShader));
	}

	public FrontFaceDepthShaderProvider (final FileHandle vertexShader, final FileHandle fragmentShader) {
		this(vertexShader.readString(), fragmentShader.readString());
	}

	public FrontFaceDepthShaderProvider () {
		this(null);
	}

	@Override
	protected Shader createShader (final Renderable renderable) {
		return new DepthShader(renderable, config);
	}
}
