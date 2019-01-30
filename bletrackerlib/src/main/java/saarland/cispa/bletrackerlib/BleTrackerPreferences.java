package saarland.cispa.bletrackerlib;

public class BleTrackerPreferences {


    //TODO: include all preferences
    private boolean sendToCispa = true;

    public BleTrackerPreferences() {

    }

    public BleTrackerPreferences(boolean sendToCispa) {
        this.sendToCispa = sendToCispa;
    }

    public boolean isSendToCispa() {
        return sendToCispa;
    }

    public void setSendToCispa(boolean sendToCispa) {
        this.sendToCispa = sendToCispa;
    }
}
