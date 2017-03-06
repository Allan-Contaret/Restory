package com.example.allancontaret.restory;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by gregoire on 02/03/2017.
 */

public class Restaurant implements Serializable {

    String name;
    String city;
    String address;
    String postal_code;
    String description;
    String image;
    int img;
    Date date;
    int id;

    Restaurant() {

    }
}
