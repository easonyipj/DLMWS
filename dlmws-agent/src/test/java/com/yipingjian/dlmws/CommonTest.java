package com.yipingjian.dlmws;

import com.sun.tools.attach.VirtualMachine;
import sun.jvmstat.monitor.MonitoredHost;
import sun.jvmstat.monitor.MonitoredVm;
import sun.jvmstat.monitor.MonitoredVmUtil;
import sun.jvmstat.monitor.VmIdentifier;
import sun.management.ConnectorAddressLink;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.lang.management.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class CommonTest {
    public static void main(String[] args) throws Exception{
        visitRemoteMXBean(27121);
        // getJavaProcess();
    }

    public static void getJavaProcess() throws Exception{
        // 获取监控主机
        MonitoredHost local = MonitoredHost.getMonitoredHost("localhost");
        // 取得所有在活动的虚拟机集合
        Set<?> vmlist = new HashSet<>(local.activeVms());
        // 遍历集合，输出PID和进程名
        for(Object process : vmlist) {
            MonitoredVm vm = local.getMonitoredVm(new VmIdentifier("//" + process));
            // 获取类名
            String processname = MonitoredVmUtil.mainClass(vm, true);
            System.out.println(process + " ------> " + processname);
        }
    }

    public static void visitRemoteMXBean(int pid) throws Exception{
        ThreadMXBean threadMXBean = visitMBean(pid,ThreadMXBean.class);
        assert threadMXBean != null;
        System.out.println("ThreadCount: " + threadMXBean.getThreadCount());
        System.out.println("DaemonThreadCount: " + threadMXBean.getDaemonThreadCount());
        System.out.println("PeakThreadCount: " + threadMXBean.getPeakThreadCount());


        List<GarbageCollectorMXBean> collectorMXBeanList2 = visitMBeans (pid, GarbageCollectorMXBean.class);
        assert collectorMXBeanList2 != null;
        for(GarbageCollectorMXBean GarbageCollectorMXBean : collectorMXBeanList2){
            System.out.println("gc name:" + GarbageCollectorMXBean.getName());
            System.out.println("CollectionCount:" + GarbageCollectorMXBean.getCollectionCount());
            System.out.println("CollectionTime" + GarbageCollectorMXBean.getCollectionTime());
        }

        MemoryMXBean memoryMXBean = visitMBean(pid, MemoryMXBean.class);
        assert memoryMXBean != null;
        System.out.println("HeapMemoryMax:" + memoryMXBean.getHeapMemoryUsage().getMax() / (1024L*1024));
        System.out.println("HeapMemoryUsed:" + memoryMXBean.getHeapMemoryUsage().getUsed() / (1024L*1024));
        System.out.println("NonHeapMemoryMax:" + memoryMXBean.getNonHeapMemoryUsage().getMax());
        System.out.println("NonHeapMemoryUsed:" + memoryMXBean.getNonHeapMemoryUsage().getUsed());

        ClassLoadingMXBean classLoadingMXBean = visitMBean(pid, ClassLoadingMXBean.class);
        assert classLoadingMXBean != null;
        System.out.println("TotalLoadedClassCount:" + classLoadingMXBean.getTotalLoadedClassCount());
        System.out.println("LoadedClassCount:" + classLoadingMXBean.getLoadedClassCount());
        System.out.println("UnloadedClassCount:" + classLoadingMXBean.getUnloadedClassCount());


    }

    public static JMXServiceURL getJMXServiceURL(VirtualMachine virtualMachine) throws Exception {
        String address = virtualMachine.getAgentProperties().getProperty("com.sun.management.jmxremote.localConnectorAddress");
        if (address != null) {
            return new JMXServiceURL(address);
        }
        int pid = Integer.parseInt(virtualMachine.id());
        address = ConnectorAddressLink.importFrom(pid);
        if (address != null) {
            return new JMXServiceURL(address);
        }
        return null;
    }

    public static <T extends PlatformManagedObject> T visitMBean(int pid, Class<T> clazz) throws Exception {
        //第一种直接调用同一 Java 虚拟机内的 MXBean 中的方法。
//        RuntimeMXBean mxbean = ManagementFactory.getRuntimeMXBean();
//        String vendor1 = mxbean.getVmVendor();
//        System.out.println("vendor1:" + vendor1);

        //第二种使用 MXBean 代理
        VirtualMachine virtualMachine = VirtualMachine.attach(Integer.toString(pid));
        JMXServiceURL jmxServiceURL = getJMXServiceURL(virtualMachine);
        if (jmxServiceURL == null) {
            return null;
        }
        JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, null);
        MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
//        return ManagementFactory.newPlatformMXBeanProxy(mBeanServerConnection, ManagementFactory.THREAD_MXBEAN_NAME, ThreadMXBean.class);
//        ManagementFactory.newPlatformMXBeanProxy(mBeanServerConnection, mxBeanName, clazz);
        return ManagementFactory.getPlatformMXBean(mBeanServerConnection, clazz);

    }

    public static <T extends PlatformManagedObject> List<T> visitMBeans(int pid, Class<T> clazz) throws Exception {
        //第一种直接调用同一 Java 虚拟机内的 MXBean 中的方法。
//        RuntimeMXBean mxbean = ManagementFactory.getRuntimeMXBean();
//        String vendor1 = mxbean.getVmVendor();
//        System.out.println("vendor1:" + vendor1);

        //第二种使用 MXBean 代理
        VirtualMachine virtualMachine = VirtualMachine.attach(Integer.toString(pid));
        JMXServiceURL jmxServiceURL = getJMXServiceURL(virtualMachine);
        if (jmxServiceURL == null) {
            return null;
        }
        JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, null);
        MBeanServerConnection mBeanServerConnection = jmxConnector.getMBeanServerConnection();
        return ManagementFactory.getPlatformMXBeans(mBeanServerConnection, clazz);

    }

}
