package com.homecare.app.model;

public class ElderlyConfig {
    private int hrCollectInterval;
    private int hrFastThreshold;
    private int hrSlowThreshold;
    private int sittingThreshold;
    private int lyingThreshold;

    public ElderlyConfig(int hrCollectInterval, int hrFastThreshold, int hrSlowThreshold, int sittingThreshold, int lyingThreshold) {
        this.hrCollectInterval = hrCollectInterval;
        this.hrFastThreshold = hrFastThreshold;
        this.hrSlowThreshold = hrSlowThreshold;
        this.sittingThreshold = sittingThreshold;
        this.lyingThreshold = lyingThreshold;
    }

    public int getHrCollectInterval() {
        return hrCollectInterval;
    }

    public void setHrCollectInterval(int hrCollectInterval) {
        this.hrCollectInterval = hrCollectInterval;
    }

    public int getHrFastThreshold() {
        return hrFastThreshold;
    }

    public void setHrFastThreshold(int hrFastThreshold) {
        this.hrFastThreshold = hrFastThreshold;
    }

    public int getHrSlowThreshold() {
        return hrSlowThreshold;
    }

    public void setHrSlowThreshold(int hrSlowThreshold) {
        this.hrSlowThreshold = hrSlowThreshold;
    }

    public int getSittingThreshold() {
        return sittingThreshold;
    }

    public void setSittingThreshold(int sittingThreshold) {
        this.sittingThreshold = sittingThreshold;
    }

    public int getLyingThreshold() {
        return lyingThreshold;
    }

    public void setLyingThreshold(int lyingThreshold) {
        this.lyingThreshold = lyingThreshold;
    }
}
