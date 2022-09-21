package ru.practicum.shareit.requests;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "requests", schema = "public")
@Getter
@Setter
@ToString
public class Request {
    @Id
    private long id;
    private String description;
    @Column(name = "requestor_id")
    private long requestorId;
}
