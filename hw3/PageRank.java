import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class PageRank {

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, Text>{

    private Text target = new Text();
    private Text targetValue = new Text();

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
        String line = value.toString();
        String[] tokens = line.split("\\s");
        
        StringBuffer sb = new StringBuffer();
        String prValue = tokens[tokens.length - 1]; 
        
        for(int i = 1; i < tokens.length - 1; i++) {
          sb.append(tokens[i] + " ");
          Double newPR = Double.parseDouble(prValue) / (tokens.length - 2);
	  target.set(tokens[i]);
          targetValue.set(tokens[0] + "," + newPR.toString());
          context.write(target, targetValue);
        }
        target.set(tokens[0]);
        targetValue.set(sb.toString());
        context.write(target, targetValue);
    }
  }

  public static class PRSumReducer
       extends Reducer<Text,Text,Text,Text> {
    private Text result = new Text();

    public void reduce(Text key, Iterable<Text> values,
                       Context context
                       ) throws IOException, InterruptedException {
      Double sumPR = 0.0;
      String outlinks = "";
      for (Text value : values) {
        String targetValue = value.toString(); 
        if(targetValue.contains(",")) {
          String[] tokens = targetValue.toString().split(",");
          sumPR += Double.parseDouble(tokens[1]);
        } else {
          outlinks = targetValue;
        }
      }
      result.set(outlinks + sumPR.doubleValue());
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "page rank");    
    job.setJarByClass(PageRank.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(PRSumReducer.class);
    job.setReducerClass(PRSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
