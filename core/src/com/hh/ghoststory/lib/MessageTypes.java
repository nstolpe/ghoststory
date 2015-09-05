package com.hh.ghoststory.lib;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by nils on 8/28/15.
 */
public class MessageTypes {
	public static class Screen {
		// ShadowRenderer
		public final static int INIT_SHADOW_BUFFER    = 0;
		// CameraController. Was removed. Its stuff went to PlayScreen.
		public final static int REQUEST_ACTIVE_CAMERA = 1;
		public final static int SEND_ACTIVE_CAMERA    = 2;
		public final static int GET_CAMERA_POSITION   = 3;
		public final static int GET_CAMERA_DIRECTION  = 4;
		public final static int GET_PICK_RAY          = 5;
		public final static int TRANSLATE_CAMERA      = 7;
	}

	public final static int POSITION_CAMERA       = 6;
}