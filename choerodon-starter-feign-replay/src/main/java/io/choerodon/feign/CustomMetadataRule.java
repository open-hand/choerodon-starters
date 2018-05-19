package io.choerodon.feign;

import java.util.*;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;



/**
 * 根据标签和权重选择目标server
 * @author crock
 */
public class CustomMetadataRule extends ZoneAvoidanceRule {
    private static final String META_DATA_KEY_LABEL = "GROUP";
    private static final String META_DATA_KEY_WEIGHT = "WEIGHT";
    private static final String LABEL_SPLIT = ",";

    private Random random = new Random();

    @Override
    public Server choose(Object key) {
        List<String> labels = getSourceLabel();
        ILoadBalancer balancer = getLoadBalancer();
        List<Server> servers = this.getPredicate().getEligibleServers(balancer.getAllServers(), key);
        Map<Server, Integer> maxLabelServers = new HashMap<>();
        int maxLabelNumber = -1;
        int totalWeight = 0;
        TreeSet<String> labelSet = new TreeSet<>();
        for (Server server : servers) {
            int weight = getTargetWeight(extractMetadata(server));
            List<String> targetLabels = getTargetLabel(extractMetadata(server));
            if (labels.isEmpty() && targetLabels.isEmpty()) {
                return server;
            }
            labelSet.addAll(labels);
            labelSet.retainAll(targetLabels);
            int labelNumber = labelSet.size();
            if (labelNumber > maxLabelNumber) {
                maxLabelServers.clear();
                maxLabelServers.put(server, weight);
                maxLabelNumber = labelNumber;
                totalWeight = weight;
            } else if (labelNumber == maxLabelNumber) {
                maxLabelServers.put(server, weight);
                totalWeight += weight;
            }
        }
        if (maxLabelServers.isEmpty()) {
            return null;
        }
        int randomWight = random.nextInt(totalWeight);
        int current = 0;
        for (Map.Entry<Server, Integer> entry : maxLabelServers.entrySet()) {
            current += entry.getValue();
            if (randomWight <= current) {
                return entry.getKey();
            }
        }
        return null;
    }

    private List<String> getSourceLabel() {
        String sourceLabel = null;
        if (HystrixRequestContext.isCurrentThreadInitialized()) {
            sourceLabel = RequestVariableHolder.LABEL.get();
        }
        List<String> labels = Collections.emptyList();
        if (sourceLabel != null) {
            labels = Arrays.asList(sourceLabel.split(LABEL_SPLIT));
        }
        return labels;
    }

    private int getTargetWeight(Map<String,String> metadata) {
        String weightString = metadata.get(META_DATA_KEY_WEIGHT);
        int weight = 100;
        if (weightString != null) {
            weight = Integer.parseInt(weightString);
        }
        return weight;
    }

    private List<String> getTargetLabel(Map<String,String> metadata) {
        String label = metadata.get(META_DATA_KEY_LABEL);
        List<String> targetLabels = Collections.emptyList();
        if (label != null) {
            targetLabels = Arrays.asList(label.split(LABEL_SPLIT));
        }
        return targetLabels;
    }

    private Map<String,String> extractMetadata(Server server) {
        return ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata();
    }

}
