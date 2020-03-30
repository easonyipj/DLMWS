package com.yipingjian.dlmws.host.service.impl;

import com.alibaba.fastjson.JSONObject;
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

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Service
public class HostServiceImpl implements HostService {

    private static final SystemInfo SYSTEM_INFO = new SystemInfo();
    private static final HardwareAbstractionLayer HARDWARE_ABSTRACTION_LAYER = SYSTEM_INFO.getHardware();
    private static final ComputerSystem COMPUTER_SYSTEM = HARDWARE_ABSTRACTION_LAYER.getComputerSystem();
    private static final OperatingSystem OPERATING_SYSTEM = SYSTEM_INFO.getOperatingSystem();


    @Override
    public String getHostBasicInfo() {
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
        return JSONObject.toJSONString(hostBasicInfo);
    }

    @Override
    public Memory getMemoryLoad() {
        Memory memory = new Memory();
        memory.setHostIp(getHostIp());
        return memory;
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

    private String getHostIp() {
        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("GET HOST IP ERROR", e);
            // TODO 使用配置文件中的agentId
            return "";
        }
        return ip;
    }
}
