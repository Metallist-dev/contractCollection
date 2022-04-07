package de.metallist.backend.utilities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * holds the user data
 *
 * @author Metallist-dev
 * @version 0.1
 */
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Getter @Setter
    private String name;

    @Getter @Setter
    private String password;
}
