package com.hh.ghoststory.screen.controllers;

import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.hh.ghoststory.screen.core.AbstractScreen;

/**
 * Created by nils on 8/27/15.
 */
public class InputController implements Telegraph {
	private InputMultiplexer multiplexer = new InputMultiplexer();
	private AbstractScreen screen;
	private MessageDispatcher messageDispatcher;

	public InputController(AbstractScreen screen, MessageDispatcher messageDispatcher) {
		this.screen = screen;
		this.messageDispatcher = messageDispatcher;
	}

	public InputController(AbstractScreen screen) {
		this(screen, screen.messageDispatcher);
	}
	
	@Override
	public boolean handleMessage(Telegram msg) {
		return false;
	}
}
