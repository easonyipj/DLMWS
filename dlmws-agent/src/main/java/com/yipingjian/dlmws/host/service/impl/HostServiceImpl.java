package com.yipingjian.dlmws.host.service.impl;


import com.yipingjian.dlmws.common.utils.CommonUtils;
import com.yipingjian.dlmws.host.entity.CPU;
import com.yipingjian.dlmws.host.entity.HostBasicInfo;
import com.yipingjian.dlmws.host.entity.Memory;
import com.yipingjian.dlmws.host.service.HostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.util.Date;

@Slf4j
@Service
public class HostServiceImpl implements HostService {


    private static final SystemInfo SYSTEM_INFO = new SystemInfo();
    private static final HardwareAbstractionLayer HARDWARE_ABSTRACTION_LAYER = SYSTEM_INFO.getHardware();
    private static final ComputerSystem COMPUTER_SYSTEM = HARDWARE_ABSTRACTION_LAYER.getComputerSystem();
    private static final OperatingSystem OPERATING_SYSTEM = SYSTEM_INFO.getOperatingSystem();


    @Override
    public HostBasicInfo getHostBasicInfo() {
        HostBasicInfo hostBasicInfo = new HostBasicInfo();
        hostBasicInfo.setOSType(SYSTEM_INFO.toString());
        hostBasicInfo.setSystem(COMPUTER_SYSTEM.toString());
        hostBasicInfo.setBaseboard(COMPUTER_SYSTEM.getBaseboard().toString());
        hostBasicInfo.setFirmware(COMPUTER_SYSTEM.getFirmware().toString());
        hostBasicInfo.setProcessors(HARDWARE_ABSTRACTION_LAYER.getProcessor().toString());
        hostBasicInfo.setMemory(getPhysicsMemory(HARDWARE_ABSTRACTION_LAYER.getMemory()));
        hostBasicInfo.setNetwork(getNetworkInterfaces(HARDWARE_ABSTRACTION_LAYER.getNetworkIFs()));
        hostBasicInfo.setFilesystem(getFileSystem(OPERATING_SYSTEM.getFileSystem()));
        hostBasicInfo.setPowerSources(getPowerSources(HARDWARE_ABSTRACTION_LAYER.getPowerSources()));
        return hostBasicInfo;
    }

    @Override
    public Memory getMemoryLoad() {
        Memory memory = new Memory();
        GlobalMemory globalMemory = HARDWARE_ABSTRACTION_LAYER.getMemory();
        Float[] memories = generateMemoryUsed(globalMemory.getTotal(), globalMemory.getAvailable());
        Float[] swaps = generateSwapUsed(globalMemory.getVirtualMemory().getSwapTotal(), globalMemory.getVirtualMemory().getSwapUsed());
        memory.setHostIp(CommonUtils.getHostIp());
        memory.setMemoryUsed(memories[0]);
        memory.setMemoryUsedRate(memories[1]);
        memory.setSwapUsed(swaps[0]);
        memory.setSwapUsedRate(swaps[1]);
        memory.setTime(new Date(System.currentTimeMillis()));
        return memory;
    }

    @Override
    public CPU getCPULoad() {
        CentralProcessor processor = HARDWARE_ABSTRACTION_LAYER.getProcessor();
        CPU cpu = new CPU();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // Wait a second...
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long sys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long totalCpu = user + nice + sys + idle + iowait + irq + softirq + steal;
        cpu.setHostIp(CommonUtils.getHostIp());
        cpu.setUserCpu((float) user / totalCpu);
        cpu.setSystemCpu((float) sys / totalCpu);
        cpu.setTime(new Date(System.currentTimeMillis()));
        return cpu;
    }

    private Float[] generateMemoryUsed(long total, long available) {
        Float[] memories = new Float[2];
        float totalGib = (float) total / (1024 * 1024 * 1024);
        float availableGib = (float) available / (1024 * 1024 * 1024);
        float usedGib = totalGib - availableGib;
        float usedRate = usedGib / totalGib;
        memories[0] = usedGib;
        memories[1] = usedRate;
        return memories;
    }

    private Float[] generateSwapUsed(long total, long used) {
        Float[] memories = new Float[2];
        float totalGib = (float) total / (1024 * 1024 * 1024);
        float usedGib = (float) used / (1024 * 1024 * 1024);
        float usedRate = usedGib / totalGib;
        memories[0] = usedGib;
        memories[1] = usedRate;
        return memories;
    }

    private String getPhysicsMemory(GlobalMemory memory) {
        PhysicalMemory[] pmArray = memory.getPhysicalMemory();
        StringBuilder sb = new StringBuilder();
        if (pmArray.length > 0) {
            for (PhysicalMemory pm : pmArray) {
                sb.append(pm.toString()).append("\n");
            }
        }
        return sb.toString();
    }

    private String getPowerSources(PowerSource[] powerSources) {
        StringBuilder sb = new StringBuilder();
        if (powerSources.length == 0) {
            sb.append("Unknown");
        }
        for (PowerSource powerSource : powerSources) {
            sb.append("\n ").append(powerSource.toString());
        }
        return sb.toString();
    }

    private String getNetworkInterfaces(NetworkIF[] networkIFs) {
        StringBuilder sb = new StringBuilder();
        if (networkIFs.length == 0) {
            sb.append(" Unknown");
        }
        for (NetworkIF net : networkIFs) {
            sb.append("\n ").append(net.toString());
        }
        return sb.toString();
    }

    private String getFileSystem(FileSystem fileSystem) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format(" File Descriptors: %d/%d", fileSystem.getOpenFileDescriptors(),
                fileSystem.getMaxFileDescriptors()));

        OSFileStore[] fsArray = fileSystem.getFileStores();
        for (OSFileStore fs : fsArray) {
            long usable = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            sb.append(String.format(
                    " %s (%s) [%s] %s of %s free (%.1f%%), %s of %s files free (%.1f%%) is %s "
                            + (fs.getLogicalVolume() != null && fs.getLogicalVolume().length() > 0 ? "[%s]" : "%s")
                            + " and is mounted at %s",
                    fs.getName(), fs.getDescription().isEmpty() ? "file system" : fs.getDescription(), fs.getType(),
                    FormatUtil.formatBytes(usable), FormatUtil.formatBytes(fs.getTotalSpace()), 100d * usable / total,
                    FormatUtil.formatValue(fs.getFreeInodes(), ""), FormatUtil.formatValue(fs.getTotalInodes(), ""),
                    100d * fs.getFreeInodes() / fs.getTotalInodes(), fs.getVolume(), fs.getLogicalVolume(),
                    fs.getMount()));
        }
        return sb.toString();
    }

}
