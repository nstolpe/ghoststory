package com.hh.ghoststory.Shaders;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;

/**
 * Created by nils on 7/25/15.
 */
public class ShadowShader extends DefaultShader {
    public ShadowShader (final Renderable renderable, final Config config) {
        super(renderable, config, createPrefix(renderable, config));
    }
    public static String createPrefix(final Renderable renderable, final Config config) {
        String prefix = DefaultShader.createPrefix(renderable, config);
//        prefix += "#define ambientCubemapFlag\n";
        return prefix;
    }
}
