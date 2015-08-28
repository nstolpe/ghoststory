package com.hh.ghoststory.lib;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nils on 8/28/15.
 */
public enum MessageType {
	UPDATE_BUFFER(1);

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