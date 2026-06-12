package mainApplication;

public class Login {

    private String firstNameData;
    private String lastNameData;
    private String userNameData;
    private String passwordData;
    private String phoneData;

    public Login(String firstNameData,
                 String lastNameData,
                 String userNameData,
                 String passwordData,
                 String phoneData) {

        this.firstNameData = firstNameData;
        this.lastNameData = lastNameData;
        this.userNameData = userNameData;
        this.passwordData = passwordData;
        this.phoneData = phoneData;
    }

    public boolean checkUserName() {
        return userNameData != null
                && userNameData.contains("_")
                && userNameData.length() <= 5;
    }

    public boolean checkPasswordComplexity() {

        boolean hasUpper = false;
        boolean hasNumber = false;
        boolean hasSpecial = false;

        if (passwordData == null || passwordData.length() < 8) {
            return false;
        }

        for (int index = 0; index < passwordData.length(); index++) {

            char ch = passwordData.charAt(index);

            if (Character.isUpperCase(ch)) {
                hasUpper = true;
            }

            if (Character.isDigit(ch)) {
                hasNumber = true;
            }

            if (!Character.isLetterOrDigit(ch)) {
                hasSpecial = true;
            }
        }

        return hasUpper && hasNumber && hasSpecial;
    }

    public boolean checkCellPhoneNumber() {
        return phoneData != null && phoneData.matches("^\\+27\\d{9}$");
    }

    public String registerUser() {

        if (checkUserName() == false) {
            return "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
        }

        if (checkPasswordComplexity() == false) {
            return "Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }

        if (checkCellPhoneNumber() == false) {
            return "Cell phone number incorrectly formatted or does not contain international code.";
        }

        return "Username successfully captured.\nPassword successfully captured.\nCell phone number successfully added.";
    }

    public boolean loginUser(String enteredUserName,
                             String enteredPassword) {

        return userNameData.equals(enteredUserName)
                && passwordData.equals(enteredPassword);
    }

    public String returnLoginStatus(String enteredUserName,
                                    String enteredPassword) {

        if (loginUser(enteredUserName, enteredPassword)) {
            return "Welcome " + firstNameData + ", " + lastNameData + " it is great to see you again.";
        }

        return "Username or password incorrect, please try again.";
    }
}