package com.hh.ghoststory.lib;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nils on 8/28/15.
 */
public enum MessageType {
	// ShadowRenderer
	INIT_SHADOW_BUFFER(0),

	// CameraController
	GET_ACTIVE_CAMERA(1),
	GET_CAMERA_POSITION(2),
	GET_CAMERA_DIRECTION(3),
	GET_PICK_RAY(4),
	POSITION_CAMERA(5),
	TRANSLATE_CAMERA(6);

	private int i;
	private static final Map lookup = new HashMap();

	MessageType(int i) { this.i = i; }


	static {
		for(MessageType d : MessageType.values()) lookup.put(d.val(), d);
	}

	public int val() { return this.i; }
	public static MessageType get(int val) {
		return (MessageType) lookup.get(val);
	}
}