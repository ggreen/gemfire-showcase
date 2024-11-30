package io.spring.gemfire.perftest.components.serialization.pdx.serialize;

import io.spring.gemfire.perftest.components.serialization.pdx.domain.PdxData;
import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.PdxWriter;

import java.math.BigDecimal;

public class QaPdSerializer implements PdxSerializer {
    public QaPdSerializer() {
    }

    @Override
    public boolean toData(Object o, PdxWriter pdxWriter) {
        PdxData pdxData = (PdxData)o;
        pdxWriter.writeLong("id",pdxData.getId());
        pdxWriter.markIdentityField("id");
        pdxWriter.writeString("s1",pdxData.getS1());
        pdxWriter.writeString("s2",pdxData.getS2());
        pdxWriter.writeString("s3",pdxData.getS3());
        pdxWriter.writeString("s4",pdxData.getS4());
        pdxWriter.writeString("s5",pdxData.getS5());
        pdxWriter.writeString("f2",pdxData.getF2());
        pdxWriter.writeString("f3",pdxData.getF3());
        pdxWriter.writeString("f4",pdxData.getF4());
        pdxWriter.writeString("f5",pdxData.getF5());
        pdxWriter.writeObject("bd1",pdxData.getBd1());
        pdxWriter.writeDouble("d1",pdxData.getD1());
        pdxWriter.writeInt("in1",pdxData.getIn1());
        pdxWriter.writeInt("in3",pdxData.getIn3());
        pdxWriter.writeInt("in4",pdxData.getIn4());
        return true;
    }

    @Override
    public Object fromData(Class<?> aClass, PdxReader pdxReader) {
        PdxData pdxData = new PdxData();
        pdxData.setId(pdxReader.readLong("id"));
        pdxData.setS1(pdxReader.readString("s1"));
        pdxData.setS2(pdxReader.readString("s2"));
        pdxData.setS3(pdxReader.readString("s3"));
        pdxData.setS4(pdxReader.readString("s4"));
        pdxData.setS5(pdxReader.readString("s5"));
        pdxData.setF2(pdxReader.readString("f2"));
        pdxData.setF3(pdxReader.readString("f3"));
        pdxData.setF4(pdxReader.readString("f4"));
        pdxData.setF5(pdxReader.readString("f5"));
        pdxData.setBd1((BigDecimal) pdxReader.readObject("bd1"));
        pdxData.setD1(pdxReader.readDouble("d1"));
        pdxData.setIn1(pdxReader.readInt("in1"));
        pdxData.setIn3(pdxReader.readInt("in3"));
        pdxData.setIn4(pdxReader.readInt("in4"));
        return pdxData;
    }
}
