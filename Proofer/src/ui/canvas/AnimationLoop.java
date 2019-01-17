package ui.canvas;

import java.util.ArrayList;
import java.util.List;

public class AnimationLoop implements Runnable {
	private int frameRate;
	private float interval;
	private boolean running;
	private boolean paused;
	
	private List<AnimationLoopListener> listeners;

	public AnimationLoop(int frameRate) {
		setFrameRate(frameRate);
		running = true;
		paused = false;
		
		listeners = new ArrayList<>();
	}

	@Override
	public void run() {
		System.out.println("Begin running");
		while (running && !paused) {
			float time = System.currentTimeMillis();

			for (AnimationLoopListener listener : listeners) {
				listener.onFrame();
			}

			time = System.currentTimeMillis() - time;

			// Adjust the timing correctly
			if (time < interval) {
				try {
					Thread.sleep((long) (interval - time));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("End running");
	}

	public void stop() {
		running = false;
	}

	public void resume() {
		paused = false;
	}

	public void pause() {
		paused = true;
	}

	public boolean isPaused() {
		return paused;
	}

	public int getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
		interval = 1000.0f / frameRate;
	}
	
	public List<AnimationLoopListener> getListeners() {
		return listeners;
	}
	
	public static interface AnimationLoopListener {
		public void onFrame();
	}
}