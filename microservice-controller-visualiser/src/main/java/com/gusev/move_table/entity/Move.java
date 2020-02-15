package com.gusev.move_table.entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table
public class Move implements Serializable {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private Long time;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer x;

    @Column(nullable = false)
    private Integer y;

    @Column(nullable = false)
    private Action action;

    @Column(nullable = false)
    private Integer upType;

    @Column(nullable = false)
    private Integer downType;

    @Column(nullable = false)
    private Integer leftType;

    @Column(nullable = false)
    private Integer rightType;

    @Column(nullable = false)
    private Long beforeMark;

    @Column(nullable = false)
    private Long afterMark;

    public Move() {
    }

    public Move(String name, Integer x, Integer y, Action action,
                Integer upType, Integer downType, Integer leftType, Integer rightType,
                Long beforeMark, Long afterMark) {
        this.time = System.currentTimeMillis();
        this.name = name;
        this.x = x;
        this.y = y;
        this.action = action;
        this.upType = upType;
        this.downType = downType;
        this.leftType = leftType;
        this.rightType = rightType;
        this.beforeMark = beforeMark;
        this.afterMark = afterMark;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = x;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Integer getUpType() {
        return upType;
    }

    public void setUpType(Integer upType) {
        this.upType = upType;
    }

    public Integer getDownType() {
        return downType;
    }

    public void setDownType(Integer downType) {
        this.downType = downType;
    }

    public Integer getLeftType() {
        return leftType;
    }

    public void setLeftType(Integer leftType) {
        this.leftType = leftType;
    }

    public Integer getRightType() {
        return rightType;
    }

    public void setRightType(Integer rightType) {
        this.rightType = rightType;
    }

    public Long getAfterMark() {
        return afterMark;
    }

    public void setAfterMark(Long afterMark) {
        this.afterMark = afterMark;
    }

    public Long getBeforeMark() {
        return beforeMark;
    }

    public void setBeforeMark(Long beforeMark) {
        this.beforeMark = beforeMark;
    }

    public Long getTime() {
        return time;
    }
}
