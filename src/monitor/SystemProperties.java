package monitor;

import com.sun.management.OperatingSystemMXBean;
import influx.Connection;
import influx.Point;

import java.io.IOException;
import java.lang.management.ManagementFactory;

public class SystemProperties implements Runnable {
    private static final String MEASUREMENT = "SystemProperties";
    private static final String DEVICE_KEY = "Device";
    private static final String DEVICE_LP = "D_LP";
    private static final String DEVICE_HP = "D_HP";

    private final Connection connection;
    private final OperatingSystemMXBean os = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

    private double cpuLoad;
    private double usedMemory;
    private int burst;

    public SystemProperties(Connection connection) {
        this.connection = connection;
    }

    public void update() {
        cpuLoad = os.getProcessCpuLoad();
        usedMemory = os.getTotalMemorySize() - os.getFreeMemorySize();
    }

    public void push() {
        try {
            new Point(MEASUREMENT)
                    .tag(DEVICE_KEY, DEVICE_LP)
                    .field("MemoryUsageFull", usedMemory / 1000000.0)
                    .field("CpuLoadFull", cpuLoad)
                    .write(connection);

            new Point(MEASUREMENT)
                    .tag(DEVICE_KEY, DEVICE_HP)
                    .field("MemoryUsageFull", usedMemory / 500000.0)
                    .field("CpuLoadFull", Math.min(cpuLoad * 2.0, 1.0))
                    .write(connection);
        } catch (IOException exception) {
            System.err.println("Failed to write to InfluxDB: " + exception.getMessage());
        }
    }

    public void pushRandom(double rate) {
        if (Math.random() < rate) {
            try {
                new Point(MEASUREMENT)
                        .tag(DEVICE_KEY, DEVICE_LP)
                        .field("MemoryUsageRandom", usedMemory / 1000000.0)
                        .field("CpuLoadRandom", cpuLoad)
                        .write(connection);

                new Point(MEASUREMENT)
                        .tag(DEVICE_KEY, DEVICE_HP)
                        .field("MemoryUsageRandom", usedMemory / 500000.0)
                        .field("CpuLoadRandom", Math.min(cpuLoad * 2.0, 1.0))
                        .write(connection);
            } catch (IOException exception) {
                System.err.println("Failed to write to InfluxDB: " + exception.getMessage());
            }
        }
    }

    public void pushBurst(double rate, int size) {
        if (burst > 0) {
            burst--;

            try {
                new Point(MEASUREMENT)
                        .tag(DEVICE_KEY, DEVICE_LP)
                        .field("MemoryUsageBurst", usedMemory / 1000000.0)
                        .field("CpuLoadBurst", cpuLoad)
                        .write(connection);

                new Point(MEASUREMENT)
                        .tag(DEVICE_KEY, DEVICE_HP)
                        .field("MemoryUsageBurst", usedMemory / 500000.0)
                        .field("CpuLoadBurst", Math.min(cpuLoad * 2.0, 1.0))
                        .write(connection);
            } catch (IOException exception) {
                System.err.println("Failed to write to InfluxDB: " + exception.getMessage());
            }
        } else {
            if (Math.random() < rate) {
                burst = (int) (size * Math.random());
            }
        }
    }

    @Override
    public void run() {
        boolean running = true;
        String[] animation = {"*----", "-*---", "--*--", "---*-", "----*", "---*-", "--*--", "-*---"};
        int animationPosition = 0;

        while (running) {
            System.out.print("\r Sending CPU/Memory monitoring data to InfluxDB... [" + animation[animationPosition++ % animation.length] + "] ");
            update();
            push();
            pushRandom(.2);
            pushBurst(0.05, 100);

            try {
                Thread.sleep(2500);
            } catch (InterruptedException ignored) {
                running = false;
            }
        }
    }
}
