package SPD.XMPP.Chat;

import java.util.Objects;
import java.time.LocalDate;

public class Users {
    private String USERNAME;
    private String EMAIL;
    private String PASSWORD;
    private String ROLE;
    private LocalDate ORDER_TIME;

    public LocalDate getORDER_TIME() {
        return ORDER_TIME;
    }

    public void setORDER_TIME(LocalDate ORDER_TIME) {
        this.ORDER_TIME = ORDER_TIME;
    }

    public Users(String USERNAME, String EMAIL, String PASSWORD, String ROLE) {
        this.USERNAME = USERNAME;
        this.EMAIL = EMAIL;
        this.PASSWORD = PASSWORD;
        this.ROLE = ROLE;
    }
    public String getUSERNAME() {
        return USERNAME;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public String getROLE() {
        return ROLE;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public void setROLE(String ROLE) {
        this.ROLE = ROLE;
    }

    @Override
    public String toString() {
        return "Users{" +
                "USERNAME='" + USERNAME + '\'' +
                ", EMAIL='" + EMAIL + '\'' +
                ", PASSWORD='" + PASSWORD + '\'' +
                ", ROLE='" + ROLE + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Users users = (Users) o;
        return EMAIL.equals(users.EMAIL) && PASSWORD.equals(users.PASSWORD);
    }

    @Override
    public int hashCode() {
        return Objects.hash(USERNAME, EMAIL, PASSWORD, ROLE);
    }
}


