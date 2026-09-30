package com.vitorcsouza.app.task_manager.domain.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Table(name = "tb_categoria")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Categoria extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "categoria")
    private List<Tarefa> tarefaList = new ArrayList<>();
}
