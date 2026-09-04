package io.spring.gemfire.rest.app.delete;

import org.apache.geode.DataSerializable;
import org.apache.geode.DataSerializer;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Customer implements DataSerializable {
    private int id;
    private String name;
    private double balance;

    // Zero-arg constructor is required for deserialization
    public Customer() {}

    @Override
    public void toData(DataOutput out) throws IOException {
        out.writeInt(id);
        DataSerializer.writeString(name, out);
        out.writeDouble(balance);
    }

    @Override
    public void fromData(DataInput in) throws IOException {
        this.id = in.readInt();
        this.name = DataSerializer.readString(in);
        this.balance = in.readDouble();
    }
}