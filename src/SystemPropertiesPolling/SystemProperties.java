package SystemPropertiesPolling;

import Measurement.Point;
import Measurement.Utility;
import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

import static jdk.nashorn.internal.runtime.regexp.joni.Syntax.Java;

/**
 * The class allows reading the RAM usage parameters and pushes them to the DatabaseConnector.
 */

public class SystemProperties {

    public double cpuLoad;
    public double usedMemorySize;
    private int burst;
    private static final String DEVICE_LP = "D_LP";
    private static final String DEVICE_HP = "D_HP";

    private OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    public SystemProperties() {

    }

    /**
     * Read the used RAM size
     */

    public void updateProperties() {
        cpuLoad = osBean.getProcessCpuLoad();
        usedMemorySize = osBean.getTotalPhysicalMemorySize() - osBean.getFreePhysicalMemorySize();
    }

    /**
     *  Push the used RAM size to the Database
     */
    public void pushToDbFull() {
        Point point = new Point("SystemProperties");
        point.addTag("Device", DEVICE_LP);
        point.addFloatField("MemoryUsageFull", (float) (usedMemorySize/1E6));
        point.addFloatField("CpuLoadFull", (float)(cpuLoad));
        point.request();

        point = new Point("SystemProperties");
        point.addTag("Device", DEVICE_HP);
        point.addFloatField("MemoryUsageFull", (float) (usedMemorySize/1E6 * 2));
        point.addFloatField("CpuLoadFull", (float)(Math.max(cpuLoad * 2, 1)));
        point.request();
    }

    /**
     * Push some used RAM size to the database
     * @param rate fraction of values to push to the database
     */
    public void pushToDbRandom(double rate) {
        if (Math.random() < rate) {
            Point point = new Point("MemoryUsage");
            point.addTag("Device", DEVICE_LP);
            point.addFloatField("MemoryUsageRandom", (float) (usedMemorySize/1E6));
            point.addFloatField("CpuLoadRandom", (float)(cpuLoad));
            point.request();

            point = new Point("SystemProperties");
            point.addTag("Device", DEVICE_HP);
            point.addFloatField("MemoryUsageRandom", (float) (usedMemorySize/1E6 * 2));
            point.addFloatField("CpuLoadRandom", (float)(Math.max(cpuLoad * 2, 1)));
            point.request();
        }
    }

    /**
     * Push burst of used RAM siez eto the database
     * @param rate prob. to kick a new burst
     * @param size max. size of burst
     */
    public void pushToDbBurst(double rate, int size) {
        if (burst > 0) {
            burst--;

            Point point = new Point("MemoryUsage");
            point.addTag("Device", DEVICE_LP);
            point.addFloatField("MemoryUsageBurst", (float) (usedMemorySize/1E6));
            point.addFloatField("CpuLoadBurst", (float)(cpuLoad));
            point.request();

            point = new Point("SystemProperties");
            point.addTag("Device", DEVICE_HP);
            point.addFloatField("MemoryUsageBurst", (float) (usedMemorySize/1E6 * 2));
            point.addFloatField("CpuLoadBurst", (float)(Math.max(cpuLoad * 2, 1)));
            point.request();
        }
        else {
            if (Math.random() < rate) {
                burst = (int) (size * Math.random());
            }
        }
    }

    /**
     * Main
     * @param args
     */
    public static void main(String[] args) {
        SystemProperties sp = new SystemProperties();
        while (true) {
            sp.updateProperties();
            sp.pushToDbFull();
            sp.pushToDbRandom(.2);
            sp.pushToDbBurst(0.05, 100);

            Utility.waitSomeTime(2500);
        }
    }
}