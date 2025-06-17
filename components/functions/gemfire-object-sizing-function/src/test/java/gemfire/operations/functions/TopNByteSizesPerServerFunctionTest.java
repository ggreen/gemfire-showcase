package gemfire.operations.functions;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;
import org.apache.geode.cache.util.ObjectSizer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TopNByteSizesPerServerFunctionTest {

    private TopNByteSizesPerServerFunction subject;
    @Mock
    private FunctionContext<String[]> context;
    @Mock
    private ResultSender<Object> sender;
    @Mock
    private Region<Object,Object> region;

    @Mock
    private ObjectSizer objectSizer;

    private final Function<FunctionContext<?>, Region<Object,Object>> regionGetter = fx -> region;

    private final Supplier<ObjectSizer> sizerSupplier = ()-> objectSizer;

    private HashMap<Object,Object> map = new HashMap<>();
    private int entries;


    @BeforeEach
    void setUp() {
        subject = new TopNByteSizesPerServerFunction(regionGetter,sizerSupplier);
    }

    @Test
    void executeWithNoArgsDoesNotException() {

        when(context.getResultSender()).thenReturn(sender);
        Assertions.assertDoesNotThrow( () -> subject.execute(context));
    }

    @Test
    void execute() {

        for(int i=0;i<20;i++)
        {
            map.put(i,i);
        }

        when(context.getResultSender()).thenReturn(sender);
        when(region.entrySet()).thenReturn(map.entrySet());
        String[] args = {"7"};
        when(context.getArguments()).thenReturn(args);


        subject.execute(context);


        verify(sender,times(6)).sendResult(any());

        verify(sender).lastResult(any());
    }

    @Test
    void id() {
        assertThat(subject.getId()).isEqualTo("TopNByteSizesPerServerFunction");
    }
}