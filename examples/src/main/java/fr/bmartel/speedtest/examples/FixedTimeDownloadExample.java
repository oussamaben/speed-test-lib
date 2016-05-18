/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2016 Bertrand Martel
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package fr.bmartel.speedtest.examples;

import fr.bmartel.speedtest.*;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Begin to download a file from server & stop downloading when test duration is elapsed.
 *
 * @author Bertrand Martel
 */
public class FixedTimeDownloadExample {

    /**
     * speed examples server host name.
     */
    private final static String SPEED_TEST_SERVER_HOST = "1.testdebit.info";

    /**
     * spedd examples server uri.
     */
    private final static String SPEED_TEST_SERVER_URI_DL = "/fichiers/10Mo.dat";

    /**
     * speed examples server port.
     */
    private final static int SPEED_TEST_SERVER_PORT = 80;

    /**
     * logger.
     */
    private final static Logger log = LogManager.getLogger(DownloadFileExample.class.getName());

    /**
     * Instanciate Speed Test and start download and upload process with speed
     * examples server of your choice.
     *
     * @param args no args required
     */
    public static void main(final String[] args) {

        final Timer timer = new Timer();

        final SpeedTestSocket speedTestSocket = new SpeedTestSocket();

        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

            @Override
            public void onDownloadPacketsReceived(final long packetSize, final float transferRateBitPerSeconds, final
            float
                    transferRateOctetPerSeconds) {
            }

            @Override
            public void onDownloadError(final SpeedTestError speedTestError, final String errorMessage) {

                if (log.isErrorEnabled()) {
                    log.error(errorMessage);
                }
                if (speedTestError != SpeedTestError.FORCE_CLOSE_SOCKET) {
                    if (timer != null) {
                        timer.purge();
                        timer.cancel();
                    }
                }
            }

            @Override
            public void onUploadPacketsReceived(final long packetSize, final float transferRateBitPerSeconds, final
            float
                    transferRateOctetPerSeconds) {
            }

            @Override
            public void onUploadError(final SpeedTestError speedTestError, final String errorMessage) {

                if (log.isErrorEnabled()) {
                    log.error(errorMessage);
                }

                if (speedTestError != SpeedTestError.FORCE_CLOSE_SOCKET) {
                    if (timer != null) {
                        timer.purge();
                        timer.cancel();
                    }
                }
            }

            @Override
            public void onDownloadProgress(final float percent, final SpeedTestReport downloadReport) {
            }

            @Override
            public void onUploadProgress(final float percent, final SpeedTestReport uploadReport) {
            }
        });

        final TimerTask task = new TimerTask() {

            @Override
            public void run() {

                LogUtils.logReport(speedTestSocket, log);
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);

        final TimerTask stopTask = new TimerTask() {

            @Override
            public void run() {

                LogUtils.logReport(speedTestSocket, log);

                speedTestSocket.forceStopTask();

                if (timer != null) {
                    timer.cancel();
                    timer.purge();
                }
            }
        };

        timer.schedule(stopTask, 15000);
        speedTestSocket.startDownload(SPEED_TEST_SERVER_HOST, SPEED_TEST_SERVER_PORT, SPEED_TEST_SERVER_URI_DL);
    }
}
