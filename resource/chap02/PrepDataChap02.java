import java.nio.*; 
// This is a community contributed helper program to set up data for Mahmoud Parsian's chapter 02 examples. 
import java.nio.file.*; 
import java.io.*; 
import java.util.*;
import java.util.stream.*;

public class PrepDataChap02 {
    public static void main(String[] args) throws IOException {
        // While a small sample of data is provided on the page in the book, one may want to run this "Big Data" example on a larger dataset. 
        // One can obtain more data from many places, but here is one example.
        // We can download Apache 2 licensed data from https://www.kaggle.com/datasets/anitarostami/historical-stock-price-dataset?resource=download ,
        // and then run this program to convert it to the expected format, e.g. `java PrepDataChap02 <path to StockPriceDataset.csv> <output path for chap02 input data.csv>`.
        // This will set up the expected columns and also scramble the sort order so that the Hadoop jobs secondary sorting of the data can be demonstrated.
        final String EXPECTED_HEADER_LINE = "Date,Open,High,Low,Close,Adj Close,Volume,Ticker";
        if (args.length < 2) {
            System.err.println("Please provide the path to the historical stock price data from Kaggle* for transformation AND the output path to write the transformed data to, e.g.\nExpected usage:\n`java PrepDataChap02 <path to stock price data csv> <path to write the ready to use for chap02 example data to>`\ne.g. `java PrepDataChap02 /home/user/Downloads/StockPriceDataset.csv /tmp/StockPriceDataset_randomorder_mparsian_chap02_format.csv`\n\nData for transformation into the expected format can be found at https://www.kaggle.com/datasets/anitarostami/historical-stock-price-dataset?resource=download .");
            return;
        }

        Path inputData = Paths.get(args[0]);
        Path outputData = Paths.get(args[1]);
        if (!Files.exists(inputData)) {
            System.err.println("Could not find historical stock data at " + args[0]);
            return;
        }
        if (!Files.lines(inputData).limit(1).collect(Collectors.joining("\n")).equals(EXPECTED_HEADER_LINE)) {
            System.err.println("Unexpected input file found, expecting a header line of \"" + EXPECTED_HEADER_LINE + "\", please try downloading from https://www.kaggle.com/datasets/anitarostami/historical-stock-price-dataset?resource=download ");
            return;
        }
        Comparator<String> shuffleComparator = new Comparator<String>() {
                public int compare(String a, String b) {
                    return Math.random() >= 0.5 ? 1 : -1; // randomized ordering
                }
        };
        Files.write(
            outputData,
            Files.lines(inputData).filter(it -> it.indexOf("Date") == -1).sorted(shuffleComparator).map(
                it -> {
                    String[] parts = it.split(",");
                    return parts[7] + "," + parts[0] + "," + parts[4];
                }
                                                                                            ).collect(Collectors.toList())
        );
        System.out.println("Wrote output data to " + args[1]);
    }
}
