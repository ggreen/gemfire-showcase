package showcase.gemfire.health.runner;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import showcase.gemfire.health.check.IsRebalanceRequired;
import showcase.gemfire.health.fix.RebalanceCommand;
import showcase.gemfire.health.service.HealthCheckService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HealthCheckServiceTest {

    private HealthCheckService subject;

    @Mock
    private RebalanceCommand rebalanceCommand;

    @Mock
    private IsRebalanceRequired isRebalanceRequired;


    @BeforeEach
    void setUp() {
        subject = new HealthCheckService(isRebalanceRequired,rebalanceCommand);
    }

    @DisplayName("Given Member over threshold When checkAndRepair Then Rebalance command")
    @Test
    void autoRebalance() throws Exception {

        when(isRebalanceRequired.get()).thenReturn(true);

        subject.checkAndRepair();

        verify(rebalanceCommand).execute();
    }


    @DisplayName("Given high memory and member less than expect When run Then no run rebalance command")
    @Test
    void autoRebalance_lessThanExpectedMembers() throws Exception {

        subject = new HealthCheckService(isRebalanceRequired,rebalanceCommand);

        subject.checkAndRepair();

        verify(rebalanceCommand,never()).execute();
    }

}