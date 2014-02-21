package com.zhangyue.monitor.metrics.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zhangyue.monitor.alarm.AlarmMessage;
import com.zhangyue.monitor.exception.ExitCodeException;
import com.zhangyue.monitor.metrics.MetricsContext;
import com.zhangyue.monitor.metrics.MetricsRecord;
import com.zhangyue.monitor.metrics.MetricsRecord.State;
import com.zhangyue.monitor.util.ParamsManager;

/**
 * @Descriptions The class DiskMetrics.java's implementation：TODO described the
 *               implementation of class
 * @author scott 2013-8-19 上午10:15:45
 * @version 1.0
 */
public class DiskMetrics implements MetricsContext {

    private static final String DISK_MONITOR = "disk_monitor";

    private String[] paths = null;
    private final double diskUsedThreshold;
    private volatile AtomicBoolean completed;
    private Process process;
    private String fileSystem;

    private static final Log LOG = LogFactory.getLog(DiskMetrics.class);

    private MetricsRecord metrics;

    public DiskMetrics(String[] paths){
        this.paths = paths;
        completed = new AtomicBoolean();
        diskUsedThreshold =
                ParamsManager.getDouble("disk.used.threshold", 0.80) * 100;
        metrics =
                new MetricsRecord(DISK_MONITOR, ParamsManager.getInt(
                    "warning.count.threshold", 3));
    }

    public void doUpdates() {
        if (paths == null) {
            LOG.error("There is no available disk path!");
            return;
        }
        boolean isError = false;
        for (String path : paths) {
            String[] dfCMD =
                    new String[] { "bash", "-c",
                                  "exec 'df' '-k' '" + path + "' 2>/dev/null" };
            int percentUsed = 0;
            try {
                percentUsed = runCommand(dfCMD);
            } catch (IOException e) {
                LOG.error("Fail to run command:" + dfCMD.toString(), e);
            }
            if (percentUsed > diskUsedThreshold) {
                metrics.addMetric(this.fileSystem, percentUsed + "%");
                if (!isError) {
                    isError = true;
                    metrics.setMetricState(State.ERROR);
                    LOG.warn("Disk warning.diskPercentUsed:" + percentUsed
                             + ",diskUsedThreshold:" + diskUsedThreshold);
                }
            }
        }
        if (!isError) {
            metrics.clearMetrics();
            metrics.setMetricState(State.OK);
        }
    }

    private int runCommand(String[] cmd) throws IOException {
        int percentUsed = 0;
        ProcessBuilder builder = new ProcessBuilder(cmd);

        process = builder.start();
        final BufferedReader errReader =
                new BufferedReader(new InputStreamReader(
                    process.getErrorStream()));
        BufferedReader inReader =
                new BufferedReader(new InputStreamReader(
                    process.getInputStream()));
        final StringBuffer errMsg = new StringBuffer();

        // read error and input streams as this would free up the buffers
        // free the error stream buffer
        Thread errThread = new Thread() {

            @Override
            public void run() {
                try {
                    String line = errReader.readLine();
                    String lineSeparator = System.getProperty("line.separator");
                    while ((line != null) && !isInterrupted()) {
                        errMsg.append(line);
                        errMsg.append(lineSeparator);
                        line = errReader.readLine();
                    }
                } catch (IOException ioe) {
                    LOG.warn("Error reading the error stream", ioe);
                }
            }
        };
        try {
            errThread.start();
        } catch (IllegalStateException ise) {
        }
        try {
            percentUsed = parseExecResult(inReader); // parse the output
            // clear the input stream buffer
            String line = inReader.readLine();
            while (line != null) {
                line = inReader.readLine();
            }
            // wait for the process to finish and check the exit code
            int exitCode = process.waitFor();
            try {
                // make sure that the error thread exits
                errThread.join();
            } catch (InterruptedException ie) {
                LOG.warn("Interrupted while reading the error stream", ie);
            }
            completed.set(true);
            // the timeout thread handling
            // taken care in finally block
            if (exitCode != 0) {
                throw new ExitCodeException(exitCode, errMsg.toString());
            }
        } catch (InterruptedException ie) {
            throw new IOException(ie.toString());
        } finally {
            // close the input stream
            try {
                inReader.close();
            } catch (IOException ioe) {
                LOG.warn("Error while closing the input stream", ioe);
            }
            if (!completed.get()) {
                errThread.interrupt();
            }
            try {
                errReader.close();
            } catch (IOException ioe) {
                LOG.warn("Error while closing the error stream", ioe);
            }
            process.destroy();
        }
        return percentUsed;
    }

    private int parseExecResult(BufferedReader lines) throws IOException {
        lines.readLine(); // skip headings

        String line = lines.readLine();
        if (line == null) {
            throw new IOException("Expecting a line not the end of stream");
        }
        StringTokenizer tokens = new StringTokenizer(line, " \t\n\r\f%");

        fileSystem = tokens.nextToken();// skip filesystem
        if (!tokens.hasMoreTokens()) { // for long filesystem name
            line = lines.readLine();
            if (line == null) {
                throw new IOException("Expecting a line not the end of stream");
            }
            tokens = new StringTokenizer(line, " \t\n\r\f%");
        }
        tokens.nextToken(); // skip capacity
        tokens.nextToken(); // skip used
        tokens.nextToken(); // skip available
        return Integer.parseInt(tokens.nextToken());// skip percentUsed
    }

    public boolean isMetricsValueException() {
        if (!metrics.isMetricNormal()) {
            return true;
        }
        return false;
    }

    public List<AlarmMessage> getAlarmMessage() {
        List<AlarmMessage> messages = new ArrayList<AlarmMessage>();
        if (!metrics.isMetricNormal()) {
            messages.add(new AlarmMessage(metrics.getMetricName(),
                metrics.toString()));
        }
        return messages;
    }
}
