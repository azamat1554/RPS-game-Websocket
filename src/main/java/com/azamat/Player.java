package com.azamat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * Класс сущности PlayerHandler
 */

@Entity
public class Player {
    @Id @GeneratedValue
    private Integer id;

    @NotNull  @Size(min = 5, max = 50)
    @Pattern(regexp = "[a-zA-Z0-9]*")
    private String nickName;

    private String password;

    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    private Integer numberOfGame;

    private Integer numberOfWin;

    @Transient
    private Integer numberOfLose;

    public Player() {}

    public Integer getNumberOfLose() {
        return numberOfLose;
    }

    public void setNumberOfLose(Integer numberOfLose) {
        this.numberOfLose = numberOfLose;
    }

    public Player(String nickName, Integer numberOfGame, Integer numberOfWin) {
        this.nickName = nickName;

        this.numberOfGame = numberOfGame;
        this.numberOfWin = numberOfWin;
    }

    //-------------------------------
    // Getters and Setters
    //-------------------------------

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Integer getNumberOfGame() {
        return numberOfGame;
    }

    public void setNumberOfGame(Integer numberOfGame) {
        this.numberOfGame = numberOfGame;
    }

    public Integer getNumberOfWin() {
        return numberOfWin;
    }

    public void setNumberOfWin(Integer numberOfWin) {
        this.numberOfWin = numberOfWin;
    }
}
