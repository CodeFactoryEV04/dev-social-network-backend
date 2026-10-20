package com.codefactory.dev_social_network.usuarios.entity;

import java.util.UUID;
import java.time.LocalDate;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;

@Entity
@Table (name = "user_profiles")
public class UserProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn (name = "user_id", referencedColumnName = "id", nullable = false)
    private Usuario userId;
    
    @Column (name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column (name = "bio", unique = false, nullable = true, length = 500)
    private String bio;

    @Column (name = "completed_profile", unique = false, nullable = false)
    private double completedProfile;

    @Column (name = "porfolio_link", unique = true, nullable = true)
    private String porfolioLink;

    @Column (name = "dev_initial_date", unique = true, nullable = true)
    private LocalDate devInitialDate;

    @Column (name = "Seniority_level", unique = false, nullable = true)
    @Enumerated(EnumType.STRING)
    private ExperienceLevel seniorityLevel;

    @Column (name = "years_of_experience", nullable = true)
    private Integer yearsOfExperience;

    protected UserProfileEntity() {}

    public UserProfileEntity(Usuario userId){
        this.userId = userId;
        this.createdAt = LocalDate.now();
        this.completedProfile = 0;
    }

    public UUID getId() {
        return id;
    }

    public Usuario getUserId() {
        return userId;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public double getCompletedProfile() {
        return completedProfile;
    }

    public void setCompletedProfile(double completedProfile) {
        this.completedProfile = completedProfile;
    }

    public String getPorfolioLink() {
        return porfolioLink;
    }

    public void setPorfolioLink(String porfolioLink) {
        this.porfolioLink = porfolioLink;
    }

    public LocalDate getDevInitialDate() {
        return devInitialDate;
    }

    public void setDevInitialDate(LocalDate devInitialDate) {
        this.devInitialDate = devInitialDate;
    }

    public ExperienceLevel getSeniorityLevel() {
        return seniorityLevel;
    }

    public void setSeniorityLevel(ExperienceLevel seniorityLevel) {
        this.seniorityLevel = seniorityLevel;
    }

    public Integer getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(Integer yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
}
