package com.wolf.mapred;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.HFileOutputFormat;
import org.apache.hadoop.hbase.mapreduce.KeyValueSortReducer;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * hadoop jar /home/work/hbase-client/hbase-security.jar completebulkload /user/rd/liujianying/storefile-2 ljy_InstalledAppList
 * @author aladdin
 */
public class JobStart extends Configured implements Tool {

    /**
     *
     * @param args 1:input path; 2:output path; 3:hbase table name
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Configuration config = HBaseConfiguration.create();
        int res = ToolRunner.run(config, new JobStart(), args);
        System.exit(res);
    }

    @Override
    public int run(String[] args) throws Exception {
        Configuration conf = this.getConf();
        int result;
        Job job = new Job(conf, "91-user-soft-storefile");
        job.setJarByClass(UserSoftStoreFileMapred.class);
        job.setMapperClass(UserSoftStoreFileMapred.MyMapper.class);
        job.setReducerClass(KeyValueSortReducer.class);
        job.setMapOutputKeyClass(ImmutableBytesWritable.class);
        job.setMapOutputValueClass(KeyValue.class);
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        TableMapReduceUtil.initCredentials(job);
        HTable hTable = new HTable(conf, args[2]);
        HFileOutputFormat.configureIncrementalLoad(job, hTable);
        result = job.waitForCompletion(true) ? 0 : 1;
        return result;
    }
}
