package com.example.workmanagement.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;

import com.example.workmanagement.middleware.FilterUserInfo;
import com.example.workmanagement.model.Session;
import com.example.workmanagement.model.User;
import com.example.workmanagement.repository.SessionRepository;
import com.example.workmanagement.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;

@Service
public class SessionService {
    private final SessionRepository sessionRepo;
    private final UserService userService;
    private final EntityManager entityManager;
    private final FilterUserInfo filterUserInfo;

    public SessionService(SessionRepository sessionRepo, UserService userService, EntityManager entityManager,
            FilterUserInfo filterUserInfo) {
        this.sessionRepo = sessionRepo;
        this.userService = userService;
        this.entityManager = entityManager;
        this.filterUserInfo = filterUserInfo;
    }

    public Session startSession(String email) {
        List<Session> listSession = getWorkingSession(email);

        if (!listSession.isEmpty()) {
            return null;
        }
        User user = userService.findUserByEmail(email);
        if (user == null) {
            return null;
        }
        Session session = new Session(user);
        sessionRepo.save(session);
        filterUserInfo.filterUserInfo(session.getUser());
        return session;

    }

    public Session endSession(String email) {
        List<Session> listSession = getWorkingSession(email);
        if (listSession == null || listSession.size() != 1) {
            return null;
        }
        Session currentSession = listSession.get(0);
        currentSession.setEndTime(new Date());
        sessionRepo.save(currentSession);
        filterUserInfo.filterUserInfo(currentSession.getUser());
        return currentSession;

    }

    private List<Session> getWorkingSession(String email) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Session> cQuery = cb.createQuery(Session.class);
        Root<Session> rootSession = cQuery.from(Session.class);
        Join<Session, User> sessionJoinUser = rootSession.join("user");
        Predicate emailPredicate = cb.equal(sessionJoinUser.get("email"), email);
        Predicate isEndPredicate = cb.isNull(rootSession.get("endTime"));
        cQuery.select(rootSession).where(cb.and(emailPredicate, isEndPredicate));
        List<Session> listSession = entityManager.createQuery(cQuery).getResultList();
        return listSession;
    }

}
