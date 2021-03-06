package com.hh.gdxtutorial.entity.systems;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.hh.gdxtutorial.ai.Messages;
import com.hh.gdxtutorial.entity.components.Mappers;
import com.hh.gdxtutorial.shaders.CelDepthShaderProvider;
import com.hh.gdxtutorial.shaders.CelLineShaderProgram;
import com.hh.gdxtutorial.shaders.CelShaderProvider;

/**
 * Draws the scene with a cel shader style.
 */
public class CelRenderer extends ModelBatchRenderer implements Telegraph {
	private ModelBatch depthBatch = new ModelBatch(new CelDepthShaderProvider());
	private SpriteBatch spriteBatch = new SpriteBatch();
	private CelLineShaderProgram celLineShader = new CelLineShaderProgram();
	private FrameBuffer fbo;
	private TextureRegion tr = new TextureRegion();


	public CelRenderer(Camera cam, Environment env) {
		super(new ModelBatch(new CelShaderProvider()), cam, env);

		resize(new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		// needs to resize too.
		clearColor.set(0.5f, 0.5f, 0.5f, 1.0f);
		MessageManager.getInstance().addListener(this, Messages.SCREEN_RESIZE);

	}

	/**
	 * Triggered when SCREEN_RESIZE message is received.
	 * @param dimensions
	 */
	private void resize(Vector2 dimensions) {
		camera.viewportWidth = dimensions.x;
		camera.viewportHeight = dimensions.y;
		camera.update();
		if (fbo != null) fbo.dispose();
		fbo = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	}
	/**
	 * Run the modelBatch
	 * @param deltaTime
	 */
	@Override
	public void update(float deltaTime) {
		Gdx.gl.glClearColor(clearColor.r, clearColor.g, clearColor.b, clearColor.a);

		fbo.begin();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		depthBatch.begin(camera);
		for (Entity e : entities) {
			ModelInstance instance = Mappers.MODEL_INSTANCE.get(e).instance();
			instance.transform.setTranslation(Mappers.POSITION.get(e).position());
			depthBatch.render(instance, env);
		}
		depthBatch.end();
		fbo.end();

		tr.setRegion(fbo.getColorBufferTexture());
		tr.flip(false, true);

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		modelBatch.begin(camera);
		for (Entity e : entities) {
			ModelInstance instance = Mappers.MODEL_INSTANCE.get(e).instance();
			instance.transform.setTranslation(Mappers.POSITION.get(e).position());
			modelBatch.render(instance, env);
		}
		modelBatch.end();

		spriteBatch.setShader(celLineShader);
		spriteBatch.begin();
		celLineShader.setUniformf("u_size", tr.getRegionWidth(), tr.getRegionHeight());
		spriteBatch.draw(tr, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch.end();
	}
	/**
	 * Disposables are managed here.
	 */
	@Override
	public void dispose() {
		super.dispose();
		depthBatch.dispose();
		fbo.dispose();
	}
	/**
	 * Handles incoming messages
	 * @param msg
	 * @return
	 */
	@Override
	public boolean handleMessage(Telegram msg) {
		switch (msg.message) {
			case Messages.SCREEN_RESIZE:
				resize((Vector2) msg.extraInfo);
				break;
			default:
				return false;
		}
		return true;
	}
}
