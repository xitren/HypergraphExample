package com.gusev.move_table.service;

import com.gusev.move_table.entity.Move;
import com.gusev.move_table.repository.MoveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Transactional
public class MoveServiceImpl implements MoveService {

    private final MoveRepository repository;

    @Autowired
    public MoveServiceImpl(MoveRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void generateTestData() {
    }

    @Override
    public Move save(Move score) {
        return repository.save(score);
    }

    @Override
    public List<Move> findAll() {
        return repository.findAll();
    }
}
