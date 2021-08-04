package com.blockchain.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String hash;
    private String previousHash;
    private String data;
    private Timestamp timestamp;
    @Lob
    private byte[] file;
    private String dataName;
}
