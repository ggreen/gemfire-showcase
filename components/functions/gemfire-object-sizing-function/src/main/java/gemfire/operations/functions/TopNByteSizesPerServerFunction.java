package gemfire.operations.functions;

import gemfire.operations.functions.domain.EntrySize;
import gemfire.operations.functions.strategy.GetRegion;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.util.ObjectSizer;

import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Supplier;

/**
 * Note this function uses the GemFire ReflectionObjectSizer.
 *  For accurate sizing of every instance use REFLECTION_SIZE instead.
 *   This sizer will add up the sizes of all objects that are reachable from the
 *   keys and values in your region by non-static fields.
 *
 * @author gregory green
 */
public class TopNByteSizesPerServerFunction implements Function<String[]> {
    private final java.util.function.Function<FunctionContext<?>,Region<Object,Object>> regionGetter;
    private final Supplier<ObjectSizer> sizerSupplier;


    public TopNByteSizesPerServerFunction()
    {
        this(new GetRegion(),() -> ObjectSizer.REFLECTION_SIZE);
    }

    public TopNByteSizesPerServerFunction(java.util.function.Function<FunctionContext<?>,Region<Object,Object>> regionGetter, Supplier<ObjectSizer> sizerSupplier) {
        this.regionGetter = regionGetter;
        this.sizerSupplier = sizerSupplier;
    }

    @Override
    public void execute(FunctionContext<String[]> functionContext) {
        Region<Object,Object> region = regionGetter.apply(functionContext);

        var objectSizer = sizerSupplier.get();

        int topN = 3;
        var args = functionContext.getArguments();
        if(args != null && args.length > 0)
            topN = Integer.parseInt(args[0]);

        var minHeap = new PriorityQueue<EntrySize>();

        for (Map.Entry<Object,Object> entry : region.entrySet())
        {
            var size = objectSizer.sizeof(entry.getValue());

            minHeap.offer(new EntrySize(entry.getKey(),size));
            if (minHeap.size() > topN) {
                minHeap.poll(); // Remove the smallest of the top numbers
            }
        }

        var sender = functionContext.getResultSender();

        if(minHeap.isEmpty())
            sender.lastResult(0);
        else {
            Iterator<EntrySize> sizes = minHeap.iterator();
            EntrySize size;
            while (sizes.hasNext()) {
                size = sizes.next();

                if (sizes.hasNext())
                    sender.sendResult(size.toString());
                else
                    sender.lastResult(size.toString());
            }
        }
    }

    @Override
    public String getId() {
        return "TopNByteSizesPerServerFunction";
    }
}
