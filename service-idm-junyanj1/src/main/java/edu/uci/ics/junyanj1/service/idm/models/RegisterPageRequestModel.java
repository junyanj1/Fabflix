package edu.uci.ics.junyanj1.service.idm.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import edu.uci.ics.junyanj1.service.idm.core.Validate;

public class RegisterPageRequestModel implements Validate {
    private String email;
    private char[] password;
    public String email_regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";

    public RegisterPageRequestModel() {}

    @JsonCreator
    public RegisterPageRequestModel(
            @JsonProperty(value = "email", required = true) String email,
            @JsonProperty(value = "password", required = true) char[] password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public char[] getPassword() {
        return password;
    }

    @Override
    public boolean isValid() {
        return (this.password != null) && (this.password.length>0) &&
                (this.email.matches(email_regex)) &&
                (this.email.length()>0 && this.email.length()<=50);
    }

    @Override
    public String toString() {
        return "Email: " + email + ", Password stored.";
    }

    // Expensive but necessary
    public static boolean passwordCharacterRequirements(char[] password) {
        String special_char = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
        boolean have_upper_case = false;
        boolean have_lower_case = false;
        boolean have_number = false;
        boolean have_spec_char = false;
        for (char c : password) {
            if(!have_upper_case) {
                if (Character.isUpperCase(c))
                    have_upper_case = true;
            }
            if (!have_lower_case) {
                if(Character.isLowerCase(c))
                    have_lower_case = true;
            }
            if (!have_number) {
                if (Character.isDigit(c))
                    have_number = true;
            }
            if (!have_spec_char) {
                if(special_char.indexOf(c) != -1)
                    have_spec_char = true;
            }
        }
        return have_lower_case&&have_number&&have_spec_char&&have_upper_case;
    }

    public void resetPassword() {
        for (char c : this.password) {
            c = '0';
        }
    }
}
