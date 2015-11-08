package com.hh.ghoststory.render.shaders;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

/**
 * Created by nils on 11/2/15.
 */
public class LocationShader extends DefaultShader {
	public final int u_color = register("u_color");

	public LocationShader(Renderable renderable) {
		super(renderable, config);
	}

	private static final Config config = new Config(
		"attribute vec3 a_position;\n" +
		"uniform mat4 u_worldTrans;\n" +
		"uniform mat4 u_projViewTrans;\n" +
		"void main() {\n" +
		"    gl_Position = u_projViewTrans * u_worldTrans * vec4(a_position, 1.0);\n" +
		"}",
		"#ifdef GL_ES \n" +
		"precision mediump float;\n" +
		"#endif\n" +
		"uniform vec3 u_color;\n" +
		"void main() {\n" +
		"    gl_FragColor = vec4(u_color, 1.0);\n" +
		"}"
	);

	public static class Config extends DefaultShader.Config {
		public Config(final String vertexShader, final String fragmentShader) {
			super(vertexShader, fragmentShader);
		}
	}

	@Override
	public void render(final Renderable renderable) {
		SilhouetteColorAttribute colorAttr = (SilhouetteColorAttribute) renderable.material.get(SilhouetteColorAttribute.ID);
		set(u_color, colorAttr.value);
		super.render(renderable);
	}

	@Override
	public boolean canRender(Renderable instance) {
		boolean foo = instance.material.has(SilhouetteColorAttribute.ID);
		return instance.material.has(SilhouetteColorAttribute.ID);
	}

	public static class SilhouetteColorAttribute extends Attribute {

		public final static String Alias = "SilhouetteColor";
		public final static long ID = register(Alias);

		public Vector3 value;

		public SilhouetteColorAttribute(final Vector3 value) {
			super(ID);
			this.value = value;
		}

		@Override
		public Attribute copy() {
			return new SilhouetteColorAttribute(value);
		}

		@Override
		protected boolean equals(Attribute other) {
			return ((SilhouetteColorAttribute)other).value == value;
		}

		@Override
		public int compareTo(Attribute o) {
			if (type != o.type) return type < o.type ? -1 : 1;
			Vector3 otherValue = ((SilhouetteColorAttribute)o).value;
			return (value == otherValue) ? 0 : 1;
		}
	}
}
