package com.battcn.samples.entity;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Levin
 */
@Data
public class Book implements java.io.Serializable {

    private Long id;
    private String title;
    private BigDecimal price;


}
