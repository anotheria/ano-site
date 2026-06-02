package net.anotheria.anosite.cms.user;

import net.anotheria.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author vbezuhyi
 * @see CMSUserManager
 **/
public class CMSUser {
    private String username;
    private String password;
    private List<String> roles;

    public CMSUser() {
        roles = new ArrayList<String>();
    }

    public CMSUser(String aUserName, String aPassword) {
        this();
        username = aUserName;
        password = aPassword;
    }

    public CMSUser(String aUserName, String aPassword, List<String> someRoles) {
        this(aUserName, aPassword);
        if (!(someRoles == null)) {
            roles.addAll(someRoles);
        }
    }

    public CMSUser(String aUserName, String aPassword, String commaSeparatedListWithRoles) {
        this(aUserName, aPassword, parseRoles(commaSeparatedListWithRoles));
    }

    public boolean isUserInRole(String role) {
        return roles.indexOf(role) != -1 || roles.indexOf("*") != -1;
    }

    @Override
    public String toString() {
        return "Username: " + getUsername() + ", Password: " + getPassword() + ", Roles: " + getRoles();
    }

    private static List<String> parseRoles(String rolesString) {
        return rolesString == null ?
                new ArrayList<String>() :
                Arrays.asList(StringUtils.tokenize(rolesString.trim(), ','));
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void addRole(String aRole) {
        roles.add(aRole);
    }
}
