package Models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Task {

    private Integer id;
    private Email email;
    private ModuleType type;

}
