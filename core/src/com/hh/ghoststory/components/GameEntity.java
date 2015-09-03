package com.hh.ghoststory.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Created by nils on 9/2/15.
 */
public class GameEntity {
	public static class IDComponent extends Component {
		public String id;

		public IDComponent id(String id) {
			this.id = id;
			return this;
		}
	}

	public static class NameComponent extends Component {
		public String name;
		public NameComponent name(String name) {
			this.name = name;
			return this;
		}
	}

	public static class GeometryComponent extends Component {
		public String file;
		public GeometryComponent file(String file) {
			this.file = file;
			return this;
		}
	}

	public static class AmbientComponent extends Component {
		public ColorAttribute colorAttribute;
		public AmbientComponent colorAttribute(ColorAttribute colorAttribute) {
			this.colorAttribute = colorAttribute;
			return this;
		}
	}

	public static class PositionComponent extends Component {
		public Vector3 position;
		public PositionComponent  position(Vector3 position) {
			this.position = position;
			return this;
		}
	}

	public static class AnimationComponent extends Component{
		public ObjectMap<String, ObjectMap<String, Object>> animations;
		public AnimationController controller;
		public AnimationComponent animations(Array<ObjectMap<String, Object>> animations) {
			for (ObjectMap<String, Object> animation : animations)
				this.animations.put((String) animation.get("id"), animation);
			return this;
		}
		public void init(ModelInstance instance) {
			controller = new AnimationController(instance);
		}
	}
}