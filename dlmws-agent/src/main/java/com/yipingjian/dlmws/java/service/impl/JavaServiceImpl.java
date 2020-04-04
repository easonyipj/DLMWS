package com.yipingjian.dlmws.java.service.impl;

import com.sun.tools.attach.VirtualMachine;
import com.yipingjian.dlmws.common.utils.CommonUtils;
import com.yipingjian.dlmws.java.entity.JPS;
import com.yipingjian.dlmws.java.entity.JVMClass;
import com.yipingjian.dlmws.java.entity.JVMMemory;
import com.yipingjian.dlmws.java.entity.JVMThread;
import com.yipingjian.dlmws.java.service.JavaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.jvmstat.monitor.*;
import sun.management.ConnectorAddressLink;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.*;
import java.util.*;

@Slf4j
@Service
public class JavaServiceImpl implements JavaService {

    private static final String JETBRAINS = "jetbrains";
    private static final String LOCAL_CONN_ADDR = "com.sun.management.jmxremote.localConnectorAddress";

    @Override
    public List<JPS> getJPSInfo() throws Exception{
        List<JPS> jpsList = new ArrayList<>();
        MonitoredHost local = MonitoredHost.getMonitoredHost("localhost");
        Set<?> vmList = new HashSet<>(local.activeVms());
        for(Object process : vmList) {
            MonitoredVm vm = local.getMonitoredVm(new VmIdentifier("//" + process));
            String processName = MonitoredVmUtil.mainClass(vm, true);
            if(!StringUtils.isEmpty(processName) && !processName.contains(JETBRAINS)) {
                jpsList.add(new JPS((Integer) process, processName));
            }
        }
        return jpsList;
    }

    @Override
    public JVMClass getJVMClassInfo(Integer pid) throws Exception {
        ClassLoadingMXBean classLoadingMXBean = visitMBean(pid, ClassLoadingMXBean.class);
        assert classLoadingMXBean != null;
        JVMClass jvmClass = new JVMClass();
        jvmClass.setPid(pid);
        jvmClass.setHostIp(CommonUtils.getHostIp());
        jvmClass.setClassCount(classLoadingMXBean.getLoadedClassCount());
        jvmClass.setTime(new Date(System.currentTimeMillis()));
        System.out.println("TotalLoadedClassCount:" + classLoadingMXBean.getTotalLoadedClassCount());
        return jvmClass;
    }

    @Override
    public JVMMemory getJVMMemoryInfo(Integer pid) throws Exception {
        MemoryMXBean memoryMXBean = visitMBean(pid, MemoryMXBean.class);
        assert memoryMXBean != null;
        JVMMemory jvmMemory = new JVMMemory();
        jvmMemory.setHostIp(CommonUtils.getHostIp());
        jvmMemory.setPid(pid);
        jvmMemory.setMemoryUsed(memoryMXBean.getHeapMemoryUsage().getUsed() / (1024L*1024));
        jvmMemory.setTime(new Date(System.currentTimeMillis()));
        return jvmMemory;
    }

    @Override
    public JVMThread getJVMThreadInfo(Integer pid) throws Exception {
        ThreadMXBean threadMXBean = visitMBean(pid,ThreadMXBean.class);
        assert threadMXBean != null;
        JVMThread jvmThread = new JVMThread();
        jvmThread.setPid(pid);
        jvmThread.setDaemonThreadCount(threadMXBean.getDaemonThreadCount());
        jvmThread.setThreadCount(threadMXBean.getThreadCount());
        jvmThread.setHostIp(CommonUtils.getHostIp());
        jvmThread.setTime(new Date(System.currentTimeMillis()));
        return jvmThread;
    }

    private <T extends PlatformManagedObject> T visitMBean(int pid, Class<T> clazz) throws Exception {
        VirtualMachine virtualMachine = VirtualMachine.attach(Integer.toString(pid));
        JMXServiceURL jmxServiceURL = getJMXServiceURL(virtualMachine);
        if (jmxServiceURL == null) {
            return null;
        }
        JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, null);
        MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
        return ManagementFactory.getPlatformMXBean(mBeanServerConnection, clazz);

    }

    private JMXServiceURL getJMXServiceURL(VirtualMachine virtualMachine) throws Exception {
        String address = virtualMachine.getAgentProperties().getProperty(LOCAL_CONN_ADDR);
        if (address != null) {
            address = address.replace("127.0.0.1","localhost");
            return new JMXServiceURL(address);
        }
        int pid = Integer.parseInt(virtualMachine.id());
        address = ConnectorAddressLink.importFrom(pid);
        if (address != null) {
            return new JMXServiceURL(address);
        }
        return null;
    }
}
