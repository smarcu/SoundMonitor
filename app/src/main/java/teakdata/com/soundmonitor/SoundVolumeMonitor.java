package teakdata.com.soundmonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Created by silviu on 31/01/16.
 */
public class SoundVolumeMonitor {

    private Deque<Double> volumeHistory;
    private int historySize;
    private double total;

    public SoundVolumeMonitor(int historySize) {
        this.historySize = historySize;
        volumeHistory = new ConcurrentLinkedDeque<>();
    }

    public void addVolume(double value) {
        if (volumeHistory.size() >= historySize) {
            double remove = volumeHistory.removeFirst();
            total -= remove;
        }

        volumeHistory.add(value);
        total += value;
    }

    public double getMax() {
        return Collections.max(volumeHistory);
    }

    public double getAvg() {
        return total/volumeHistory.size();
    }

    public void reset() {
        this.total = 0;
        this.volumeHistory.clear();
    }
}
