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

public class WordCount {

  public static class TokenizerMapper
       extends Mapper<Object, Text, Text, IntWritable>{

    private final static IntWritable zero = new IntWritable(0);
    private final static IntWritable one = new IntWritable(1);
    private Text word = new Text();

    public void map(Object key, Text value, Context context
                    ) throws IOException, InterruptedException {
        Configuration conf = context.getConfiguration();
        String tokens[] = conf.getStrings("wordsToCheck");
	String line = value.toString().toLowerCase();
	for(String token : tokens) {
	    word.set(token);
	    if(line.contains(token.toLowerCase())) {
                context.write(word, one);
            } else {
		context.write(word, zero);
	    }
       }
    }
  }

  public static class IntSumReducer
       extends Reducer<Text,IntWritable,Text,IntWritable> {
    private IntWritable result = new IntWritable();

    public void reduce(Text key, Iterable<IntWritable> values,
                       Context context
                       ) throws IOException, InterruptedException {
      int sum = 0;
      for (IntWritable val : values) {
        sum += val.get();
      }
      result.set(sum);
      context.write(key, result);
    }
  }

  public static void main(String[] args) throws Exception {
    Configuration conf = new Configuration();
    ArrayList<String> list = new ArrayList<String>();
    String[] words = {"hackathon", "Dec", "Chicago", "Java"};
    for(int i = 2; i < args.length; i++) {
        list.add(args[i]);
    }
    if(list.size() > 0) {
        String[] wordlist = list.toArray(new String[list.size()]);
        conf.setStrings("wordsToCheck", wordlist);
    } else {
        conf.setStrings("wordsToCheck", words);
    }

    Job job = Job.getInstance(conf, "word count");    
    job.setJarByClass(WordCount.class);
    job.setMapperClass(TokenizerMapper.class);
    job.setCombinerClass(IntSumReducer.class);
    job.setReducerClass(IntSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(IntWritable.class);
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
