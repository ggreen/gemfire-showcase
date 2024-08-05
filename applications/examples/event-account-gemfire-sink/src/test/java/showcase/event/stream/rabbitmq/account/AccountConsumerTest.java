package showcase.event.stream.rabbitmq.account;

import com.vmware.tanzu.data.services.rabbitmq.streaming.account.domain.Account;
import nyla.solutions.core.patterns.creational.generator.JavaBeanGeneratorCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import showcase.event.stream.rabbitmq.account.functions.AccountConsumer;
import showcase.event.stream.rabbitmq.account.repostories.AccountRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountConsumerTest {

    private Account account = JavaBeanGeneratorCreator.of(Account.class).create();
    private AccountConsumer subject;
    @Mock
    private AccountRepository repository;

    @BeforeEach
    void setUp() {
        subject = new AccountConsumer(repository);
    }

    @Test
    void accept() {
        subject.accept(account);

        verify(repository).save(any());
    }
}