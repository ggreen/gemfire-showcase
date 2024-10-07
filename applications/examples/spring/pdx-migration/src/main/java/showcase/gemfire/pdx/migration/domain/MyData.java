package showcase.gemfire.pdx.migration.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MyData {
    private String id;
    private MyEnum myEnum;
}
