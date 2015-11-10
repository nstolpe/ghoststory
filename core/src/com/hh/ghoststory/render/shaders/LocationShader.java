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
	public final int u_black = register("u_black");

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
		"uniform int u_black;\n" +
		"void main() {\n" +
		"    if (u_black == true) {\n" +
		"        gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);\n" +
		"    } else {\n" +
		"        gl_FragColor = vec4(u_color, 1.0);\n" +
		"    }\n" +
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

		if (renderable.material.has(SilhouetteAttribute.ID))
			set(u_black, 1);
		else
			set(u_black, 0);

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

	public static class SilhouetteAttribute extends Attribute {

		public final static String Alias = "Silhouette";
		public final static long ID = register(Alias);

		public int value;

		public SilhouetteAttribute(final int value) {
			super(ID);
			this.value = value;
		}

		@Override
		public Attribute copy() {
			return new SilhouetteAttribute(value);
		}

		@Override
		protected boolean equals(Attribute other) {
			return ((SilhouetteAttribute)other).value == value;
		}

		@Override
		public int compareTo(Attribute o) {
			if (type != o.type) return type < o.type ? -1 : 1;
			int otherValue = ((SilhouetteAttribute)o).value;
			return (value == otherValue) ? 0 : value > otherValue ? 1 : -1;
		}
	}
}
