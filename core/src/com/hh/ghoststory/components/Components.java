package com.hh.ghoststory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by nils on 9/2/15.
 */
public class Components {
	public static class IDComp extends Component {
		public String id;

		public IDComp id(String id) {
			this.id = id;
			return this;
		}
	}

	public static class NameComp extends Component {
		public String name;
		public NameComp name(String name) {
			this.name = name;
			return this;
		}
	}

	public static class GeometryComp extends Component {
		public String file;
		public GeometryComp file(String file) {
			this.file = file;
			return this;
		}
	}

	public static class AmbientComp extends Component {
		public ColorAttribute colorAttribute;
		public AmbientComp colorAttribute(ColorAttribute colorAttribute) {
			this.colorAttribute = colorAttribute;
			return this;
		}
	}

	public static class PositionComp extends Component {
		public Vector3 position;
		public PositionComp position(Vector3 position) {
			this.position = position;
			return this;
		}
	}

	public static class ColorComp extends Component {
		public Color color;
		public ColorComp color(Color color) {
			this.color = color;
			return this;
		}
	}

	public static class IntensityComp extends Component {
		public float intensity;
		public IntensityComp intensity(float intensity) {
			this.intensity = intensity;
			return this;
		}
	}

	public static class AnimationComp extends Component{
		public ObjectMap<String, ObjectMap<String, Object>> animations;
		public AnimationController controller;
		public AnimationComp animations(Array<ObjectMap<String, Object>> animations) {
			for (ObjectMap<String, Object> animation : animations)
				this.animations.put((String) animation.get("id"), animation);
			return this;
		}
		public void init(ModelInstance instance) {
			controller = new AnimationController(instance);
		}
	}
}