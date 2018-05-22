package de.fraunhofer.cortex.recommender.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.commons.io.Charsets;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.similarity.precompute.SimilarItem;
import org.apache.mahout.cf.taste.similarity.precompute.SimilarItems;
import org.apache.mahout.cf.taste.similarity.precompute.SimilarItemsWriter;

import com.google.common.io.Closeables;

/**
 * Persist the precomputed item similarities to a file using the string IDs
 * instead of the numeric IDs
 * @author luigi
 *
 */
public class AtnFileSimilarItemsWriter implements SimilarItemsWriter {

  private final File file;
  private BufferedWriter writer;
  private SignalsDataModel model;

  public AtnFileSimilarItemsWriter(File file, SignalsDataModel model) {
    this.file = file;
    this.model = model;
  }

  @Override
  public void open() throws IOException {
    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8));
  }

  @Override
  public void add(SimilarItems similarItems) throws IOException {
    String itemID = String.valueOf(similarItems.getItemID());
    try {
      String atnItemID = model.getItemIDAsString(Long.parseLong(itemID));
      for (SimilarItem similarItem : similarItems.getSimilarItems()) {
        writer.write(atnItemID);
        writer.write(',');
        writer.write(model.getItemIDAsString(similarItem.getItemID()));
        writer.write(',');
        writer.write(String.valueOf(similarItem.getSimilarity()));
        writer.newLine();
      }
    }
    catch(TasteException te) {
    	te.printStackTrace();
    }
  }

  @Override
  public void close() throws IOException {
    Closeables.close(writer, false);
  }

}
