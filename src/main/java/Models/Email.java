package Models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class Email {
    @Getter
    @Setter
    private String email;

    @Getter
    @Setter
    private String password;

    public Email(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
