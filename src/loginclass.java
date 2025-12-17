public class loginclass {

    private String username;
    private String password;
    private person person;

    public loginclass(person person) {
        this.person = person;
        this.username = person.getUsername();
        this.password = person.getPassword();
    }

    public person getPerson() {
        return person;
    }

    public void setPerson(person person) {
        this.person = person;
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

    public boolean validateLogin(String inputUser, String inputPass) {

        if (!inputUser.equals(this.username)) {
            System.out.println("Login Failed: Incorrect username!");
            return false;
        }

        if (!inputPass.equals(this.password)) {
            System.out.println("Login Failed: Incorrect password!");
            return false;
        }

        System.out.println("Login successful! Welcome, " + inputUser + ".");

        return true;

    }

    @Override
    public String toString() {
        return "loginclass [username=" + username + "]";
    }
}
