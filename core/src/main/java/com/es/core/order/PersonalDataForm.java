package com.es.core.order;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class PersonalDataForm {
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "Too short or too long")
    private String firstname;
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Too short or too long")
    private String lastname;
    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "(\\+?)(\\d(\\s?)){7}(\\d(\\s?))*", message = "Wrong number")
    private String phoneNumber;
    @NotBlank(message = "Address is required")
    @Size(max = 200, message = "Too long")
    private String deliveryAddress;
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
