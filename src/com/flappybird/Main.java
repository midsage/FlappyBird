package com.flappybird;

import static org.lwjgl.glfw.GLFW.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import com.flappybird.graphics.Shader;
import com.flappybird.input.Input;
import com.flappybird.level.Level;
import com.flappybird.math.Matrix4f;

public class Main implements Runnable {

	private int width = 1280;
	private int height = 720;

	private Thread thread;
	private boolean running = false;
	
	private long window;
	
	private Level level;

	public void start() {
		running = true;
		thread = new Thread(this, "Game");
		thread.start();

	}

	private void init() {

		if (glfwInit() != true) {

			System.err.println("GLFW initialization failed!");

			// Handle the GLFWInit did not work.
			return;
		}

		glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
		window = glfwCreateWindow(width, height, "FlappyBird", NULL, NULL);
		
		if (window == NULL) {
			// TO DO...
			return;
		}

		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(window, (vidmode.width() - width / 2 - 750), (vidmode.height() - height) / 2);
		
		//System.out.println("Vid Mode Height " + vidmode.height());
		//System.out.println("Vid Mode Width " + vidmode.width());
		//System.out.println("Height " + height);
		//System.out.println("Width " + width);
		
		glfwSetKeyCallback(window, new Input());

		glfwMakeContextCurrent(window);
		glfwShowWindow(window);
		GL.createCapabilities(); // Same as GLContext.CreateFromCurrent
		
		//glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		glEnable(GL_DEPTH_TEST);
		glActiveTexture(GL_TEXTURE1);
		System.out.println("OpenGL: " + glGetString(GL_VERSION));
		Shader.loadAll();
		
	
		Matrix4f pr_matrix = Matrix4f.orthographic(-10.0f, 10.0f, -10.0f * 9.0f / 16.0f, 10.0f * 9.0f / 16.0f, -1.0f, 1.0f);
		Shader.BG.setUniformMat4f("pr_matrix", pr_matrix);
		Shader.BG.setUniform1i("tex", 1);
		
		Shader.BIRD.setUniformMat4f("pr_matrix", pr_matrix);
		Shader.BIRD.setUniform1i("tex", 1);
		
		
		level = new Level();
		
		

	}

	public void run() {
		init();
		
		long lastTime = System.nanoTime();
		double delta = 0.0;
		double ns = 1000000000.0 / 60.0;
		int updates = 0;
		int frames = 0;
		long timer = System.currentTimeMillis();
		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if(delta >= 1.0) {
				update();
				updates++;
				delta--;
				
			}
			render();
			frames++;
			if(System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
				System.out.println("Updates = " + updates + " ,  Frames = " + frames);
				updates = 0;
				frames = 0;
			}

			if (glfwWindowShouldClose(window) == true) {
				running = false;
			}

		}
		
		//glfwDestroyWindow(window);
		//glfwTerminate();
	}

	private void update() {
		//System.out.println("LWJGL Version: " + Version.getVersion());
		glfwPollEvents();
		level.update();
		
	
			
		}


	private void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		level.render();
		int error = glGetError();
		if(error != GL_NO_ERROR) {
			System.out.println(error);
		}
		glfwSwapBuffers(window);

	}

	public static void main(String[] args) {
		new Main().start();

	}

}
