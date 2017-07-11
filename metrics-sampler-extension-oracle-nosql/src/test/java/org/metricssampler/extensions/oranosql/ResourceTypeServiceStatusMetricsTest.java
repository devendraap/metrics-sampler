package org.metricssampler.extensions.oranosql;

import oracle.kv.impl.topo.ResourceId.ResourceType;
import oracle.kv.impl.util.ConfigurableService.ServiceStatus;
import org.junit.Test;
import org.metricssampler.reader.MetricName;
import org.metricssampler.reader.MetricValue;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResourceTypeServiceStatusMetricsTest {

    @Test
    public void addMetricsStandard() throws Exception {
        ResourceTypeServiceStatusMetrics testee = new ResourceTypeServiceStatusMetrics();
        testee.add(ResourceType.ADMIN, ServiceStatus.RUNNING);
        testee.add(ResourceType.ADMIN, ServiceStatus.ERROR_NO_RESTART);
        testee.add(ResourceType.REP_NODE, ServiceStatus.RUNNING);
        testee.add(ResourceType.REP_NODE, ServiceStatus.STOPPED);
        testee.add(ResourceType.REP_NODE, ServiceStatus.RUNNING);
        testee.add(ResourceType.STORAGE_NODE, ServiceStatus.RUNNING);
        testee.add(ResourceType.STORAGE_NODE, ServiceStatus.UNREACHABLE);

        final HashMap<MetricName, MetricValue> result = new HashMap<>();

        testee.addMetrics(result);

        verifyMetric(result, "admin.count", 2);
        verifyMetric(result, "rep_node.count", 3);
        verifyMetric(result, "storage_node.count", 2);

        verifyStatusMetrics(result, "rep_node", 0, 0, 2, 0, 1, 0, 0, 0, 0);
        verifyStatusMetrics(result, "storage_node", 0, 0, 1, 0, 0, 0, 0, 1, 0);
        verifyStatusMetrics(result, "admin", 0, 0, 1, 0, 0, 0, 1, 0, 0);
    }

    @Test
    public void addMetricsNone() throws Exception {
        ResourceTypeServiceStatusMetrics testee = new ResourceTypeServiceStatusMetrics();

        final HashMap<MetricName, MetricValue> result = new HashMap<>();

        testee.addMetrics(result);

        verifyMetric(result, "admin.count", 0);
        verifyMetric(result, "rep_node.count", 0);
        verifyMetric(result, "storage_node.count", 0);

        verifyStatusMetrics(result, "rep_node", 0, 0, 0, 0, 0, 0, 0, 0, 0);
        verifyStatusMetrics(result, "storage_node", 0, 0, 0, 0, 0, 0, 0, 0, 0);
        verifyStatusMetrics(result, "admin", 0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    protected void verifyMetric(HashMap<MetricName, MetricValue> result, String name, Integer expectedCount) {
        final Optional<MetricName> metricName = result.keySet().stream().filter(n -> n.getName().equals(name)).findFirst();
        assertTrue("Could not find metric " + name + " in " + result.keySet().stream().map(MetricName::getName).collect(Collectors.joining(", ")), metricName.isPresent());

        final MetricValue value = result.get(metricName.get());
        assertEquals("Value for " + name + " wrong", expectedCount, value.getValue());
    }

    protected void verifyStatusMetrics(HashMap<MetricName, MetricValue> result, String type, Integer starting, Integer waitingForDeploy, Integer running, Integer stopping, Integer stopped, Integer errorRestarting, Integer errorNoRestart, Integer unreachable, Integer expectedRestarting) {
        verifyMetric(result, type + ".status.starting.count", starting);
        verifyMetric(result, type + ".status.waiting_for_deploy.count", waitingForDeploy);
        verifyMetric(result, type + ".status.running.count", running);
        verifyMetric(result, type + ".status.stopping.count", stopping);
        verifyMetric(result, type + ".status.stopped.count", stopped);
        verifyMetric(result, type + ".status.error_restarting.count", errorRestarting);
        verifyMetric(result, type + ".status.error_no_restart.count", errorNoRestart);
        verifyMetric(result, type + ".status.unreachable.count", unreachable);
        verifyMetric(result, type + ".status.expected_restarting.count", expectedRestarting);
    }
}