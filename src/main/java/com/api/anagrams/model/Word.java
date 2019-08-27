package com.api.anagrams.model;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import javax.persistence.*;
import javax.persistence.Entity;

/**
 * The type Word.
 *
 * @author Nagalakshmi Paramasivam
 */
@Entity
@Table(name = "words")
@EntityListeners(AuditingEntityListener.class)
public class Word {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "value", unique = true, nullable = false)
    private String value;

    /**
     * Gets id.
     *
     * @return the id
     */
    public long getId() {

        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets value.
     *
     * @param Value the value
     */
    public void setValue(String Value) {
        this.value = Value;
    }


    @Override
    public String toString() {
        return "Word{" +
                "id=" + id +
                ", value='" + value + '\'' +
                '}';
    }

}
