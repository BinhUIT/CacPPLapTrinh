package com.example.workmanagement.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.workmanagement.middleware.FilterUserInfo;

import com.example.workmanagement.model.Session;
import com.example.workmanagement.model.Task;
import com.example.workmanagement.model.User;

import com.example.workmanagement.repository.TaskRepository;
import com.example.workmanagement.response.ReportResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
@Service
public class ReportService {
    private final TaskRepository taskRepo;
    
    private final UserService userService;
    private final EntityManager entityManager;
    private final FilterUserInfo filter;
    public ReportService(TaskRepository taskRepo, UserService userService, EntityManager entityManager, FilterUserInfo filter) {
        this.taskRepo= taskRepo;
        
        this.userService= userService;
        this.entityManager = entityManager;
        this.filter= filter;
    } 
    public ReportResponse reportOneDay(Date reportDate, String email) {
        long currentDateInt = reportDate.getTime();
        long nextDateInt = currentDateInt+24 * 60 * 60 * 1000L;
        Date nextDate = new Date(nextDateInt);
        List<Session> listSession = getSessionInTime(reportDate, nextDate, email);
        
        
        return new ReportResponse(listSession);
        
    }
    public List<Session> getSessionInTime(Date start,Date end, String email) {
        /*long currentDateInt = reportDate.getTime();
        long nextDateInt = currentDateInt+24 * 60 * 60 * 1000L;
        Date nextDate = new Date(nextDateInt);*/
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Session> criteriaQuery= cb.createQuery(Session.class);
        Root<Session> rootSession = criteriaQuery.from(Session.class);
        Join<Session,User> sessionJoinUser = rootSession.join("user");
        Predicate emailPredicate= cb.equal(sessionJoinUser.get("email"), email);
        Predicate startBetweenDate = cb.between(rootSession.get("startTime"),start ,end );
        Predicate endBetweenDate = cb.between(rootSession.get("endTime"),start,end);
        criteriaQuery.select(rootSession).where(cb.and(emailPredicate,startBetweenDate,endBetweenDate));
        return entityManager.createQuery(criteriaQuery).getResultList();

    }
    public List<Task> listCompletedTask(Date start, Date end, String email) 
    {
       CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> criteriaQuery= cb.createQuery(Task.class);
        Root<Task> rootTask= criteriaQuery.from(Task.class);
        Join<Task, User> taskJoinUser = rootTask.join("user"); 
        Predicate emailPredicate = cb.equal(taskJoinUser.get("email"),email);
        Predicate completeBetweenPredicate = cb.between(rootTask.get("completeTime"), start, end);
        criteriaQuery.select(rootTask).where(cb.and(emailPredicate,completeBetweenPredicate));
        return entityManager.createQuery(criteriaQuery).getResultList();
        /*CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Task> criteriaQuery= cb.createQuery(Task.class);
        Root<Task> rootTask = criteriaQuery.from(Task.class);
        Join<Task,Complete> taskJoinComplete = rootTask.join("complete");
        Join<Task, User> taskJoinUser = rootTask.join("user");
        Predicate emailPredicate = cb.equal(taskJoinUser.get("email"),email);
        Predicate completeBetweenPredicate = cb.between(taskJoinComplete.get("completeTime"), start, end);
        criteriaQuery.select(rootTask).where(cb.and(emailPredicate,completeBetweenPredicate));
        return entityManager.createQuery(criteriaQuery).getResultList();*/
    }
    public long getTotalWorkTime(String email) {
        CriteriaBuilder cb= entityManager.getCriteriaBuilder(); 
         CriteriaQuery<Session> criteriaQuery= cb.createQuery(Session.class);
        Root<Session> rootSession = criteriaQuery.from(Session.class);
        Join<Session,User> sessionJoinUser = rootSession.join("user");
        Predicate emailPredicate= cb.equal(sessionJoinUser.get("email"), email);
        criteriaQuery.select(rootSession).where(emailPredicate);
        List<Session> listSession = entityManager.createQuery(criteriaQuery).getResultList();
        long res=0;
        for(Session s:listSession) {
            long totalRes = s.getEndTime().getTime()-s.getStartTime().getTime();
            res+=totalRes;
        } 
        return res;
    }
    public List<Session> getSessionInWeek(String email) {
        Date currentDate= new Date();
        List<Date> dateInWeek = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate); 
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
         for (int i = 0; i < 7; i++) {
            dateInWeek.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return getSessionInTime(dateInWeek.get(0), dateInWeek.get(dateInWeek.size()-1), email);
     }
    
}
