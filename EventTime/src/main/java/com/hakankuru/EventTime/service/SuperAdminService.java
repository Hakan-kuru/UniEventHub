package com.hakankuru.EventTime.service;

import com.hakankuru.EventTime.dto.AdminResponse;
import com.hakankuru.EventTime.dto.AdminUserDto;
import com.hakankuru.EventTime.dto.AssignAdminRequest;
import com.hakankuru.EventTime.entity.GlobalRole;
import com.hakankuru.EventTime.entity.University;
import com.hakankuru.EventTime.entity.User;
import com.hakankuru.EventTime.entity.UserRole;
import com.hakankuru.EventTime.repository.UniversityRepository;
import com.hakankuru.EventTime.repository.UserRepository;
import com.hakankuru.EventTime.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SuperAdminService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UniversityRepository universityRepository;

    /**
     * Email ile kullaniciyi bulur, ADMIN rol� atar ve university_id baglar.
     * Kural: Ayni user birden fazla ADMIN olamaz � varsa override edilir.
     */
    @Transactional
    public AdminResponse assignAdminByEmail(AssignAdminRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found: " + request.getEmail()));

        University university = universityRepository.findById(request.getUniversityId())
                .orElseThrow(() -> new RuntimeException("University not found: " + request.getUniversityId()));

        // Var olan ADMIN rolunu bul veya yeni olustur
        Optional<UserRole> existing = userRoleRepository
                .findByUser_UserIdAndRole(user.getUserId(), GlobalRole.ADMIN.name());

        UserRole userRole = existing.orElseGet(() -> {
            UserRole r = new UserRole();
            r.setUser(user);
            r.setRole(GlobalRole.ADMIN.name());
            return r;
        });

        userRole.setUniversity(university);
        userRoleRepository.save(userRole);

        // User entity'sindeki globalRole'u da guncelle
        user.setGlobalRole(GlobalRole.ADMIN);
        userRepository.save(user);

        return new AdminResponse(
                user.getUserId(),
                user.getEmail(),
                GlobalRole.ADMIN.name(),
                university.getUniversityId()
        );
    }

    /**
     * Admin rolunu kaldirir: UserRole satirini siler, globalRole'u USER'a cevirir.
     */
    @Transactional
    public void removeAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        Optional<UserRole> adminRole = userRoleRepository
                .findByUser_UserIdAndRole(userId, GlobalRole.ADMIN.name());

        adminRole.ifPresent(userRoleRepository::delete);

        user.setGlobalRole(GlobalRole.USER);
        userRepository.save(user);
    }

    /**
     * Tum ADMIN rolundeki kullanicilari listeler.
     */
    public List<AdminResponse> getAllAdmins() {
        return userRoleRepository.findByRole(GlobalRole.ADMIN.name())
                .stream()
                .map(ur -> new AdminResponse(
                        ur.getUser().getUserId(),
                        ur.getUser().getEmail(),
                        ur.getRole(),
                        ur.getUniversity() != null ? ur.getUniversity().getUniversityId() : null
                ))
                .collect(Collectors.toList());
    }

    /**
     * Sistemdeki tum kullanicilari AdminUserDto olarak doner.
     * universityId: ADMIN ise users_roles'dan, diger roller icin null.
     */
    public List<AdminUserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> {
                    Long universityId = null;
                    if (user.getGlobalRole() == GlobalRole.ADMIN) {
                        Optional<UserRole> adminRole = userRoleRepository
                                .findByUser_UserIdAndRole(user.getUserId(), GlobalRole.ADMIN.name());
                        universityId = adminRole
                                .map(r -> r.getUniversity() != null ? r.getUniversity().getUniversityId() : null)
                                .orElse(null);
                    }
                    return new AdminUserDto(
                            user.getUserId(),
                            user.getName(),
                            user.getEmail(),
                            user.getGlobalRole() != null ? user.getGlobalRole().name() : "USER",
                            universityId
                    );
                })
                .collect(Collectors.toList());
    }
}
