package showcase.gemfire.account.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {
    private String email;
    private String mobilePhone;
    private String workPhone;
    private String homePhone;
}
