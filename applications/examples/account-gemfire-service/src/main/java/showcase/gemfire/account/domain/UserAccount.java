package showcase.gemfire.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAccount {
    private Long id;
    private String name;
    private Long currentTimestamp;
    private Contact contact;
    private Address workAddress;
    private Address homeAddress;

}
