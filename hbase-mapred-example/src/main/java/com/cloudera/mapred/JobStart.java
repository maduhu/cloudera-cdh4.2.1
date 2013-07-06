package com.cloudera.mapred;

import java.util.Iterator;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.output.NullOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class JobStart extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        Configuration config = HBaseConfiguration.create();
        Iterator<Map.Entry<String, String>> iterator = config.iterator();
        Map.Entry<String, String> entiry;
        while(iterator.hasNext()) {
            entiry = iterator.next();
            System.out.println(entiry.getKey() + "=>" + entiry.getValue());
        }
        config.set("hbase.zookeeper.quorum", "aladdin.com");
        config.set("hbase.security.authentication", "kerberos");
        config.set("hbase.rpc.engine", "org.apache.hadoop.hbase.ipc.SecureRpcEngine");
        config.set("hbase.regionserver.kerberos.principal", "hbase/aladdin.com@ALADDIN.COM");
//        config.set("hbase.regionserver.keytab.file", "/etc/krb5kdc/kadm5.keytab");
        config.set("hbase.master.kerberos.principal", "hbase/aladdin.com@ALADDIN.COM");
//        config.set("hbase.master.keytab.file", "/etc/krb5kdc/kadm5.keytab");
        int res = ToolRunner.run(config, new JobStart(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        int result;
        Configuration conf = this.getConf();
        Job job = new Job(conf, "ReadExample");
        job.setJarByClass(HbaseMapredReadExample.class);
        Scan scan = new Scan();
        scan.setCaching(500);
        scan.setCacheBlocks(false);
        TableMapReduceUtil.initTableMapperJob(
                "Installed_App_List", // input HBase table name
                scan, // Scan instance to control CF and attribute selection
                HbaseMapredReadExample.MyMapper.class, // mapper
                null, // mapper output key
                null, // mapper output value
                job);
        job.setOutputFormatClass(NullOutputFormat.class);
        result = job.waitForCompletion(true) ? 0 : 1;
        return result;
    }
}
