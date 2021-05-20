package com.es.core.order;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class PersonalDataForm {
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name field must be between 2 and 50 symbols.")
    private String firstname;
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name field must be between 2 and 50 symbols.")
    private String lastname;
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "(\\+?)(\\d(\\s?)){7}(\\d(\\s?))*", message = "Invalid number. Phone size more than 7 digit. First symbol can be plus")
    private String phoneNumber;
    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Address can consist 200 symbols.")
    private String deliveryAddress;
    @Size(max = 2000, message = "Additional information can consist 2000 symbols.")
    private String additionalInformation;


    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }
}
