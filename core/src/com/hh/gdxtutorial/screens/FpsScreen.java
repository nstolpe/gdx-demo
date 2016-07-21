package com.hh.gdxtutorial.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.hh.gdxtutorial.ai.Messages;

/**
 * Extend from this class if you want to have an FPS 2d overlay. Call each implemented method
 * from the derived class via super.
 *
 * Make sure `super.render(delta)` comes at the end of the derived class's `render()` call so
 * the 2d stage is drawn on top.
 */
public abstract class FpsScreen extends AbstractScreen {
	protected Stage stage;
	protected PerspectiveCamera camera;
	protected BitmapFont font;
	protected Label label;
	protected StringBuilder stringBuilder;
	protected Button mainMenuScreenButton;
	protected TextButtonStyle buttonStyle;
	protected Table table;
	protected InputMultiplexer multiplexer;

	/**
	 * Sets up everything we need to draw the FPS overlay and 3D scene.
	 */
	@Override
	public void show() {
		stringBuilder = new StringBuilder();
		initCamera();
		initStage();

		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(stage);
		Gdx.input.setInputProcessor(multiplexer);
	}

	/**
	 * Initializes a new full viewport PerspectiveCamera with a 67 deg 
	 * field of view. The camera is position at at x:20, y: 20, z: 20.
	 */
	protected void initCamera() {
		camera = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(20.0f, 20.0f, 20.0f);
		camera.lookAt(0, 0, 0);
		camera.near = 1;
		camera.far = 1000;
		camera.update();
	}
	/**
	 * Initializes the Stage for drawing FPS data and the MainMenuScreenButton
	 * in the 2D overlay.
	 */
	protected void initStage() {
		stage = new Stage(new ScreenViewport());
		font = new BitmapFont();
		label = new Label(" ", new Label.LabelStyle(font, Color.WHITE));

		// create a new button style and add the font
		buttonStyle = new TextButton.TextButtonStyle();
		buttonStyle.font = font;
		// font.getData().setScale(2.0f, 2.0f);
		buttonStyle.fontColor = new Color(1.0f, 0.0f, 0.0f, 1.0f);

		mainMenuScreenButton = new TextButton("MainMenuScreen", buttonStyle);

		// add the listener that will take the player back to the 
		// main menu to mainMenuScreenButton.
		mainMenuScreenButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MessageManager.getInstance().dispatchMessage(0, Messages.CHANGE_SCREEN, new MainMenuScreen());
			}
		});

		// create table that will fill it's parent from top left.
		table = new Table();
		table.left().top();
		table.setFillParent(true);

		// put the label that will hold the FPS data at the top left.
		table.add(label);
		// put the main menu button at the top right
		table.add(mainMenuScreenButton).expandX().right();
		// add the table to the stage.
		stage.addActor(table);
	}

	/**
	 * Resizes the stage. Should be called from end of derived render().
	 */
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	/**
	 * Draws the stage.
	 */
	@Override
	public void render(float delta) {
		// updates the FPS and draws the stage.
		stringBuilder.setLength(0);
		stringBuilder.append(" FPS: ").append(Gdx.graphics.getFramesPerSecond());
		label.setText(stringBuilder);
		stage.draw();
	}

	/**
	 * Get rid of our disposables.
	 */
	@Override
	public void dispose() {
		font.dispose();
		stage.dispose();
	}
}
