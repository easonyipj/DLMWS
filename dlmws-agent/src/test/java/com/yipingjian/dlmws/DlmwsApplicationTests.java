package com.yipingjian.dlmws;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.SysInfo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

@SpringBootTest
class DlmwsApplicationTests {

    @Test
    void contextLoads() throws SigarException {
        Sigar sigar = new Sigar();
        Mem mem = sigar.getMem();
        System.out.println(mem.getFreePercent());
    }
    //D:\IDEA\IntelliJ IDEA 2019.3.4\jbr\bin;C:\Windows\Sun\Java\bin;C:\Windows\system32;C:\Windows;C:\Windows\system32;C:\Windows;C:\Windows\System32\Wbem;C:\Windows\System32\WindowsPowerShell\v1.0\;C:\Windows\System32\OpenSSH\;D:\JDK8\jdk1.8.0_144\bin;D:\JDK8\jdk1.8.0_144\jre\bin;D:\Git\Git\cmd;C:\Users\易平建\AppData\Local\Microsoft\WindowsApps;;.

    @Test
    void setProperty() throws SigarException {
        String dir = System.getProperty("user.dir");
        String sigarPath = "D:\\ref\\hyperic-sigar-1.6.4\\hyperic-sigar-1.6.4\\sigar-bin\\lib";
        System.out.println(sigarPath);
        String path = System.getProperty("java.library.path");
        System.out.println(path);
        String newJavaLibraryPath = path + ";" + sigarPath;
        System.setProperty("java.library.path", newJavaLibraryPath);
        contextLoads();
    }

    @Test
    void memrory() {
        SystemInfo systemInfo = new SystemInfo();


    }

}
