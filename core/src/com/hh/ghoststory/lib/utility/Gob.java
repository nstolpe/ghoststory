package com.hh.ghoststory.lib.utility;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by nils on 12/13/15.
 */
public class Gob {
	private String id;
	private String modelAsset;
	private Vector3 position;
	private ModelInstance modelInstance;

	public Gob(){}
	public Gob(String id, String modelAsset, Vector3 position) {
		this.id = id;
		this.modelAsset = modelAsset;
		this.position = position;
	}

	public String id() { return id; }
	public String modelAsset() { return modelAsset; }
	public Vector3 position() { return position; }
	public ModelInstance modelInstance() { return modelInstance; }

	public Gob id(String id) {
		this.id = id;
		return this;
	}

	public Gob modelAsset(String modelAsset) {
		this.modelAsset = modelAsset;
		return this;
	}

	public Gob position(Vector3 position) {
		this.position = position;
		return this;
	}

	public Gob modelInstance(ModelInstance modelInstance) {
		this.modelInstance = modelInstance;
		return this;
	}
}
