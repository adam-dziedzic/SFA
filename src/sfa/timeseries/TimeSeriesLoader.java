// Copyright (c) 2016 - Patrick Schäfer (patrick.schaefer@zib.de)
// Distributed under the GLP 3.0 (See accompanying file LICENSE)
package sfa.timeseries;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.carrotsearch.hppc.DoubleArrayList;

public class TimeSeriesLoader {
  
  /**
   * Loads the time series from a csv-file of the UCR time series archive.
   * @param dataset
   * @return
   * @throws IOException
   */
  public static TimeSeries[] loadDatset(File dataset) throws IOException {
    ArrayList<TimeSeries> samples = new ArrayList<TimeSeries>();
   
    try (BufferedReader br = new BufferedReader(new FileReader(dataset))) {      
      String line = null;
      while( (line = br.readLine()) != null) {        
        if (line.startsWith("@")) {
          continue;
        }
        String[] columns = line.split(" ");
        double[] data = new double[columns.length];
        int j = 0;
        String label = null;

        // first is the label
        int i = 0;
        for (; i < columns.length; i++) {
          String column = columns[i].trim();
          if (isNonEmptyColumn(column)) {
            label = column;
            break;
          }
        }

        // next the data
        for (i = i+1; i < columns.length; i++) {
          String column = columns[i].trim();
          try {
            if (isNonEmptyColumn(column)) {
              data[j++] = Double.parseDouble(column);
            }
          } catch (NumberFormatException nfe) {
            nfe.printStackTrace();
          }
        }
        if (j > 0) {
          TimeSeries ts = new TimeSeries(Arrays.copyOfRange(data, 0, j), label);
          ts.norm();          
          samples.add(ts);          
        }
      }

    } catch (IOException e) {
      e.printStackTrace();
    } 

    System.out.println("Done reading from " + dataset + " samples " + samples.size() + " length " + samples.get(0).getLength());
    return samples.toArray(new TimeSeries[] {});
  }
  
  public static TimeSeries readSamplesSubsequences (File dataset, int skipColumns) throws IOException {
    try (BufferedReader br = new BufferedReader(new FileReader(dataset))){      
      DoubleArrayList data = new DoubleArrayList();
      String line = null;
      while( (line = br.readLine()) != null) {        
        line.trim();        
        String[] values = line.split("[ \\t]");
        if (values.length > 0) {
          for (int i = skipColumns; i < values.length; i++) {           
            try {
              values[i].trim();
              if (isNonEmptyColumn(values[i])) {
                data.add(Double.parseDouble(values[i]));
              }
            } catch (NumberFormatException nfe) {
              // Parse-Exception ignorieren
            }
          }
        }
      }
      return new TimeSeries(data.toArray());
    }   
  }

  public static boolean isNonEmptyColumn(String column) {
    return column!=null && !"".equals(column) && !"NaN".equals(column) && !"\t".equals(column);
  }
}
