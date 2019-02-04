package saarland.cispa.bletrackerlib.remote;

public class RemotePreferences {

    private SendMode sendMode = SendMode.DO_ONLY_SEND_IF_BEACONS_HAVE_GPS;
    private int sendInterval = 15 * 1000;
    private int minConfirmations = 1;

    /**
     * get the send mode
     * default DO_ONLY_SEND_IF_BEACONS_HAVE_GPS
     * @return the send mode
     */
    public SendMode getSendMode() {
        return sendMode;
    }

    /**
     * set the send mode
     * default DO_ONLY_SEND_IF_BEACONS_HAVE_GPS
     * @param sendMode the send mode
     */
    public void setSendMode(SendMode sendMode) {
        this.sendMode = sendMode;
    }

    /**
     * In which interval the beacons will be sent if they are not new to us
     * default 15,000ms (15s)
     * @return the time in ms
     */
    public int getSendInterval() {
        return sendInterval;
    }

    /**
     * In which interval we should send beacons we had already seen?
     * default 15,000ms (15s)
     * @param sendInterval in ms
     */
    public void setSendInterval(int sendInterval) {
        this.sendInterval = sendInterval;
    }

    /**
     * get the number of times a beacon has been seen to be approved
     * default 1
     * @return the number
     */
    public int getMinConfirmations() {
        return minConfirmations;
    }

    /**
     * set the number of times a beacon has to be seen to be approved
     * default 1
     * @param minConfirmations the number
     */
    public void setMinConfirmations(int minConfirmations) {
        this.minConfirmations = minConfirmations;
    }
}
