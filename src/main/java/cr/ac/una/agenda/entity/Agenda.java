package cr.ac.una.agenda.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;




@Entity
@Data

public class Agenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
