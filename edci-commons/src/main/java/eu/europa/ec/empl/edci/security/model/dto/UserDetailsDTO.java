package eu.europa.ec.empl.edci.security.model.dto;

public class UserDetailsDTO {

    private boolean authenticated;
    private String sub;
    private String email;
    private String name;
    private String nickname;

    public UserDetailsDTO() {
    }

    public UserDetailsDTO(boolean isAuthenticated) {
        this.setAuthenticated(isAuthenticated);
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
