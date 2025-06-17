package gemfire.operations.functions.domain;

public record EntrySize(Object key, int size) implements Comparable<EntrySize>{
    @Override
    public int compareTo(EntrySize o) {
        if(o == null)
            return 1;
        return Integer.valueOf(size).compareTo(o.size);
    }
}
