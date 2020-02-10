package com.gusev.move_table.repository;

import com.gusev.move_table.entity.Move;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(propagation = Propagation.MANDATORY)
public interface MoveRepository extends CrudRepository<Move, Long> {
    List<Move> findAll();
}
