package com.hh.gdxtutorial.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.hh.gdxtutorial.ai.Messages;

/**
 * Created by nils on 6/3/16.
 */
public class MainMenuScreen extends AbstractScreen {
	private Stage stage;
	private BitmapFont font;
	private TextButton celShaderScreenButton;
	private TextButton gaussianBlurShaderScreenButton;
	private TextButton turnEngineScreenButton;
	private TextButton tiltShiftShaderScreenButton;
	private TextButton turnSystemScreenButton;
	private TextButtonStyle buttonStyle;
	private Table buttonColumn;

	@Override
	public void show() {
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

		font = new BitmapFont();
		buttonStyle = new TextButtonStyle();
		buttonStyle.font = font;
		font.getData().setScale(2.0f, 2.0f);
		buttonStyle.fontColor = new Color(1.0f, 0.0f, 0.0f, 1.0f);

		celShaderScreenButton = new TextButton("CelShaderScreen", buttonStyle);
		gaussianBlurShaderScreenButton = new TextButton("GaussianBlurShaderScreen", buttonStyle);
		tiltShiftShaderScreenButton = new TextButton("TiltShiftShaderScreen", buttonStyle);
		turnEngineScreenButton = new TextButton("TurnEngineScreen", buttonStyle);
		turnSystemScreenButton = new TextButton("TurnSystemScreen", buttonStyle);

		celShaderScreenButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MessageManager.getInstance().dispatchMessage(0, Messages.CHANGE_SCREEN, new CelShaderScreen());
			}
		});
		gaussianBlurShaderScreenButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MessageManager.getInstance().dispatchMessage(0, Messages.CHANGE_SCREEN, new GaussianBlurShaderScreen());
			}
		});
		tiltShiftShaderScreenButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MessageManager.getInstance().dispatchMessage(0, Messages.CHANGE_SCREEN, new TiltShiftShaderScreen());
			}
		});
		turnEngineScreenButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MessageManager.getInstance().dispatchMessage(0, Messages.CHANGE_SCREEN, new TurnEngineScreen());
			}
		});
		turnSystemScreenButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				MessageManager.getInstance().dispatchMessage(0, Messages.CHANGE_SCREEN, new TurnSystemScreen());
			}
		});

		buttonColumn = new Table();
		buttonColumn.setFillParent(true);
		buttonColumn.add(celShaderScreenButton);
		buttonColumn.row();
		buttonColumn.add(gaussianBlurShaderScreenButton);
		buttonColumn.row();
		buttonColumn.add(tiltShiftShaderScreenButton);
		buttonColumn.row();
		buttonColumn.add(turnEngineScreenButton);
		buttonColumn.row();
		buttonColumn.add(turnSystemScreenButton);

		stage.addActor(buttonColumn);
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		stage.draw();
		MessageManager.getInstance().update();
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void dispose() {
		super.dispose();
		stage.dispose();
		font.dispose();
	}
}
