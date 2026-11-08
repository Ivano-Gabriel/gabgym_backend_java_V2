package com.gabgym.api.controllers;

import com.gabgym.api.entities.*;
import com.gabgym.api.repositories.*;
import com.gabgym.api.security.AuthorizationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/**
 * Portal de acompanhamento com consentimento. Nunca concede acesso aos endpoints
 * legados de logs/perfil. Todas as leituras sensíveis verificam vínculo ACTIVE.
 */
@RestController
@RequestMapping("/api/personal")
public class PersonalController {
    private final AuthorizationService auth;
    private final UserRepository users;
    private final CoachRelationshipRepository relationships;
    private final CoachEntryRepository entries;
    private final DiaryLogRepository logs;
    private final MetaRepository metas;

    public PersonalController(AuthorizationService auth, UserRepository users,
            CoachRelationshipRepository relationships, CoachEntryRepository entries,
            DiaryLogRepository logs, MetaRepository metas) {
        this.auth=auth;this.users=users;this.relationships=relationships;
        this.entries=entries;this.logs=logs;this.metas=metas;
    }

    public record CoachRequest(String username) {}
    public record Decision(String decision) {}
    public record MessageRequest(String kind, String content) {}
    public record RelationshipView(Long id, String coachUsername, String status) {}
    public record StudentView(Long id, String username, String name, String status) {}
    public record TimelineItem(Long id, String kind, String content, String status,
                               boolean mine, Instant createdAt) {}
    public record LogView(String type, String name, Integer calories, Integer volume) {}
    public record MetaView(String title, String status) {}
    public record DaySummary(Long studentId, String username, String name, LocalDate date,
                             int calories, int waterMl, int exercises,
                             List<LogView> logs, List<MetaView> goals) {}

    private User requireStudent() {
        User user=auth.currentUser();
        if(user.getRole()!=UserRole.ALUNO)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Área exclusiva de aluno");
        return user;
    }
    private User requireCoach() {
        User user=auth.currentUser();
        if(user.getRole()!=UserRole.PERSONAL)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Área exclusiva de personal");
        return user;
    }
    private CoachRelationship active(Long studentId) {
        User actor=auth.currentUser();
        CoachRelationship rel=relationships.findByStudentId(studentId)
            .filter(link -> "ACTIVE".equals(link.getStatus()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,"Vínculo não autorizado"));
        boolean permitted=(actor.getRole()==UserRole.ALUNO && actor.getId().equals(studentId))
            ||(actor.getRole()==UserRole.PERSONAL && actor.getId().equals(rel.getCoachId()));
        if(!permitted)throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Vínculo não autorizado");
        return rel;
    }

    @GetMapping("/student/connection")
    public RelationshipView studentConnection() {
        User student=requireStudent();
        return relationships.findByStudentId(student.getId()).map(link -> {
            String username=users.findById(link.getCoachId()).map(User::getUsername).orElse("indisponível");
            return new RelationshipView(link.getId(),username,link.getStatus());
        }).orElse(new RelationshipView(null,null,"NONE"));
    }

    @Transactional
    @PostMapping("/student/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RelationshipView request(@RequestBody CoachRequest input) {
        User student=requireStudent();
        String username=input==null?null:input.username();
        if(username==null || !username.matches("[a-zA-Z0-9_.-]{3,40}"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Informe o usuário do personal");
        User coach=users.findByUsername(username)
            .filter(u->u.isActive() && u.getRole()==UserRole.PERSONAL)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Personal indisponível"));
        Optional<CoachRelationship> previous=relationships.findByStudentId(student.getId());
        if(previous.isPresent() && ("ACTIVE".equals(previous.get().getStatus())
                                    || "PENDING".equals(previous.get().getStatus())))
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Já existe uma solicitação ou vínculo");
        CoachRelationship rel=previous.orElseGet(()->{
            CoachRelationship created=new CoachRelationship();
            created.setStudentId(student.getId());
            return created;
        });
        // Uma nova autorização nunca herda mensagens e anotações do vínculo anterior.
        if(previous.isPresent()) entries.deleteByRelationshipId(rel.getId());
        rel.setCoachId(coach.getId());
        rel.setStatus("PENDING");
        relationships.save(rel);
        return new RelationshipView(rel.getId(),coach.getUsername(),"PENDING");
    }

    @Transactional
    @DeleteMapping("/student/connection")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revoke() {
        User student=requireStudent();
        relationships.findByStudentId(student.getId()).ifPresent(link->{
            link.setStatus("REVOKED");
            relationships.save(link);
        });
    }

    @GetMapping("/coach/students")
    public List<StudentView> students() {
        User coach=requireCoach();
        return relationships.findByCoachIdOrderByCreatedAtDesc(coach.getId()).stream()
            .filter(link -> "ACTIVE".equals(link.getStatus())||"PENDING".equals(link.getStatus()))
            .map(link->users.findById(link.getStudentId())
                .map(u -> new StudentView(u.getId(),u.getUsername(),u.getName(),link.getStatus()))
                .orElse(null))
            .filter(Objects::nonNull).toList();
    }

    @Transactional
    @PostMapping("/coach/requests/{studentId}/decision")
    public StudentView decideRequest(@PathVariable Long studentId,@RequestBody Decision input) {
        User coach=requireCoach();
        if(input==null || !List.of("ACCEPT","REJECT").contains(input.decision()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Decisão inválida");
        CoachRelationship link=relationships.findByStudentId(studentId)
            .filter(x->x.getCoachId().equals(coach.getId()) && "PENDING".equals(x.getStatus()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,"Solicitação não autorizada"));
        link.setStatus("ACCEPT".equals(input.decision())?"ACTIVE":"REJECTED");
        relationships.save(link);
        User student=users.findById(studentId).orElseThrow();
        return new StudentView(student.getId(),student.getUsername(),student.getName(),link.getStatus());
    }

    @GetMapping("/coach/students/{studentId}/day")
    public DaySummary day(@PathVariable Long studentId,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date) {
        requireCoach();
        active(studentId);
        LocalDate requested=date==null?LocalDate.now():date;
        if(requested.isBefore(LocalDate.now().minusYears(2))||requested.isAfter(LocalDate.now().plusDays(1)))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Data fora do intervalo");
        User student=users.findById(studentId).orElseThrow();
        List<DiaryLog> items=logs.findByUserIdAndLogDate(studentId,requested);
        int water=items.stream().filter(x->"water".equals(x.getType()))
            .mapToInt(x->x.getVolume()==null?0:Math.max(0,x.getVolume())).sum();
        int calories=items.stream().filter(x->"food".equals(x.getType()))
            .mapToInt(x->x.getCalories()==null?0:Math.max(0,x.getCalories())).sum();
        int exercise=(int)items.stream().filter(x->"exercise".equals(x.getType())||"cardio".equals(x.getType())).count();
        List<LogView> details=items.stream().limit(100)
            .map(x->new LogView(x.getType(),x.getName(),x.getCalories(),x.getVolume())).toList();
        List<MetaView> goals=metas.findByUserId(studentId).stream().limit(30)
            .map(x->new MetaView(x.getTitulo(),x.getStatus())).toList();
        return new DaySummary(studentId,student.getUsername(),student.getName(),requested,
                              calories,water,exercise,details,goals);
    }

    @GetMapping("/students/{studentId}/timeline")
    public List<TimelineItem> timeline(@PathVariable Long studentId) {
        CoachRelationship rel=active(studentId);
        User actor=auth.currentUser();
        return entries.findTop100ByRelationshipIdOrderByCreatedAtDesc(rel.getId()).stream()
            .filter(e->!"NOTE".equals(e.getKind())||actor.getRole()==UserRole.PERSONAL)
            .map(e->view(e,actor)).toList();
    }

    @Transactional
    @PostMapping("/students/{studentId}/timeline")
    @ResponseStatus(HttpStatus.CREATED)
    public TimelineItem postEntry(@PathVariable Long studentId,@RequestBody MessageRequest input) {
        CoachRelationship rel=active(studentId);
        User actor=auth.currentUser();
        if(input==null || input.kind()==null || input.content()==null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Mensagem inválida");
        String kind=input.kind().trim().toUpperCase(Locale.ROOT),content=input.content().trim();
        if(!List.of("MESSAGE","ALERT","PLAN","NOTE").contains(kind)||content.isBlank()||content.length()>2000)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Conteúdo inválido (1 a 2000 caracteres)");
        if(actor.getRole()==UserRole.ALUNO && !"MESSAGE".equals(kind))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Apenas o personal pode propor planos, notas ou alertas");
        CoachEntry entry=new CoachEntry();
        entry.setRelationshipId(rel.getId());
        entry.setAuthorId(actor.getId());
        entry.setKind(kind);
        entry.setContent(content);
        entry.setStatus("PLAN".equals(kind)?"PENDING":"NONE");
        return view(entries.save(entry),actor);
    }

    @Transactional
    @PostMapping("/students/{studentId}/plans/{entryId}/decision")
    public TimelineItem decidePlan(@PathVariable Long studentId,@PathVariable Long entryId,
                                   @RequestBody Decision input) {
        User actor=requireStudent();
        if(!actor.getId().equals(studentId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,"Acesso negado");
        CoachRelationship rel=active(studentId);
        if(input==null||!List.of("APPROVE","REJECT").contains(input.decision()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Decisão inválida");
        CoachEntry entry=entries.findByIdAndRelationshipId(entryId,rel.getId())
            .filter(x->"PLAN".equals(x.getKind()) && "PENDING".equals(x.getStatus())
                && x.getAuthorId().equals(rel.getCoachId()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,"Proposta indisponível"));
        entry.setStatus("APPROVE".equals(input.decision())?"APPROVED":"REJECTED");
        return view(entries.save(entry),actor);
    }

    private TimelineItem view(CoachEntry entry,User actor) {
        return new TimelineItem(entry.getId(),entry.getKind(),entry.getContent(),entry.getStatus(),
            actor.getId().equals(entry.getAuthorId()),entry.getCreatedAt());
    }
}
