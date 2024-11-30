package io.spring.gemfire.perftest.components.serialization.dataSerialize.domain;

import org.apache.geode.DataSerializable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;


public class ExampleDataSerializable implements DataSerializable {

    private long id;
    private int in1;
    private int in3;
    private int in4;
    private String s1;
    private String s2;
    private String s3;
    private String s4;
    private String s5;
    private double d1;
    private BigDecimal bd1;
    private String f2;
    private String f3;
    private String f4;
    private String f5;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIn1() {
        return in1;
    }

    public void setIn1(int in1) {
        this.in1 = in1;
    }

    public int getIn3() {
        return in3;
    }

    public void setIn3(int in3) {
        this.in3 = in3;
    }

    public int getIn4() {
        return in4;
    }

    public void setIn4(int in4) {
        this.in4 = in4;
    }

    public String getS1() {
        return s1;
    }

    public void setS1(String s1) {
        this.s1 = s1;
    }

    public String getS2() {
        return s2;
    }

    public void setS2(String s2) {
        this.s2 = s2;
    }

    public String getS3() {
        return s3;
    }

    public void setS3(String s3) {
        this.s3 = s3;
    }

    public String getS4() {
        return s4;
    }

    public void setS4(String s4) {
        this.s4 = s4;
    }

    public String getS5() {
        return s5;
    }

    public void setS5(String s5) {
        this.s5 = s5;
    }

    public double getD1() {
        return d1;
    }

    public void setD1(double d1) {
        this.d1 = d1;
    }

    public BigDecimal getBd1() {
        return bd1;
    }

    public void setBd1(BigDecimal bd1) {
        this.bd1 = bd1;
    }

    public String getF2() {
        return f2;
    }

    public void setF2(String f2) {
        this.f2 = f2;
    }

    public String getF3() {
        return f3;
    }

    public void setF3(String f3) {
        this.f3 = f3;
    }

    public String getF4() {
        return f4;
    }

    public void setF4(String f4) {
        this.f4 = f4;
    }

    public String getF5() {
        return f5;
    }

    public void setF5(String f5) {
        this.f5 = f5;
    }

    @Override
    public void toData(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(s1);
        dataOutput.writeUTF(s2);
        dataOutput.writeUTF(s3);
        dataOutput.writeUTF(s4);
        dataOutput.writeUTF(s5);
        dataOutput.writeUTF(f2);
        dataOutput.writeUTF(f3);
        dataOutput.writeUTF(f4);
        dataOutput.writeUTF(f5);
        dataOutput.writeLong(bd1.longValue());
        dataOutput.writeDouble(d1);
        dataOutput.writeInt(in1);
        dataOutput.writeInt(in3);
        dataOutput.writeInt(in4);
    }

    @Override
    public void fromData(DataInput dataInput) throws IOException, ClassNotFoundException {
        s1 = dataInput.readUTF();
        s2 = dataInput.readUTF();
        s3 = dataInput.readUTF();
        s4 = dataInput.readUTF();
        s5 = dataInput.readUTF();

        f2 = dataInput.readUTF();
        f3 = dataInput.readUTF();
        f4 = dataInput.readUTF();
        f5 = dataInput.readUTF();

        bd1 = BigDecimal.valueOf(dataInput.readLong());

        in1 = dataInput.readInt();
        in3 = dataInput.readInt();
        in4 = dataInput.readInt();
    }
}
