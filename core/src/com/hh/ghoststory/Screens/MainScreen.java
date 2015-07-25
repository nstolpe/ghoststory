package com.hh.ghoststory.Screens;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.alpha;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeIn;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.fadeOut;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.hh.ghoststory.GhostStory;

public class MainScreen extends AbstractScreen {
	protected Table table;
	private Stage stage;
	private SpriteBatch batch;
	private MainScreenButton startButton;
	private MainScreenButton createButton;
	private EventListener startButtonListener;
	private EventListener createButtonListener;

	public MainScreen(GhostStory game) {
		super(game);

		this.stage = new Stage();
//		this.stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		Gdx.input.setInputProcessor(stage);
		table = new Table();
		table.setFillParent(true);
		table.debug();
		stage.addActor(table);
		startButton = new MainScreenButton("Play Ghost Story");
		table.add(startButton.button).width(500).height(100);
		table.row();
		createButton = new MainScreenButton("Create Ghost Character");
		table.add(createButton.button).width(500).height(100);
	}

	/*
	 * Adds a fade in action to the screen and enables input listeners when it's finished.
	 * @see com.hh.ghoststory.screens.AbstractScreen#show()
	 */
	@Override
	public void show() {
		super.show();
		stage.addAction(
				sequence(
						alpha(0f, 0f),
						fadeIn(0.3f),
						new Action() {
							@Override
							public boolean act(float delta) {
								setInputListeners();
								return true;
							}
						}
				)
		);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
		font.dispose();
		stage.dispose();
	}

	@Override
	public void resize(int width, int height) {
//		stage.setViewport(width, height, true);
	}

	/*
	 * This is where you'll set input listeners.
	 */
	public void setInputListeners() {
		startButtonListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				startButton.button.removeListener(startButtonListener);
				createButton.button.removeListener(createButtonListener);
				stage.addAction(
						sequence(
								fadeOut(0.3f),
								new Action() {
									@Override
									public boolean act(float delta) {
										game.setScreen(game.getGameScreen());
										return true;
									}
								}
						)
				);
			}
		};

		createButtonListener = new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				startButton.button.removeListener(startButtonListener);
				createButton.button.removeListener(createButtonListener);
				stage.addAction(
						sequence(
								fadeOut(0.3f),
								new Action() {
									@Override
									public boolean act(float delta) {
										game.setScreen(game.getCreateScreen());
										return true;
									}
								}
						)
				);
			}
		};

		startButton.button.addListener(startButtonListener);
		createButton.button.addListener(createButtonListener);
	}
}
