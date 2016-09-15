package org.vitrivr.cineast.art.modules;

import org.vitrivr.cineast.api.WebUtils;
import org.vitrivr.cineast.art.modules.abstracts.AbstractVisualizationModule;
import org.vitrivr.cineast.art.modules.visualization.VisualizationResult;
import org.vitrivr.cineast.art.modules.visualization.VisualizationType;
import org.vitrivr.cineast.core.data.providers.primitive.PrimitiveTypeProvider;
import org.vitrivr.cineast.core.db.DBSelector;
import org.vitrivr.cineast.core.util.ArtUtil;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sein on 26.08.16.
 */
public class VisualizationAverageColorGrid8Vertical extends AbstractVisualizationModule {
  public VisualizationAverageColorGrid8Vertical() {
    super();
    tableNames.put("AverageColorGrid8", "features_AverageColorGrid8");
  }

  @Override
  public String getDisplayName() {
    return "VisualizationAverageColorGrid8Vertical";
  }

  @Override
  public String visualizeMultimediaobject(String multimediaobjectId) {
    String cacheData = visualizationCache.getFromCache(getDisplayName(), VisualizationType.VISUALIZATION_MULTIMEDIAOBJECT, multimediaobjectId);
    if(cacheData != null){
      return cacheData;
    }
    List<Map<String, PrimitiveTypeProvider>> featureData = ArtUtil.getFeatureData(selectors.get("AverageColorGrid8"), multimediaobjectId);

    int[][][] pixels = new int[8][8][3];
    for (Map<String, PrimitiveTypeProvider> feature : featureData) {
      int[][][] shotPixels = ArtUtil.shotToRGB(feature.get("feature").getFloatArray(), 8, 8);
      for (int x = 0; x < pixels.length; x++) {
        for (int y = 0; y < pixels[0].length; y++) {
          for (int i = 0; i < 3; i++) {
            pixels[x][y][i] += shotPixels[x][y][i];
          }
        }
      }
    }

    for (int x = 0; x < pixels.length; x++) {
      for (int y = 0; y < pixels[0].length; y++) {
        for (int i = 0; i < 3; i++) {
          pixels[x][y][i] = pixels[x][y][i] / featureData.size();
        }
      }
    }

    BufferedImage image = new BufferedImage(8*16, 8*9, BufferedImage.TYPE_INT_RGB);
    Graphics2D graph = image.createGraphics();
    for (int x = 0; x < pixels.length; x++) {
      for (int y = 0; y < pixels[0].length; y++) {
        graph.setColor(new Color(pixels[x][y][0], pixels[x][y][1], pixels[x][y][2]));
        graph.fillRect(x*16, y*9, 16, 9);
      }
    }
    graph.dispose();

    return visualizationCache.cacheResult(getDisplayName(), VisualizationType.VISUALIZATION_MULTIMEDIAOBJECT, multimediaobjectId, WebUtils.BufferedImageToDataURL(image, "png"));
  }

  @Override
  public String visualizeSegment(String segmentId) {
    String cacheData = visualizationCache.getFromCache(getDisplayName(), VisualizationType.VISUALIZATION_SEGMENT, segmentId);
    if(cacheData != null){
      return cacheData;
    }
    DBSelector selector = selectors.get("AverageColorGrid8");
    int[][][] pixels = ArtUtil.shotToRGB(segmentId, selector, 8, 8);

    BufferedImage image = new BufferedImage(8*16, 8*9, BufferedImage.TYPE_INT_RGB);
    Graphics2D graph = image.createGraphics();
    for (int x = 0; x < pixels.length; x++) {
      for (int y = 0; y < pixels[0].length; y++) {
        graph.setColor(new Color(pixels[x][y][0], pixels[x][y][1], pixels[x][y][2]));
        graph.fillRect(x*16, y*9, 16, 9);
      }
    }
    graph.dispose();

    return visualizationCache.cacheResult(getDisplayName(), VisualizationType.VISUALIZATION_SEGMENT, segmentId, WebUtils.BufferedImageToDataURL(image, "png"));
  }

  @Override
  public List<VisualizationType> getVisualizations() {
    List<VisualizationType> types = new ArrayList();
    types.add(VisualizationType.VISUALIZATION_SEGMENT);
    types.add(VisualizationType.VISUALIZATION_MULTIMEDIAOBJECT);
    return types;
  }

  @Override
  public VisualizationResult getResultType() {
    return VisualizationResult.IMAGE;
  }
}
