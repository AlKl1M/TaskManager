package com.alkl1m.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.CallableStatement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Date expiryDate;

    private static final int EXPIRATION_TIME = 5;

    public PasswordResetToken(User user, String token){
        this.token = token;
        this.user = user;
        this.expiryDate = this.getExpiryDate();
    }
    public PasswordResetToken(String token){
        super();
        this.token = token;
        this.expiryDate = this.getExpiryDate();
    }
    public PasswordResetToken(User user){
        super();
        this.token = "token";
        this.expiryDate = Date.from(new GregorianCalendar(2022, Calendar.NOVEMBER,2).toInstant());
    }

    public Date getExpiryDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
        return new Date(calendar.getTime().getTime());
    }
    public Date getDate() {
        return this.expiryDate;
    }
}
