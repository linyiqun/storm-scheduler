/**
 * 主类
 * Created by zhexuan on 15/5/22.
 */
public class Test {
    public static void main(String[] args) throws Exception{
        int numOfParallel;
        TopologyBuilder builder;
        StormTopology stormTopology;
        Config config;
        //待分配的组件名称与节点名称的映射关系
        HashMap<String, String> component2Node;

        //任务并行化数设为10个
        numOfParallel = 2;

        builder = new TopologyBuilder();

        String desSpout = "my_spout";
        String desBolt = "my_bolt";

        //设置spout数据源
        builder.setSpout(desSpout, new TestSpout(), numOfParallel);

        builder.setBolt(desBolt, new TestBolt(), numOfParallel)
                .shuffleGrouping(desSpout);

        config = new Config();
        config.setNumWorkers(numOfParallel);
        config.setMaxSpoutPending(65536);
        config.put(Config.STORM_ZOOKEEPER_CONNECTION_TIMEOUT, 40000);
        config.put(Config.STORM_ZOOKEEPER_SESSION_TIMEOUT, 40000);

        //LocalCluster cluster = new LocalCluster();
        //config.setDebug(true);
        //cluster.submitTopology("test-monitor", config, builder.createTopology());

        component2Node = new HashMap<>();

        component2Node.put(desSpout, "special-supervisor1");
        component2Node.put(desBolt, "special-supervisor2");

        //此标识代表topology需要被调度
        config.put("assigned_flag", "1");
        //具体的组件节点对信息
        config.put("design_map", component2Node);

        StormSubmitter.submitTopology("test", config, builder.createTopology());
    }
}
