package com.hh.gdxtutorial.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.hh.gdxtutorial.shaders.CelDepthShaderProvider;
import com.hh.gdxtutorial.shaders.CelLineShaderProgram;
import com.hh.gdxtutorial.shaders.CelShaderProvider;

import java.util.Random;

public class CelShaderScreen extends FpsScreen {
	public CameraInputController camController;

	public AssetManager assetManager;
	public Array<ModelInstance> instances = new Array<ModelInstance>();

	public ModelBatch modelBatch;
	public ModelBatch depthBatch;
	public SpriteBatch spriteBatch;

	public CelLineShaderProgram celLineShader;
	public FrameBuffer fbo;
	public TextureRegion tr = new TextureRegion();
	public Environment environment;

	public Random random = new Random();

	@Override
	public void show() {
		super.show();

		// Declare camController and set it as the input processor.
		camController = new CameraInputController(camera);
		multiplexer.addProcessor(camController);

		// Declare the modelBatch, depthBatch and spriteBatch.
		// Both ModelBatches takes a custom ShaderProvider
		modelBatch = new ModelBatch(new CelShaderProvider());
		depthBatch = new ModelBatch(new CelDepthShaderProvider());
		spriteBatch = new SpriteBatch();

		// Declare the cel line shader and set it to use the celLineShader
		celLineShader = new CelLineShaderProgram();

		// Instantiate environment and add a directional light to reflect off the cubes.
		environment = new Environment();
		// environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1f, 1f, 1f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -0.5f, 0.5f, -1.0f));

		assetManager = new AssetManager();
		assetManager.load("models/cube.g3dj", Model.class);
	}
	/**
	 * Calls super.initCamera() and then resets the position.
	 * @TODO might not need to bother with super, just copy the rest from FpsScreen
	 */
	@Override
	public void initCamera() {
		super.initCamera();
		camera.position.set(0.48f, 5.67f, 2.37f);
		camera.lookAt(0, 0, 0);
		camera.update();
	}

	/**
	 * This render processs makes use of a FrameBuffer, a SpriteBatch, and two ModelBatches to 
	 * achieve the cel effect.
	 *
	 * First, the depth data is captured to the FrameBuffer with the depth batch.
	 *
	 * Second,  modelbatch, which was set to use the CelShaderProvider, is run to render an image
	 * with discretized colors for cel shading.
	 *
	 * Third, spriteBatch is set to use the celLineShader when it draws the depth data captured 
	 * in frameBuffer. The depth data is processed to give cel outlines.
	 */
	@Override
	public void render(float delta) {
		// Update the camera and models, call doneLoading if it's time.
		camController.update();
		updateModels(delta);
		if (loading && assetManager.update()) doneLoading();

		// Start the FrameBuffer, clear color and depth bits, draw
		// the scene with depthBatch.
		fbo.begin();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		runModelBatch(depthBatch, camera, instances, null);
		fbo.end();

		// Get the image drawn to fbo and store it into a TextureRegion.
		tr.setRegion(fbo.getColorBufferTexture());
		// flup the TextureRegion since it will be upside down.
		tr.flip(false, true);

		// Clear color and depth bits again and draw the scene with modelBatch.
		// We're capturing lighting too so environment is passed to runModelBatch 
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		runModelBatch(modelBatch, camera, instances, environment);

		// Set spriteBatch to use celLineShader and start
		spriteBatch.setShader(celLineShader);
		spriteBatch.begin();
		// Set the size uniform from the TextureRegion dimensions.
		celLineShader.setUniformf("u_size", tr.getRegionWidth(), tr.getRegionHeight());
		spriteBatch.draw(tr, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		spriteBatch.end();

		// Call super.render() to draw the FPS overlay.
		super.render(delta);
	}

	/**
	 * Updates the cube ModelInstances rotation relative to the delta time since
	 * the last frame was rendered.
	 */
	public void updateModels(float delta) {
		// Set model rotations.
		for (int i = 0; i < instances.size; i++) {
			// Use the instances index to give some variation to the rotation speeds
			int f = (i % 5) + 1;
			// Rotate around the Y axis.
			instances.get(i).transform.rotate(new Vector3(0, 1, 0), (90 * f / 8 * delta) % 360);
		}
	}

	/**
	 * camera and spriteBatch need to be set to use the new viewport.
	 * fbo will be disposed and reinstantiated with the new dimensions.
	 */
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();

		spriteBatch.setProjectionMatrix(new Matrix4().setToOrtho2D(0, 0, width, height));

		if (fbo != null) fbo.dispose();
		fbo = new FrameBuffer(Pixmap.Format.RGBA8888, width, height, true);
	}
	/**
	 * Call when all assets have been loaded by the AssetManager
	 */
	@Override
	public void doneLoading() {
		super.doneLoading();

		// These loops create 441 cubes with random purpleish red colors and
		// positions them in a grid around 0,0,0.
		// Going from -21 to 21 emsires the 0,0,0 centering.
		for (float x = -21.0f; x <= 21.0f; x+=2) {
			for (float y = -21.0f; y <= 21.0f; y+=2) {
				// Create a new instance of the cube
				ModelInstance instance = new ModelInstance(assetManager.get("models/cube.g3dj", Model.class));
				// Get the 'skin' material on the cube and assign it a color with random red values and random blue values
				// but no green value. The red is also much stronger than the blue.
				Material mat = instance.getMaterial("skin");
				mat.set(ColorAttribute.createDiffuse(new Color(random.nextFloat() * 2.0f, 0.0f, random.nextFloat() / 2.0f, 1.0f)));
				// Set the x and y coordinates of the cubes by using x and y
				instance.transform.setTranslation(x, y, 0.0f);
				instances.add(instance);
			}
		}
	}

	/**
	 * Dispose of everything that needs it.
	 */
	@Override
	public void dispose() {
		super.dispose();
		modelBatch.dispose();
		depthBatch.dispose();
		instances.clear();
		spriteBatch.dispose();
		celLineShader.dispose();
		fbo.dispose();
		assetManager.dispose();
	}
}