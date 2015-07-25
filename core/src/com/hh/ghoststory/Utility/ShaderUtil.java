package com.hh.ghoststory.Utility;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

/**
 * Created by nils on 7/23/15.
 */
public class ShaderUtil {
    public static final String shaderPath = "shaders/";

    /**
     *
     * @param type String for the shader type. Vertex and fragment .glsl files should be present under the
     *             internal path corresponding to `shaderPath`. Ex: `default.vertex.glsl` and `default.fragment.glsl`
     *             would be used to create the shader program if 'default' was passed as `type`
     * @return
     */
    public static ShaderProgram getShader(String type) {
        ShaderProgram.pedantic = false;
        final ShaderProgram shaderProgram = new ShaderProgram(
                Gdx.files.internal(shaderPath + type + ".vertex.glsl"),
                Gdx.files.internal(shaderPath + type + ".fragment.glsl")
        );

        if (!shaderProgram.isCompiled()) {
            System.err.println("Error with shader " + type + ": " + shaderProgram.getLog());
            System.exit(1);
        } else {
            Gdx.app.log("init", "Shader " + type + " compiled " + shaderProgram.getLog());
        }

        return shaderProgram;
    }
}
