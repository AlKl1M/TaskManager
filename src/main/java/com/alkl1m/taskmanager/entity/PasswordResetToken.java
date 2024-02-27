package com.alkl1m.taskmanager.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.Calendar;
import java.util.Date;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "password_reset_tokens")
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
    public Date getExpiryDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, EXPIRATION_TIME);
        return new Date(calendar.getTime().getTime());
    }
}
