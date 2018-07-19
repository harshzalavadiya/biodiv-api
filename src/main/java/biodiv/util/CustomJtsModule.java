package biodiv.util;

import com.bedatadriven.jackson.datatype.jts.parsers.GenericGeometryParser;
import com.bedatadriven.jackson.datatype.jts.parsers.GeometryCollectionParser;
import com.bedatadriven.jackson.datatype.jts.parsers.LineStringParser;
import com.bedatadriven.jackson.datatype.jts.parsers.MultiLineStringParser;
import com.bedatadriven.jackson.datatype.jts.parsers.MultiPointParser;
import com.bedatadriven.jackson.datatype.jts.parsers.MultiPolygonParser;
import com.bedatadriven.jackson.datatype.jts.parsers.PointParser;
import com.bedatadriven.jackson.datatype.jts.parsers.PolygonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

public class CustomJtsModule extends SimpleModule {

    public CustomJtsModule() {
        this(new GeometryFactory());
    }
    
    public CustomJtsModule(GeometryFactory geometryFactory) {
        super("CustomJtsModule", new Version(1, 0, 0, null,"com.bedatadriven","jackson-datatype-jts"));

        addSerializer(Geometry.class, new CustomGeometrySerializer());
        GenericGeometryParser genericGeometryParser = new GenericGeometryParser(geometryFactory);
        addDeserializer(Geometry.class, new CustomGeometryDeserializer<Geometry>(genericGeometryParser));
        addDeserializer(Point.class, new CustomGeometryDeserializer<Point>(new PointParser(geometryFactory)));
        addDeserializer(MultiPoint.class, new CustomGeometryDeserializer<MultiPoint>(new MultiPointParser(geometryFactory)));
        addDeserializer(LineString.class, new CustomGeometryDeserializer<LineString>(new LineStringParser(geometryFactory)));
        addDeserializer(MultiLineString.class, new CustomGeometryDeserializer<MultiLineString>(new MultiLineStringParser(geometryFactory)));
        addDeserializer(Polygon.class, new CustomGeometryDeserializer<Polygon>(new PolygonParser(geometryFactory)));
        addDeserializer(MultiPolygon.class, new CustomGeometryDeserializer<MultiPolygon>(new MultiPolygonParser(geometryFactory)));
        addDeserializer(GeometryCollection.class, new CustomGeometryDeserializer<GeometryCollection>(new GeometryCollectionParser(geometryFactory, genericGeometryParser)));
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
    }
}
