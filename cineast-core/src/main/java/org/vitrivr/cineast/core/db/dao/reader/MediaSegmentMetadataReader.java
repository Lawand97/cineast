package org.vitrivr.cineast.core.db.dao.reader;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.vitrivr.cineast.core.data.entities.MediaSegmentMetadataDescriptor;
import org.vitrivr.cineast.core.data.providers.primitive.PrimitiveTypeProvider;
import org.vitrivr.cineast.core.data.providers.primitive.StringTypeProvider;
import org.vitrivr.cineast.core.db.DBSelector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MediaSegmentMetadataReader extends AbstractEntityReader {

    private static final Logger LOGGER = LogManager.getLogger();


    public MediaSegmentMetadataReader(DBSelector selector) {
        super(selector);
        this.selector.open(MediaSegmentMetadataDescriptor.ENTITY);
    }

    public List<MediaSegmentMetadataDescriptor> lookupMultimediaMetadata(String segmentid) {
        final List<Map<String, PrimitiveTypeProvider>> results = this.selector.getRows(MediaSegmentMetadataDescriptor.FIELDNAMES[0], new StringTypeProvider(segmentid));
        if(results.isEmpty()){
            LOGGER.debug("Could not find MediaSegmentMetadataDescriptor with ID {}", segmentid);
            return new ArrayList<>(0);
        }

        final ArrayList<MediaSegmentMetadataDescriptor> list = new ArrayList<>(results.size());
        results.forEach(r -> {
            try {
                list.add(new MediaSegmentMetadataDescriptor(r));
            } catch (DatabaseLookupException exception) {
                LOGGER.fatal("Could not map data returned for row {}. This is a programmer's error!", segmentid);
            }
        });
        return list;
    }

    public List<MediaSegmentMetadataDescriptor> lookupMultimediaMetadata(List<String> segmentIds) {
        StopWatch watch = StopWatch.createStarted();
        final List<Map<String, PrimitiveTypeProvider>> results = this.selector.getRows(MediaSegmentMetadataDescriptor.FIELDNAMES[0], segmentIds.stream().map(StringTypeProvider::new).collect(Collectors.toList()));
        if(results.isEmpty()){
            LOGGER.debug("Could not find any MediaObjectMetadataDescriptor for provided IDs: {}. ID count: {}", String.join(", ", segmentIds ), segmentIds.size());
        }

        final ArrayList<MediaSegmentMetadataDescriptor> list = new ArrayList<>(results.size());
        results.forEach(r -> {
            try {
                list.add(new MediaSegmentMetadataDescriptor(r));
            } catch (DatabaseLookupException exception) {
                LOGGER.fatal("Could not map data. This is a programmer's error!");
            }
        });
        watch.stop();
        LOGGER.debug("Performed segment metadata lookup for {} ids in {} ms. {} results.", segmentIds.size(), watch.getTime(TimeUnit.MILLISECONDS), list.size());
        return list;
    }
}
