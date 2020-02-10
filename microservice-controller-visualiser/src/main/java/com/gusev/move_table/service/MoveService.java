package com.gusev.move_table.service;

import com.gusev.move_table.entity.Move;

import java.util.List;

public interface MoveService {

    Move save(Move contact);

    List<Move> findAll();

}
