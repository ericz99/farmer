package Models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class Config {

    private String chromePath;
    private int breakInterval;
    private boolean closeAfterBreakInterval;
    private int duration;

}
