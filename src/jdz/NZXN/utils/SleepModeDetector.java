
package jdz.NZXN.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class SleepModeDetector {
	private static final Set<SleepModeListener> listeners = new HashSet<SleepModeListener>();
	private static final long DELAY_UNTIL_SLEEP_MODE = 5000L;
	private static long lastCheck = -1;

	static {
		lastCheck = System.currentTimeMillis();
		new Thread(() -> {
			new Timer().scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					if (System.currentTimeMillis() - lastCheck > DELAY_UNTIL_SLEEP_MODE)
						notifyOnWake();
					lastCheck = System.currentTimeMillis();
				}
			}, 1000L, 1000L);
		});
	}

	public static void addListener(SleepModeListener l) {
		listeners.add(l);
	}

	public static void removeListener(SleepModeListener l) {
		listeners.remove(l);
	}

	public static void notifyOnWake() {
		for (SleepModeListener l : listeners)
			l.onDeviceWake();
	}
}
